package me.wth.battleClass.medical.items;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.medical.AbstractMedicalItem;
import org.bukkit.Material;

/**
 * Бинты для остановки кровотечения
 */
public class Bandage extends AbstractMedicalItem {
    
    /**
     * Конструктор для бинтов
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public Bandage(BattleClass plugin) {
        super(
            plugin,
            "bandage",
            "Бинт",
            Material.PAPER,
            plugin.getConfig().getDouble("medical.bandage.heal-amount", 4.0),           
            plugin.getConfig().getDouble("medical.bandage.use-time", 2.5),              
            plugin.getConfig().getBoolean("medical.bandage.stops-bleeding", true),      
            plugin.getConfig().getBoolean("medical.bandage.heals-injuries", false)      
        );
    }
} 