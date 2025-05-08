package me.wth.battleClass.medical.items;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.medical.AbstractMedicalItem;
import org.bukkit.Material;

/**
 * Аптечка для серьезного лечения
 */
public class MedKit extends AbstractMedicalItem {
    
    /**
     * Конструктор для аптечки
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public MedKit(BattleClass plugin) {
        super(
            plugin,
            "medkit",
            "Аптечка",
            Material.GOLDEN_APPLE,
            plugin.getConfig().getDouble("medical.medkit.heal-amount", 15.0),          
            plugin.getConfig().getDouble("medical.medkit.use-time", 6.0),              
            plugin.getConfig().getBoolean("medical.medkit.stops-bleeding", true),      
            plugin.getConfig().getBoolean("medical.medkit.heals-injuries", true)       
        );
    }
} 