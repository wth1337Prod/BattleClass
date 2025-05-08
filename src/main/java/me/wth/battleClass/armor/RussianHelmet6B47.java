package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;

/**
 * Российский шлем 6Б47
 */
public class RussianHelmet6B47 extends AbstractHelmet {
    
    /**
     * Конструктор для российского шлема 6Б47
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public RussianHelmet6B47(BattleClass plugin) {
        super(
            plugin,
            "6b47",
            "Шлем 6Б47 (Россия)",
            Material.IRON_HELMET,
            plugin.getConfig().getDouble("helmet.6b47.head-protection-level", 0.55),       
            plugin.getConfig().getDouble("helmet.6b47.armor-piercing-protection", 0.35),   
            plugin.getConfig().getInt("helmet.6b47.durability", 650)                      
        );
    }
} 