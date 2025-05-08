package me.wth.battleClass.radio;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * Реализация российской цифровой радиостанции Р-187П1 "Азарт"
 */
public class R187P1Azart extends AbstractRadio {
    
    /**
     * Конструктор радиостанции Р-187П1 "Азарт"
     * 
     * @param plugin экземпляр плагина
     */
    public R187P1Azart(BattleClass plugin) {
        super(
            plugin,
            "r187p1_azart",
            "Р-187П1 \"Азарт\"",
            Arrays.asList(
                "§7Современная российская цифровая",
                "§7военная радиостанция, используемая",
                "§7в Вооруженных Силах РФ."
            ),
            Material.DIAMOND_HOE, 
            800, 
            27.5, 
            true, 
            true, 
            480 
        );
    }
    
    /**
     * Подавляет шум при работе с шифрованием
     * 
     * @return уровень подавления шума
     */
    public double getNoiseCancellation() {
        return 0.85; 
    }
    
    /**
     * Проверяет, поддерживает ли рация режим прыгающих частот
     * 
     * @return true, поскольку Азарт поддерживает режим прыгающих частот
     */
    public boolean supportsFrequencyHopping() {
        return true;
    }
} 