package me.wth.battleClass.medical.items;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.medical.AbstractMedicalItem;
import org.bukkit.Material;

/**
 * Обезболивающее для быстрого лечения
 */
public class Painkiller extends AbstractMedicalItem {
    
    /**
     * Конструктор для обезболивающего
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public Painkiller(BattleClass plugin) {
        super(
            plugin,
            "painkiller",
            "Обезболивающее",
            Material.GLASS_BOTTLE,
            plugin.getConfig().getDouble("medical.painkiller.heal-amount", 6.0),           
            plugin.getConfig().getDouble("medical.painkiller.use-time", 1.5),              
            plugin.getConfig().getBoolean("medical.painkiller.stops-bleeding", false),     
            plugin.getConfig().getBoolean("medical.painkiller.heals-injuries", false)      
        );
    }
} 