package me.wth.battleClass.armor;

import org.bukkit.inventory.ItemStack;

/**
 * Интерфейс для камуфляжных штанов
 */
public interface Pants {
    /**
     * Получает идентификатор штанов
     * 
     * @return строковый идентификатор
     */
    String getId();
    
    /**
     * Получает название штанов для отображения
     * 
     * @return название штанов
     */
    String getDisplayName();
    
    /**
     * Получает уровень защиты штанов
     * 
     * @return уровень защиты
     */
    int getProtectionLevel();
    
    /**
     * Получает уровень маскировки (снижение шанса обнаружения)
     * 
     * @return уровень маскировки от 0 до 1
     */
    double getCamouflageLevel();
    
    /**
     * Создает ItemStack для штанов
     * 
     * @return экземпляр ItemStack
     */
    ItemStack createItemStack();
} 