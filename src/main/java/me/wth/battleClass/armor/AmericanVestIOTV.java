package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;

/**
 * Американский бронежилет IOTV Gen 4
 */
public class AmericanVestIOTV extends AbstractArmor {
    
    /**
     * Конструктор для американского бронежилета IOTV Gen 4
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public AmericanVestIOTV(BattleClass plugin) {
        super(
            plugin,
            "iotv_gen4",
            "Бронежилет IOTV Gen 4 (США)",
            Material.NETHERITE_CHESTPLATE,
            plugin.getConfig().getDouble("armor.iotv_gen4.protection-level", 0.70),       
            plugin.getConfig().getDouble("armor.iotv_gen4.armor-piercing-protection", 0.45), 
            plugin.getConfig().getInt("armor.iotv_gen4.durability", 750)         
        );
    }
} 