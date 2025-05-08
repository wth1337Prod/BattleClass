package me.wth.battleClass.radio;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый абстрактный класс для реализации радиостанций
 */
public abstract class AbstractRadio implements Radio {
    protected final BattleClass plugin;
    protected final String id;
    protected final String displayName;
    protected final List<String> description;
    protected final Material material;
    protected final int range;
    protected final double defaultFrequency;
    protected final boolean waterproof;
    protected final boolean hasEncryption;
    protected final int batteryLife;
    
    /**
     * Конструктор для создания радиостанции
     * 
     * @param plugin экземпляр плагина
     * @param id идентификатор рации
     * @param displayName отображаемое название
     * @param description описание рации
     * @param material материал для отображения в инвентаре
     * @param range дальность связи в блоках
     * @param defaultFrequency частота по умолчанию в МГц
     * @param waterproof может ли рация работать под водой
     * @param hasEncryption имеет ли рация шифрование
     * @param batteryLife время работы от батареи в минутах
     */
    public AbstractRadio(BattleClass plugin, String id, String displayName, List<String> description, 
                         Material material, int range, double defaultFrequency, 
                         boolean waterproof, boolean hasEncryption, int batteryLife) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.range = range;
        this.defaultFrequency = defaultFrequency;
        this.waterproof = waterproof;
        this.hasEncryption = hasEncryption;
        this.batteryLife = batteryLife;
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
    public List<String> getDescription() {
        return description;
    }
    
    @Override
    public int getRange() {
        return range;
    }
    
    @Override
    public double getDefaultFrequency() {
        return defaultFrequency;
    }
    
    @Override
    public boolean isWaterproof() {
        return waterproof;
    }
    
    @Override
    public boolean hasEncryption() {
        return hasEncryption;
    }
    
    @Override
    public int getBatteryLife() {
        return batteryLife;
    }
    
    @Override
    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§a" + displayName);
            
            List<String> lore = new ArrayList<>();
            lore.addAll(description);
            lore.add("");
            lore.add("§7Дальность: §f" + range + " блоков");
            lore.add("§7Частота: §f" + defaultFrequency + " МГц");
            lore.add("§7Работает под водой: " + (waterproof ? "§aДа" : "§cНет"));
            lore.add("§7Шифрование: " + (hasEncryption ? "§aДа" : "§cНет"));
            lore.add("§7Время работы: §f" + batteryLife + " минут");
            lore.add("");
            lore.add("§eПКМ - использовать рацию");
            
            meta.setLore(lore);
            
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            
            NamespacedKey key = new NamespacedKey(plugin, "radio_id");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, id);
            
            NamespacedKey freqKey = new NamespacedKey(plugin, "radio_frequency");
            container.set(freqKey, PersistentDataType.DOUBLE, defaultFrequency);
            
            itemStack.setItemMeta(meta);
        }
        
        return itemStack;
    }
} 