package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Базовый абстрактный класс для бронежилетов
 */
public abstract class AbstractArmor implements Armor {
    protected final BattleClass plugin;
    protected final String id;
    protected final String displayName;
    protected final Material material;
    protected final double protectionLevel;
    protected final double armorPiercingProtection;
    protected final int durability;
    
    /**
     * Конструктор абстрактного бронежилета
     * 
     * @param plugin экземпляр главного класса плагина
     * @param id уникальный идентификатор
     * @param displayName отображаемое имя
     * @param material материал для создания предмета
     * @param protectionLevel базовый уровень защиты (от 0.0 до 1.0)
     * @param armorPiercingProtection защита от бронебойных патронов (от 0.0 до 1.0)
     * @param durability прочность бронежилета
     */
    public AbstractArmor(BattleClass plugin, String id, String displayName, Material material, 
                        double protectionLevel, double armorPiercingProtection, int durability) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.protectionLevel = protectionLevel;
        this.armorPiercingProtection = armorPiercingProtection;
        this.durability = durability;
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
    public double getProtectionLevel() {
        return protectionLevel;
    }
    
    @Override
    public double getArmorPiercingProtection() {
        return armorPiercingProtection;
    }
    
    /**
     * Получает значение из конфигурации или значение по умолчанию, если настройка отсутствует
     *
     * @param plugin экземпляр главного класса плагина
     * @param path путь к настройке
     * @param defaultValue значение по умолчанию
     * @return значение настройки
     */
    protected double getConfigValue(BattleClass plugin, String path, double defaultValue) {
        return plugin.getConfig().getDouble(path, defaultValue);
    }
    
    /**
     * Получает прочность бронежилета
     * @return значение прочности
     */
    public int getDurability() {
        return durability;
    }
    
    @Override
    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6" + displayName);
            
            List<String> lore = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("#.##");
            
            lore.add("§7Тип: §fБронежилет");
            lore.add("§7Защита: §f" + df.format(protectionLevel * 100) + "%");
            lore.add("§7Защита от бронебойных: §f" + df.format(armorPiercingProtection * 100) + "%");
            lore.add("§7Прочность: §f" + durability);
            lore.add("");
            lore.add("§8Идентификатор: §7" + id);
            
            meta.setLore(lore);
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setUnbreakable(true);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
} 