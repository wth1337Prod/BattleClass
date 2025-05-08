package me.wth.battleClass.flamethrowers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Интерфейс для огнеметов
 */
public interface Flamethrower {
    /**
     * Получение уникального идентификатора огнемета
     * @return строковый идентификатор
     */
    String getId();
    
    /**
     * Получение отображаемого имени огнемета
     * @return название огнемета
     */
    String getDisplayName();
    
    /**
     * Получение описания огнемета
     * @return список строк с описанием
     */
    java.util.List<String> getDescription();
    
    /**
     * Создание ItemStack для данного огнемета
     * @return ItemStack объект огнемета
     */
    ItemStack createItemStack();
    
    /**
     * Получение дальности стрельбы огнемета в блоках
     * @return максимальная дальность
     */
    int getRange();
    
    /**
     * Получение урона от прямого попадания огнемета (урон в секунду)
     * @return величина урона
     */
    double getDamagePerSecond();
    
    /**
     * Получение радиуса действия огнемета в блоках
     * @return радиус действия
     */
    double getRadius();
    
    /**
     * Получение длительности горения цели в секундах
     * @return длительность горения
     */
    int getBurnDuration();
    
    /**
     * Получение емкости топливного бака огнемета
     * @return объем топлива
     */
    int getFuelCapacity();
    
    /**
     * Получение расхода топлива огнемета (единиц в секунду)
     * @return расход топлива
     */
    double getFuelConsumption();
    
    /**
     * Получение веса огнемета (влияет на скорость передвижения)
     * @return вес в условных единицах (1-10)
     */
    int getWeight();
    
    /**
     * Использование огнемета для атаки
     * @param player игрок, использующий огнемет
     * @return true, если атака выполнена успешно
     */
    boolean fire(Player player);
    
    /**
     * Перезаправка огнемета
     * @param player игрок, перезаправляющий огнемет
     * @param amount количество единиц топлива для заправки
     * @return true, если перезаправка выполнена успешно
     */
    boolean refuel(Player player, int amount);
} 