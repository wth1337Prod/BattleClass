package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;

/**
 * Класс для лесных камуфляжных штанов
 */
public class CamouflagePantsForest extends AbstractPants {
    
    /**
     * Конструктор для лесных камуфляжных штанов
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public CamouflagePantsForest(BattleClass plugin) {
        super(
            plugin,
            "camouflage_pants_forest",
            "Лесные камуфляжные штаны",
            2,  
            0.35  
        );
    }
    
    @Override
    protected int getCustomModelData() {
        return 2001;  
    }
} 