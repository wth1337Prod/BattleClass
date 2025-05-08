package me.wth.battleClass.medical.items;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.medical.AbstractMedicalItem;
import org.bukkit.Material;

/**
 * Адреналин для временного усиления
 */
public class Adrenaline extends AbstractMedicalItem {
    
    /**
     * Конструктор для адреналина
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public Adrenaline(BattleClass plugin) {
        super(
            plugin,
            "adrenaline",
            "Адреналин",
            Material.DRAGON_BREATH,
            plugin.getConfig().getDouble("medical.adrenaline.heal-amount", 3.0),           
            plugin.getConfig().getDouble("medical.adrenaline.use-time", 1.0),              
            plugin.getConfig().getBoolean("medical.adrenaline.stops-bleeding", false),     
            plugin.getConfig().getBoolean("medical.adrenaline.heals-injuries", false)      
        );
    }
    
    /**
     * Время действия эффекта адреналина в секундах
     * @return продолжительность эффекта
     */
    public int getEffectDuration() {
        return plugin.getConfig().getInt("medical.adrenaline.effect-duration", 30);
    }
    
    /**
     * Уровень усиления скорости
     * @return уровень эффекта скорости (начинается с 0)
     */
    public int getSpeedLevel() {
        return plugin.getConfig().getInt("medical.adrenaline.speed-level", 1);
    }
    
    /**
     * Уровень усиления силы
     * @return уровень эффекта силы (начинается с 0)
     */
    public int getStrengthLevel() {
        return plugin.getConfig().getInt("medical.adrenaline.strength-level", 0);
    }
} 