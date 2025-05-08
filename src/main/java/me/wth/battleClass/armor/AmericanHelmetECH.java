package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;

/**
 * Американский шлем ECH (Enhanced Combat Helmet)
 */
public class AmericanHelmetECH extends AbstractHelmet {
    
    /**
     * Конструктор для американского шлема ECH
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public AmericanHelmetECH(BattleClass plugin) {
        super(
            plugin,
            "ech",
            "Шлем ECH (США)",
            Material.DIAMOND_HELMET,
            plugin.getConfig().getDouble("helmet.ech.head-protection-level", 0.60),       
            plugin.getConfig().getDouble("helmet.ech.armor-piercing-protection", 0.40),   
            plugin.getConfig().getInt("helmet.ech.durability", 700)                      
        );
    }
} 