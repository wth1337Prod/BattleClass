package me.wth.battleClass.armor;

import org.bukkit.inventory.ItemStack;

/**
 * Интерфейс для бронежилетов
 */
public interface Armor {
    
    /**
     * Получает уникальный идентификатор бронежилета
     * @return идентификатор
     */
    String getId();
    
    /**
     * Получает отображаемое имя бронежилета
     * @return отображаемое имя
     */
    String getDisplayName();
    
    /**
     * Получает уровень защиты бронежилета (процент уменьшения урона)
     * @return уровень защиты от 0.0 до 1.0
     */
    double getProtectionLevel();
    
    /**
     * Получает защиту от бронебойных патронов (дополнительный процент защиты)
     * @return защита от бронебойных патронов от 0.0 до 1.0
     */
    double getArmorPiercingProtection();
    
    /**
     * Создает ItemStack для этого бронежилета
     * @return ItemStack бронежилета
     */
    ItemStack createItemStack();
} 