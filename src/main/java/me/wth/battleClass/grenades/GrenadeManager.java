package me.wth.battleClass.grenades;

import me.wth.battleClass.BattleClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GrenadeManager {
    private final BattleClass plugin;
    private final Map<String, Grenade> grenades;
    private final Map<UUID, GrenadeTask> activeGrenades;
    
    public GrenadeManager(BattleClass plugin) {
        this.plugin = plugin;
        this.grenades = new HashMap<>();
        this.activeGrenades = new HashMap<>();
        
        registerGrenades();
    }
    
    private void registerGrenades() {
        M67Grenade m67Grenade = new M67Grenade();
        grenades.put(m67Grenade.getId(), m67Grenade);
        
        RGDGrenade rgdGrenade = new RGDGrenade();
        grenades.put(rgdGrenade.getId(), rgdGrenade);
    }
    
    public Map<String, Grenade> getGrenades() {
        return grenades;
    }
    
    public Grenade getGrenade(String id) {
        return grenades.get(id);
    }
    
    public void giveGrenadeToPlayer(Player player, String grenadeId, int amount) {
        Grenade grenade = grenades.get(grenadeId);
        
        if (grenade == null) {
            player.sendMessage("§cГраната " + grenadeId + " не найдена!");
            return;
        }
        
        if (amount < 1) amount = 1;
        if (amount > 64) amount = 64;
        
        ItemStack grenadeItem = grenade.createItemStack();
        grenadeItem.setAmount(amount);
        
        player.getInventory().addItem(grenadeItem);
        player.sendMessage("§aВы получили §f" + grenade.getDisplayName() + " §ax" + amount);
    }
    
    public void throwGrenade(Player player, Grenade grenade) {
        World world = player.getWorld();
        Location startLocation = player.getEyeLocation();
        
        ItemStack grenadeItemStack = new ItemStack(Material.FIREWORK_STAR);
        
        Item grenadeItem = world.dropItem(startLocation, grenadeItemStack);
        
        grenadeItem.setPickupDelay(Integer.MAX_VALUE);
        
        Vector direction = player.getLocation().getDirection().normalize().multiply(1.2);
        
        direction.add(new Vector(0, 0.2, 0)); 
        
        grenadeItem.setVelocity(direction);
        
        GrenadeTask task = new GrenadeTask(plugin, grenadeItem, grenade);
        task.runTaskTimer(plugin, 1L, 1L);
        
        activeGrenades.put(grenadeItem.getUniqueId(), task);
        
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getAmount() > 1) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
        
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_SNOWBALL_THROW, 1.0f, 0.5f);
    }
    
    private class GrenadeTask extends BukkitRunnable {
        private final BattleClass plugin;
        private final Item grenadeItem;
        private final Grenade grenade;
        private int ticksLived = 0;
        
        public GrenadeTask(BattleClass plugin, Item grenadeItem, Grenade grenade) {
            this.plugin = plugin;
            this.grenadeItem = grenadeItem;
            this.grenade = grenade;
        }
        
        @Override
        public void run() {
            ticksLived++;
            
            if (grenadeItem == null || !grenadeItem.isValid()) {
                cancel();
                return;
            }
            
            if (ticksLived > grenade.getExplosionDelay() / 2) {
                grenadeItem.getWorld().spawnParticle(
                    Particle.SMOKE,
                    grenadeItem.getLocation(), 
                    1, 0.05, 0.05, 0.05, 0.01
                );
            }
            
            if (ticksLived >= grenade.getExplosionDelay()) {
                explode();
                cancel();
                return;
            }
        }
        
        private void explode() {
            Location location = grenadeItem.getLocation();
            World world = location.getWorld();
            
            grenadeItem.remove();
            activeGrenades.remove(grenadeItem.getUniqueId());
            
            world.createExplosion(location, 0F, false, false);
            
            world.spawnParticle(Particle.EXPLOSION, location, 1);
            world.spawnParticle(Particle.LARGE_SMOKE, location, 20, 0.5, 0.5, 0.5, 0.1);
            
            world.playSound(location, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1.0f);
            
            double radius = grenade.getBlastRadius();
            double damage = grenade.getDamage();
            
            for (Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
                if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    
                    double distance = location.distance(livingEntity.getLocation());
                    
                    double scaledDamage = damage * (1 - distance / radius);
                    
                    if (scaledDamage > 0) {
                        livingEntity.damage(scaledDamage);
                        
                        if (livingEntity instanceof Player) {
                            Vector knockback = livingEntity.getLocation().toVector()
                                    .subtract(location.toVector())
                                    .normalize()
                                    .multiply(2.0); 
                            
                            livingEntity.setVelocity(knockback);
                        }
                    }
                }
            }
        }
    }
    
    public boolean isGrenade(ItemStack item) {
        if (item == null || item.getType() != Material.FIREWORK_STAR) {
            return false;
        }
        
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        
        for (Grenade grenade : grenades.values()) {
            if (displayName.equals(grenade.getDisplayName())) {
                return true;
            }
        }
        
        return false;
    }
    
    public Grenade getGrenadeFromItem(ItemStack item) {
        if (!isGrenade(item)) {
            return null;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        
        for (Grenade grenade : grenades.values()) {
            if (displayName.equals(grenade.getDisplayName())) {
                return grenade;
            }
        }
        
        return null;
    }
    
    /**
     * Получает список идентификаторов всех зарегистрированных гранат
     * 
     * @return список идентификаторов гранат
     */
    public List<String> getGrenadeIds() {
        return new ArrayList<>(grenades.keySet());
    }
} 