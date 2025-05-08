package me.wth.battleClass.armor;

import org.bukkit.inventory.ItemStack;

/**
 * Интерфейс для военных шлемов
 */
public interface Helmet {
    
    /**
     * Получает уникальный идентификатор шлема
     * @return идентификатор
     */
    String getId();
    
    /**
     * Получает отображаемое имя шлема
     * @return отображаемое имя
     */
    String getDisplayName();
    
    /**
     * Получает уровень защиты головы
     * @return уровень защиты от 0.0 до 1.0
     */
    double getHeadProtectionLevel();
    
    /**
     * Получает защиту от бронебойных патронов
     * @return защита от бронебойных патронов от 0.0 до 1.0
     */
    double getArmorPiercingProtection();
    
    /**
     * Создает ItemStack для этого шлема
     * @return ItemStack шлема
     */
    ItemStack createItemStack();
} 