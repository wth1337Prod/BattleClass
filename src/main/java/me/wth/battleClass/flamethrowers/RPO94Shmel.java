package me.wth.battleClass.flamethrowers;

import me.wth.battleClass.BattleClass;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
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

/**
 * Класс представляющий российский реактивный пехотный огнемет РПО-94 "Шмель"
 */
public class RPO94Shmel extends AbstractFlamethrower {
    
    /**
     * Создает новый экземпляр огнемета РПО-94 "Шмель"
     * 
     * @param plugin экземпляр основного плагина
     */
    public RPO94Shmel(BattleClass plugin) {
        super(
            plugin,
            "rpo94_shmel",
            ChatColor.RED + "РПО-94 \"Шмель\"",
            createDescription(),
            Material.BLAZE_ROD, 
            18,    
            8.0,   
            3.0,   
            10,    
            100,   
            4.0,   
            8      
        );
    }
    
    /**
     * Создает описание для огнемета
     * 
     * @return список строк с описанием
     */
    private static List<String> createDescription() {
        return Arrays.asList(
            ChatColor.GRAY + "Российский реактивный пехотный огнемет",
            ChatColor.GRAY + "РПО-94 \"Шмель\" - является одним из",
            ChatColor.GRAY + "мощнейших огнеметов в мире",
            ChatColor.GRAY + "Введен в эксплуатацию в 1994 году",
            ChatColor.GRAY + "Дальность стрельбы: до 800 м",
            ChatColor.GRAY + "Дальность огнеметного действия: до 18 м",
            ChatColor.GRAY + "Температура горения: 800-1000°C",
            ChatColor.RED + "Используется только войсками РФ"
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
            
            meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
            
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            
            meta.setUnbreakable(true);
            
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "flamethrower_id"), PersistentDataType.STRING, id);
            container.set(new NamespacedKey(plugin, "flamethrower_model"), PersistentDataType.STRING, "shmel");
            
            itemStack.setItemMeta(meta);
        }
        
        return itemStack;
    }
    
    /**
     * Переопределяем метод создания эффекта пламени для более мощного огня
     */
    @Override
    protected void createFlameEffect(Player player) {
        World world = player.getWorld();
        Location startLocation = player.getEyeLocation();
        Vector direction = startLocation.getDirection().normalize();
        
        for (double i = 0.5; i < range; i += 0.4) { 
            Location currentLocation = startLocation.clone().add(direction.clone().multiply(i));
            
            double spreadFactor = i / 4.0; 
            
            for (int j = 0; j < 3; j++) { 
                double offsetX = (Math.random() - 0.5) * spreadFactor;
                double offsetY = (Math.random() - 0.5) * spreadFactor;
                double offsetZ = (Math.random() - 0.5) * spreadFactor;
                
                world.spawnParticle(
                    Particle.FLAME,
                    currentLocation.clone().add(offsetX, offsetY, offsetZ),
                    1, 0.0, 0.0, 0.0, 0.02
                );
                
                if (Math.random() < 0.2) {
                    world.spawnParticle(
                        Particle.LAVA,
                        currentLocation.clone().add(offsetX, offsetY, offsetZ),
                        1, 0.0, 0.0, 0.0, 0.0
                    );
                }
            }
            
            if (Math.random() < 0.4) {
                world.spawnParticle(
                    Particle.LARGE_SMOKE,
                    currentLocation.clone().add((Math.random() - 0.5) * spreadFactor, 
                                                (Math.random() - 0.5) * spreadFactor, 
                                                (Math.random() - 0.5) * spreadFactor),
                    1, 0.0, 0.0, 0.0, 0.01
                );
            }
            
            if (Math.random() < 0.08 && currentLocation.getBlock().getType().isSolid()) {
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
} 