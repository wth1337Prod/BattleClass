package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;

/**
 * Класс американских боевых ботинок (берцев)
 */
public class AmericanBootsCombat extends AbstractBoots {
    
    /**
     * Конструктор американских боевых ботинок
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public AmericanBootsCombat(BattleClass plugin) {
        super(
            plugin,
            "american_boots_combat",
            "Американские боевые ботинки",
            3,  
            0.05  
        );
    }
    
    @Override
    protected int getCustomModelData() {
        return 1001;  
    }
} 