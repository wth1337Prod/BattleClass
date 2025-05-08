package me.wth.battleClass.medical;

import org.bukkit.inventory.ItemStack;

/**
 * Интерфейс для медицинских предметов
 */
public interface MedicalItem {
    
    /**
     * Получает уникальный идентификатор медицинского предмета
     * @return идентификатор
     */
    String getId();
    
    /**
     * Получает отображаемое имя медицинского предмета
     * @return отображаемое имя
     */
    String getDisplayName();
    
    /**
     * Получает количество восстанавливаемого здоровья
     * @return значение восстанавливаемого здоровья
     */
    double getHealAmount();
    
    /**
     * Получает время использования предмета в секундах
     * @return время использования
     */
    double getUseTime();
    
    /**
     * Останавливает ли предмет кровотечение
     * @return true, если останавливает кровотечение
     */
    boolean stopsBleeding();
    
    /**
     * Вылечивает ли предмет переломы
     * @return true, если вылечивает переломы
     */
    boolean healsInjuries();
    
    /**
     * Создает ItemStack для этого медицинского предмета
     * @return ItemStack предмета
     */
    ItemStack createItemStack();
} 