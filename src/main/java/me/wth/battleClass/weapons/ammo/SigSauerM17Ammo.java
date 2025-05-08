package me.wth.battleClass.weapons.ammo;

import me.wth.battleClass.BattleClass;
import org.bukkit.ChatColor;
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
 * Класс для создания патронов для пистолета Sig Sauer M17
 */
public class SigSauerM17Ammo {
    private final BattleClass plugin;
    
    public SigSauerM17Ammo(BattleClass plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Создает стандартные патроны 9×19 мм для M17
     * @param amount количество патронов
     * @return ItemStack с патронами
     */
    public ItemStack createStandardAmmo(int amount) {
        ItemStack item = new ItemStack(Material.IRON_NUGGET, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "9×19 мм стандартный патрон");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Стандартный патрон для Sig Sauer M17");
            lore.add("");
            lore.add(ChatColor.GRAY + "Характеристики:");
            lore.add(ChatColor.YELLOW + "• Базовый урон");
            lore.add(ChatColor.YELLOW + "• Базовая пробиваемость");
            lore.add(ChatColor.YELLOW + "• Базовая точность");
            
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            NamespacedKey key = new NamespacedKey(plugin, "ammo_id");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, "sigm17_standard");
            
            NamespacedKey weaponKey = new NamespacedKey(plugin, "weapon_id");
            container.set(weaponKey, PersistentDataType.STRING, "sigm17");
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает бронебойные патроны 9×19 мм для M17
     * @param amount количество патронов
     * @return ItemStack с патронами
     */
    public ItemStack createAPAmmo(int amount) {
        ItemStack item = new ItemStack(Material.GOLD_NUGGET, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "9×19 мм бронебойный патрон M1152");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Бронебойный патрон для Sig Sauer M17");
            lore.add("");
            lore.add(ChatColor.GRAY + "Характеристики:");
            lore.add(ChatColor.YELLOW + "• +10% к урону");
            lore.add(ChatColor.YELLOW + "• +20% к пробиваемости брони");
            lore.add(ChatColor.YELLOW + "• -5% к точности");
            
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            NamespacedKey key = new NamespacedKey(plugin, "ammo_id");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, "sigm17_ap");
            
            NamespacedKey weaponKey = new NamespacedKey(plugin, "weapon_id");
            container.set(weaponKey, PersistentDataType.STRING, "sigm17");
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает экспансивные патроны для M17
     * @param amount количество патронов
     * @return ItemStack с патронами
     */
    public ItemStack createJHPAmmo(int amount) {
        ItemStack item = new ItemStack(Material.COPPER_INGOT, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "9×19 мм JHP патрон M1153");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Экспансивный патрон для Sig Sauer M17");
            lore.add(ChatColor.GRAY + "Увеличивает урон по небронированным целям");
            lore.add("");
            lore.add(ChatColor.GRAY + "Характеристики:");
            lore.add(ChatColor.YELLOW + "• +30% к урону по мягким целям");
            lore.add(ChatColor.YELLOW + "• -25% к пробиваемости брони");
            lore.add(ChatColor.YELLOW + "• +5% к точности");
            
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            NamespacedKey key = new NamespacedKey(plugin, "ammo_id");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, "sigm17_jhp");
            
            NamespacedKey weaponKey = new NamespacedKey(plugin, "weapon_id");
            container.set(weaponKey, PersistentDataType.STRING, "sigm17");
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает дозвуковые патроны для M17
     * @param amount количество патронов
     * @return ItemStack с патронами
     */
    public ItemStack createSubsonicAmmo(int amount) {
        ItemStack item = new ItemStack(Material.PRISMARINE_CRYSTALS, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "9×19 мм дозвуковой патрон");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Дозвуковой патрон для Sig Sauer M17");
            lore.add(ChatColor.GRAY + "Идеален для использования с глушителем");
            lore.add("");
            lore.add(ChatColor.GRAY + "Характеристики:");
            lore.add(ChatColor.YELLOW + "• -15% к урону");
            lore.add(ChatColor.YELLOW + "• -10% к пробиваемости");
            lore.add(ChatColor.YELLOW + "• +10% к точности");
            lore.add(ChatColor.YELLOW + "• -15% к дальности");
            lore.add(ChatColor.YELLOW + "• -25% к звуку выстрела");
            
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            NamespacedKey key = new NamespacedKey(plugin, "ammo_id");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, "sigm17_subsonic");
            
            NamespacedKey weaponKey = new NamespacedKey(plugin, "weapon_id");
            container.set(weaponKey, PersistentDataType.STRING, "sigm17");
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
} 