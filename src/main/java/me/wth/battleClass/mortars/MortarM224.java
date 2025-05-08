package me.wth.battleClass.mortars;

import me.wth.battleClass.BattleClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс представляющий американский 60-мм миномет M224
 */
public class MortarM224 extends AbstractMortar {
    
    /**
     * Создает новый экземпляр миномета M224
     * 
     * @param plugin экземпляр основного плагина
     */
    public MortarM224(BattleClass plugin) {
        super(
            plugin,
            "m224",
            ChatColor.BLUE + "M224 Mortar",
            createDescription(),
            Material.HOPPER, 
            700,   
            80,    
            18.0,  
            7.0,   
            12,    
            false, 
            5      
        );
    }
    
    /**
     * Создает описание для миномета
     * 
     * @return список строк с описанием
     */
    private static List<String> createDescription() {
        return Arrays.asList(
            ChatColor.GRAY + "Американский 60-мм миномет",
            ChatColor.GRAY + "Состоит на вооружении ВС США",
            ChatColor.GRAY + "Принят на вооружение в 1978 году",
            ChatColor.GRAY + "Вес в боевом положении: 21 кг",
            ChatColor.GRAY + "Дальность стрельбы: до 3500 м",
            ChatColor.GRAY + "Скорострельность: до 30 выстр./мин",
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
            lore.add("§7Дальность: §f" + minRange + "-" + maxRange + " блоков");
            lore.add("§7Урон: §f" + directDamage);
            lore.add("§7Радиус взрыва: §f" + explosionRadius + " блоков");
            lore.add("§7Перезарядка: §f" + reloadTime + " сек.");
            lore.add("§7Бесшумность: " + (silent ? "§aДа" : "§cНет"));
            lore.add("§7Вес: §f" + weight + "/10");
            lore.add("");
            lore.add("§eПКМ - установить миномет");
            lore.add("§eПрисесть+ПКМ - настроить угол и силу");
            
            meta.setLore(lore);
            
            meta.addEnchant(Enchantment.QUICK_CHARGE, 1, true);
            
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            
            meta.setUnbreakable(true);
            
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "mortar_id"), PersistentDataType.STRING, id);
            container.set(new NamespacedKey(plugin, "mortar_model"), PersistentDataType.STRING, "m224");
            
            itemStack.setItemMeta(meta);
        }
        
        return itemStack;
    }
} 