package me.wth.battleClass.tablet;

import me.wth.battleClass.BattleClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Менеджер для создания и управления военными планшетами
 */
public class TabletManager {
    private final BattleClass plugin;
    private final NamespacedKey tabletTypeKey;
    
    /**
     * Конструктор для TabletManager
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public TabletManager(BattleClass plugin) {
        this.plugin = plugin;
        this.tabletTypeKey = new NamespacedKey(plugin, "tablet_type");
    }
    
    /**
     * Создает планшет производства США
     * 
     * @return ItemStack представляющий американский планшет
     */
    public ItemStack createUSTablet() {
        ItemStack tablet = new ItemStack(Material.BOOK);
        ItemMeta meta = tablet.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.BLUE + "Военный планшет США");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Тактический военный планшет");
            lore.add(ChatColor.GRAY + "Производство: " + ChatColor.BLUE + "США");
            lore.add("");
            lore.add(ChatColor.YELLOW + "ПКМ чтобы открыть интерфейс");
            meta.setLore(lore);
            
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            
            meta.getPersistentDataContainer().set(tabletTypeKey, PersistentDataType.STRING, "us");
            
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            
            tablet.setItemMeta(meta);
        }
        
        return tablet;
    }
    
    /**
     * Создает планшет производства России
     * 
     * @return ItemStack представляющий российский планшет
     */
    public ItemStack createRussianTablet() {
        ItemStack tablet = new ItemStack(Material.BOOK);
        ItemMeta meta = tablet.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Военный планшет РФ");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Тактический военный планшет");
            lore.add(ChatColor.GRAY + "Производство: " + ChatColor.RED + "Россия");
            lore.add("");
            lore.add(ChatColor.YELLOW + "ПКМ чтобы открыть интерфейс");
            meta.setLore(lore);
            
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            
            meta.getPersistentDataContainer().set(tabletTypeKey, PersistentDataType.STRING, "ru");
            
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            
            tablet.setItemMeta(meta);
        }
        
        return tablet;
    }
    
    /**
     * Проверяет, является ли предмет военным планшетом
     * 
     * @param item проверяемый предмет
     * @return true если предмет является военным планшетом
     */
    public boolean isTablet(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(tabletTypeKey, PersistentDataType.STRING);
    }
    
    /**
     * Получает тип планшета (us/ru)
     * 
     * @param item проверяемый предмет
     * @return строка с типом планшета или null, если это не планшет
     */
    public String getTabletType(ItemStack item) {
        if (!isTablet(item)) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(tabletTypeKey, PersistentDataType.STRING);
    }
    
    /**
     * Получает список доступных идентификаторов планшетов
     * 
     * @return список идентификаторов планшетов (us, ru)
     */
    public List<String> getTabletIds() {
        return Arrays.asList("us", "ru");
    }
} 