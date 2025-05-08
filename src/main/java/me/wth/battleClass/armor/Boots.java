package me.wth.battleClass.armor;

import org.bukkit.inventory.ItemStack;

/**
 * Интерфейс для военных ботинок (берцев)
 */
public interface Boots {
    /**
     * Получает идентификатор ботинок
     * 
     * @return строковый идентификатор
     */
    String getId();
    
    /**
     * Получает название ботинок для отображения
     * 
     * @return название ботинок
     */
    String getDisplayName();
    
    /**
     * Получает уровень защиты ботинок
     * 
     * @return уровень защиты
     */
    int getProtectionLevel();
    
    /**
     * Получает бонус к скорости от ботинок
     * 
     * @return бонус к скорости (может быть отрицательным)
     */
    double getSpeedBonus();
    
    /**
     * Создает ItemStack для ботинок
     * 
     * @return экземпляр ItemStack
     */
    ItemStack createItemStack();
} 