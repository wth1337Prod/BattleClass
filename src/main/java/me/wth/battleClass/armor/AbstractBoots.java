package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный класс для военных ботинок (берцев)
 */
public abstract class AbstractBoots implements Boots {
    protected final BattleClass plugin;
    protected final String id;
    protected final String displayName;
    protected final int protectionLevel;
    protected final double speedBonus;
    
    /**
     * Конструктор для ботинок
     * 
     * @param plugin экземпляр главного класса плагина
     * @param id идентификатор ботинок
     * @param displayName название для отображения
     * @param protectionLevel уровень защиты
     * @param speedBonus бонус к скорости
     */
    public AbstractBoots(BattleClass plugin, String id, String displayName, int protectionLevel, double speedBonus) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.protectionLevel = protectionLevel;
        this.speedBonus = speedBonus;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public int getProtectionLevel() {
        return protectionLevel;
    }
    
    @Override
    public double getSpeedBonus() {
        return speedBonus;
    }
    
    @Override
    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(Material.IRON_BOOTS);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6" + displayName);
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Тип: §fБоевые ботинки");
            lore.add("§7Защита: §f" + protectionLevel);
            
            if (speedBonus > 0) {
                lore.add("§7Бонус скорости: §a+" + speedBonus);
            } else if (speedBonus < 0) {
                lore.add("§7Бонус скорости: §c" + speedBonus);
            }
            
            lore.add("§8Идентификатор: §7" + id);
            
            meta.setLore(lore);
            
            if (plugin.getConfig().getBoolean("custom_models_enabled", false)) {
                meta.setCustomModelData(getCustomModelData());
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Получает ID кастомной модели для предмета
     * 
     * @return ID кастомной модели
     */
    protected abstract int getCustomModelData();
} 