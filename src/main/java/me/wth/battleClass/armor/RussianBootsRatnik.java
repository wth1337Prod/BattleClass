package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;

/**
 * Класс российских боевых ботинок (берцев)
 */
public class RussianBootsRatnik extends AbstractBoots {
    
    /**
     * Конструктор российских боевых ботинок
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public RussianBootsRatnik(BattleClass plugin) {
        super(
            plugin,
            "russian_boots_ratnik",
            "Российские боевые ботинки 'Ратник'",
            4,  
            0.03  
        );
    }
    
    @Override
    protected int getCustomModelData() {
        return 1002;  
    }
} 