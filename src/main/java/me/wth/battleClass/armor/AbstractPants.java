package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный класс для камуфляжных штанов
 */
public abstract class AbstractPants implements Pants {
    protected final BattleClass plugin;
    protected final String id;
    protected final String displayName;
    protected final int protectionLevel;
    protected final double camouflageLevel;
    
    /**
     * Конструктор для штанов
     * 
     * @param plugin экземпляр главного класса плагина
     * @param id идентификатор штанов
     * @param displayName название для отображения
     * @param protectionLevel уровень защиты
     * @param camouflageLevel уровень маскировки
     */
    public AbstractPants(BattleClass plugin, String id, String displayName, int protectionLevel, double camouflageLevel) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.protectionLevel = protectionLevel;
        this.camouflageLevel = camouflageLevel;
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
    public double getCamouflageLevel() {
        return camouflageLevel;
    }
    
    @Override
    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6" + displayName);
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Тип: §fКамуфляжные штаны");
            lore.add("§7Защита: §f" + protectionLevel);
            
            lore.add("§7Маскировка: §f" + (int)(camouflageLevel * 100) + "%");
            
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