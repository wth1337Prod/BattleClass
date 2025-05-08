package me.wth.battleClass.medical;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Базовый абстрактный класс для медицинских предметов
 */
public abstract class AbstractMedicalItem implements MedicalItem {
    protected final BattleClass plugin;
    protected final String id;
    protected final String displayName;
    protected final Material material;
    protected final double healAmount;
    protected final double useTime;
    protected final boolean stopsBleeding;
    protected final boolean healsInjuries;
    
    /**
     * Конструктор абстрактного медицинского предмета
     * 
     * @param plugin экземпляр главного класса плагина
     * @param id уникальный идентификатор
     * @param displayName отображаемое имя
     * @param material материал для создания предмета
     * @param healAmount количество восстанавливаемого здоровья
     * @param useTime время использования в секундах
     * @param stopsBleeding останавливает ли кровотечение
     * @param healsInjuries вылечивает ли переломы
     */
    public AbstractMedicalItem(BattleClass plugin, String id, String displayName, Material material, 
                             double healAmount, double useTime, boolean stopsBleeding, boolean healsInjuries) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.healAmount = healAmount;
        this.useTime = useTime;
        this.stopsBleeding = stopsBleeding;
        this.healsInjuries = healsInjuries;
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
    public double getHealAmount() {
        return healAmount;
    }
    
    @Override
    public double getUseTime() {
        return useTime;
    }
    
    @Override
    public boolean stopsBleeding() {
        return stopsBleeding;
    }
    
    @Override
    public boolean healsInjuries() {
        return healsInjuries;
    }
    
    @Override
    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§a" + displayName);
            
            List<String> lore = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("#.##");
            
            lore.add("§7Тип: §fМедицинский предмет");
            lore.add("§7Лечение: §f+" + df.format(healAmount) + " HP");
            lore.add("§7Время использования: §f" + df.format(useTime) + " сек.");
            
            if (stopsBleeding) {
                lore.add("§7Останавливает кровотечение: §aДа");
            }
            
            if (healsInjuries) {
                lore.add("§7Лечит переломы: §aДа");
            }
            
            lore.add("");
            lore.add("§8Идентификатор: §7" + id);
            
            meta.setLore(lore);
            
            meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            NamespacedKey key = new NamespacedKey(plugin, "medical_id");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
} 