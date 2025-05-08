package me.wth.battleClass.radio;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * Реализация американской тактической радиостанции AN/PRC-163
 */
public class ANPRC163 extends AbstractRadio {
    
    /**
     * Конструктор радиостанции AN/PRC-163
     * 
     * @param plugin экземпляр плагина
     */
    public ANPRC163(BattleClass plugin) {
        super(
            plugin,
            "anprc163",
            "AN/PRC-163",
            Arrays.asList(
                "§7Новейшая американская тактическая",
                "§7многоканальная радиостанция системы JTRS,",
                "§7используемая подразделениями армии США."
            ),
            Material.GOLDEN_HOE, 
            1000, 
            30.0, 
            true, 
            true, 
            540 
        );
    }
    
    /**
     * Проверяет, поддерживает ли рация режим прыгающих частот
     * 
     * @return true, поскольку AN/PRC-163 поддерживает режим прыгающих частот
     */
    public boolean supportsFrequencyHopping() {
        return true;
    }
    
    /**
     * Проверяет, имеет ли рация поддержку GPS
     * 
     * @return true, поскольку AN/PRC-163 имеет встроенную поддержку GPS
     */
    public boolean hasGPS() {
        return true;
    }
    
    /**
     * Проверяет, поддерживает ли рация расширенный диапазон частот
     * 
     * @return true, поскольку AN/PRC-163 поддерживает расширенный диапазон частот
     */
    public boolean hasExtendedBand() {
        return true;
    }
} 