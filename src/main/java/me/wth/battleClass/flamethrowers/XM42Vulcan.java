package me.wth.battleClass.flamethrowers;

import me.wth.battleClass.BattleClass;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

/**
 * Класс представляющий американский тактический огнемет XM42 Vulcan
 */
public class XM42Vulcan extends AbstractFlamethrower {
    
    /**
     * Создает новый экземпляр огнемета XM42 Vulcan
     * 
     * @param plugin экземпляр основного плагина
     */
    public XM42Vulcan(BattleClass plugin) {
        super(
            plugin,
            "xm42_vulcan",
            ChatColor.BLUE + "XM42 Vulcan",
            createDescription(),
            Material.LIGHTNING_ROD, 
            15,    
            5.0,   
            2.5,   
            7,     
            150,   
            2.5,   
            6      
        );
    }
    
    /**
     * Создает описание для огнемета
     * 
     * @return список строк с описанием
     */
    private static List<String> createDescription() {
        return Arrays.asList(
            ChatColor.GRAY + "Американский тактический огнемет",
            ChatColor.GRAY + "XM42 Vulcan - первый коммерческий",
            ChatColor.GRAY + "портативный огнемет на жидком топливе",
            ChatColor.GRAY + "Разработан в 2018 году",
            ChatColor.GRAY + "Дальность огнеметания: до 15 м",
            ChatColor.GRAY + "Время работы на одной заправке: около 60 сек",
            ChatColor.GRAY + "Превосходная управляемость и мобильность",
            ChatColor.BLUE + "Используется только войсками США"
        );
    }
    
    /**
     * Переопределяем метод создания ItemStack для добавления дополнительных 
     * визуальных эффектов
     */
    @Override
    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(displayName);
            
            List<String> lore = new ArrayList<>();
            lore.addAll(description);
            lore.add("");
            lore.add("§7Дальность: §f" + range + " блоков");
            lore.add("§7Урон: §f" + damagePerSecond + "/сек");
            lore.add("§7Радиус действия: §f" + radius + " блоков");
            lore.add("§7Длительность горения: §f" + burnDuration + " сек.");
            lore.add("§7Емкость бака: §f" + fuelCapacity + " единиц");
            lore.add("§7Расход топлива: §f" + fuelConsumption + " ед./сек");
            lore.add("§7Вес: §f" + weight + "/10");
            lore.add("");
            lore.add("§eПКМ - выстрел из огнемета");
            lore.add("§eШифт+ПКМ - перезаправка (нужна канистра)");
            
            meta.setLore(lore);
            
            meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
            
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            
            meta.setUnbreakable(true);
            
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "flamethrower_id"), PersistentDataType.STRING, id);
            container.set(new NamespacedKey(plugin, "flamethrower_model"), PersistentDataType.STRING, "vulcan");
            
            itemStack.setItemMeta(meta);
        }
        
        return itemStack;
    }
    
    /**
     * Переопределяем метод создания эффекта пламени для более точного и контролируемого огня
     */
    @Override
    protected void createFlameEffect(Player player) {
        World world = player.getWorld();
        Location startLocation = player.getEyeLocation();
        Vector direction = startLocation.getDirection().normalize();
        
        long currentTime = System.currentTimeMillis();
        UUID playerUUID = player.getUniqueId();
        Long lastTime = lastFireSound.get(playerUUID);
        
        if (lastTime == null || currentTime - lastTime > 1000) {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.5f);
            lastFireSound.put(playerUUID, currentTime);
        }
        
        for (double i = 0.5; i < range; i += 0.45) {
            Location currentLocation = startLocation.clone().add(direction.clone().multiply(i));
            
            double spreadFactor = i / 6.0; 
            
            for (int j = 0; j < 2; j++) { 
                double offsetX = (Math.random() - 0.5) * spreadFactor;
                double offsetY = (Math.random() - 0.5) * spreadFactor;
                double offsetZ = (Math.random() - 0.5) * spreadFactor;
                
                world.spawnParticle(
                    Particle.FLAME,
                    currentLocation.clone().add(offsetX, offsetY, offsetZ),
                    1, 0.0, 0.0, 0.0, 0.01
                );
            }
            
            if (Math.random() < 0.2) {
                world.spawnParticle(
                    Particle.SMOKE,
                    currentLocation.clone().add((Math.random() - 0.5) * spreadFactor, 
                                               (Math.random() - 0.5) * spreadFactor, 
                                               (Math.random() - 0.5) * spreadFactor),
                    1, 0.0, 0.0, 0.0, 0.01
                );
            }
            
            if (Math.random() < 0.06 && currentLocation.getBlock().getType().isSolid()) {
                Location fireLocation = currentLocation.clone();
                fireLocation.setY(fireLocation.getY() + 1.0);
                
                if (fireLocation.getBlock().getType() == Material.AIR) {
                    fireLocation.getBlock().setType(Material.FIRE);
                    
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (fireLocation.getBlock().getType() == Material.FIRE) {
                            fireLocation.getBlock().setType(Material.AIR);
                        }
                    }, burnDuration * 20L);
                }
            }
        }
    }
    
    private static final Map<UUID, Long> lastFireSound = new HashMap<>();
} 