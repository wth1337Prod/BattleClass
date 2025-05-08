package me.wth.battleClass.drones;

import me.wth.battleClass.BattleClass;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Класс американского боевого дрона "Predator"
 */
public class PredatorBattleDrone extends AbstractDrone {
    
    private final Map<UUID, Long> lastFireTime = new HashMap<>();
    
    private final long weaponCooldown = 3000; 
    
    /**
     * Конструктор для создания боевого дрона "Predator"
     *
     * @param plugin экземпляр плагина
     */
    public PredatorBattleDrone(BattleClass plugin) {
        super(
            plugin,
            "predator_battle_drone", 
            "Боевой дрон \"Predator MQ-1\"", 
            createDescription(),     
            Material.FIREWORK_STAR,  
            200,                     
            400,                     
            3.5,                     
            800,                     
            8.0,                     
            0,                       
            0.0,                     
            0.0,                     
            "AGM-114 Hellfire II"    
        );
    }
    
    /**
     * Создает список строк с описанием дрона
     *
     * @return список строк описания
     */
    private static List<String> createDescription() {
        return Arrays.asList(
            "§7Американский разведывательно-ударный",
            "§7БПЛА с ракетным вооружением.",
            "§7Применяется для наблюдения",
            "§7и высокоточных ударов по целям."
        );
    }
    
    /**
     * Выстрел ракетой из дрона по цели
     * 
     * @param player игрок-оператор дрона
     * @param target целевое местоположение
     * @return true, если выстрел выполнен успешно
     */
    public boolean fireMissile(Player player, Location target) {
        UUID playerUUID = player.getUniqueId();
        
        if (!hasActiveDrone(playerUUID)) {
            player.sendMessage(ChatColor.RED + "У вас нет активного дрона.");
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long lastFire = lastFireTime.getOrDefault(playerUUID, 0L);
        
        if (currentTime - lastFire < weaponCooldown) {
            long remainingCooldown = (weaponCooldown - (currentTime - lastFire)) / 1000 + 1;
            player.sendMessage(ChatColor.RED + "Перезарядка ракеты: " + remainingCooldown + " сек.");
            return false;
        }
        
        ArmorStand drone = getDroneEntity(playerUUID);
        if (drone == null) {
            return false;
        }
        
        launchMissile(player, drone.getLocation(), target);
        
        lastFireTime.put(playerUUID, currentTime);
        
        return true;
    }
    
    /**
     * Создает и запускает ракету из дрона в указанную точку
     * 
     * @param player игрок-оператор дрона
     * @param from точка запуска ракеты
     * @param to целевая точка
     */
    private void launchMissile(Player player, Location from, Location to) {
        World world = from.getWorld();
        
        world.playSound(from, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 0.8f);
        world.spawnParticle(Particle.SMOKE, from, 20, 0.1, 0.1, 0.1, 0.1);
        
        player.sendMessage(ChatColor.GREEN + "Ракета AGM-114 Hellfire II запущена!");
        
        Vector direction = to.toVector().subtract(from.toVector()).normalize();
        
        ArmorStand missile = (ArmorStand) world.spawnEntity(from, org.bukkit.entity.EntityType.ARMOR_STAND);
        missile.setVisible(false);
        missile.setSmall(true);
        missile.setInvulnerable(true);
        missile.setGravity(false);
        missile.setCustomName(ChatColor.RED + "AGM-114");
        missile.setCustomNameVisible(true);
        
        missile.getEquipment().setHelmet(new org.bukkit.inventory.ItemStack(Material.TNT));
        
        new BukkitRunnable() {
            private int ticks = 0;
            private final int maxTicks = 100; 
            private final Location destination = to.clone();
            private Location current = from.clone();
            
            @Override
            public void run() {
                ticks++;
                
                if (ticks >= maxTicks || current.distance(destination) < 1.0 || missile.isDead()) {
                    explodeMissile(player, missile.getLocation());
                    
                    missile.remove();
                    
                    cancel();
                    return;
                }
                
                Vector newDirection = destination.toVector().subtract(current.toVector()).normalize();
                
                double speed = 1.0; 
                newDirection.multiply(speed);
                
                current.add(newDirection);
                missile.teleport(current);
                
                world.spawnParticle(Particle.FLAME, current, 1, 0.0, 0.0, 0.0, 0.01);
                world.spawnParticle(Particle.SMOKE, current, 3, 0.05, 0.05, 0.05, 0.01);
                
                if (ticks % 5 == 0) {
                    world.playSound(current, Sound.ENTITY_PHANTOM_AMBIENT, 0.3f, 2.0f);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    /**
     * Создает взрыв ракеты в указанной локации
     * 
     * @param player игрок, выпустивший ракету
     * @param location место взрыва
     */
    private void explodeMissile(Player player, Location location) {
        World world = location.getWorld();
        
        double explosionRadius = 6.0;
        
        double baseDamage = 15.0;
        
        world.spawnParticle(Particle.EXPLOSION, location, 1, 0.0, 0.0, 0.0, 0.0);
        world.spawnParticle(Particle.EXPLOSION, location, 10, 1.0, 1.0, 1.0, 0.1);
        world.spawnParticle(Particle.FLAME, location, 30, 1.5, 1.5, 1.5, 0.1);
        world.spawnParticle(Particle.LARGE_SMOKE, location, 20, 1.0, 1.0, 1.0, 0.1);
        
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        
        List<Entity> nearbyEntities = world.getNearbyEntities(location, explosionRadius, explosionRadius, explosionRadius)
            .stream()
            .filter(entity -> entity instanceof LivingEntity && !(entity instanceof ArmorStand) && !entity.equals(player))
            .toList();
        
        for (Entity entity : nearbyEntities) {
            LivingEntity living = (LivingEntity) entity;
            
            double distance = location.distance(living.getLocation());
            
            if (distance <= explosionRadius) {
                double damage = baseDamage * (1 - (distance / explosionRadius));
                
                living.damage(damage, player);
                
                Vector knockback = living.getLocation().toVector()
                        .subtract(location.toVector())
                        .normalize()
                        .multiply(1.5); 
                knockback.setY(0.5); 
                
                living.setVelocity(living.getVelocity().add(knockback));
            }
        }
    }
} 