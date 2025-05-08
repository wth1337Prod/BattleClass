package me.wth.battleClass.radio;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Интерфейс для радиостанций
 */
public interface Radio {
    /**
     * Получение уникального идентификатора радиостанции
     * @return строковый идентификатор
     */
    String getId();
    
    /**
     * Получение отображаемого имени радиостанции
     * @return название радиостанции
     */
    String getDisplayName();
    
    /**
     * Получение описания радиостанции
     * @return список строк с описанием
     */
    List<String> getDescription();
    
    /**
     * Создание ItemStack для данной радиостанции
     * @return ItemStack объект радиостанции
     */
    ItemStack createItemStack();
    
    /**
     * Получение дальности радиостанции в блоках
     * @return дальность связи
     */
    int getRange();
    
    /**
     * Получение частоты по умолчанию
     * @return частота в МГц
     */
    double getDefaultFrequency();
    
    /**
     * Проверка, может ли рация работать под водой
     * @return true, если может работать под водой
     */
    boolean isWaterproof();
    
    /**
     * Проверка, имеет ли рация шифрование
     * @return true, если рация имеет шифрование
     */
    boolean hasEncryption();
    
    /**
     * Получение времени работы от батареи в минутах
     * @return время работы в минутах
     */
    int getBatteryLife();
} 