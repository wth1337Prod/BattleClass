package me.wth.battleClass.drones;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Интерфейс для боевых дронов
 */
public interface Drone {
    /**
     * Получение уникального идентификатора дрона
     * @return строковый идентификатор
     */
    String getId();
    
    /**
     * Получение отображаемого имени дрона
     * @return название дрона
     */
    String getDisplayName();
    
    /**
     * Получение описания дрона
     * @return список строк с описанием
     */
    java.util.List<String> getDescription();
    
    /**
     * Создание ItemStack для данного дрона
     * @return ItemStack объект дрона
     */
    ItemStack createItemStack();
    
    /**
     * Получение максимальной высоты полета дрона в блоках
     * @return максимальная высота
     */
    int getMaxHeight();
    
    /**
     * Получение дальности действия дрона от оператора в блоках
     * @return максимальная дальность
     */
    int getOperationalRange();
    
    /**
     * Получение скорости перемещения дрона (блоков в секунду)
     * @return скорость перемещения
     */
    double getSpeed();
    
    /**
     * Получение емкости батареи дрона
     * @return емкость в условных единицах
     */
    int getBatteryCapacity();
    
    /**
     * Получение расхода энергии дрона (единиц в минуту)
     * @return расход энергии
     */
    double getBatteryConsumption();
    
    /**
     * Получение времени перезарядки дрона-камикадзе (только для камикадзе)
     * @return время перезарядки в секундах, 0 если дрон не камикадзе
     */
    int getRechargeTime();
    
    /**
     * Получение урона от подрыва дрона-камикадзе
     * @return величина урона, 0 если дрон не камикадзе
     */
    double getExplosionDamage();
    
    /**
     * Получение радиуса поражения от подрыва дрона-камикадзе
     * @return радиус в блоках, 0 если дрон не камикадзе
     */
    double getExplosionRadius();
    
    /**
     * Получение вооружения дрона (только для боевых дронов)
     * @return описание вооружения, null если дрон не боевой
     */
    String getWeaponType();
    
    /**
     * Запуск дрона
     * @param player игрок, запускающий дрон
     * @return true, если запуск выполнен успешно
     */
    boolean launch(Player player);
    
    /**
     * Перезарядка батареи дрона
     * @param player игрок, перезаряжающий дрон
     * @param amount количество единиц заряда
     * @return true, если перезарядка выполнена успешно
     */
    boolean recharge(Player player, int amount);
    
    /**
     * Детонация дрона-камикадзе
     * @param player игрок, активирующий подрыв
     * @return true, если подрыв выполнен успешно
     */
    boolean detonate(Player player);

    boolean toggleFirstPersonView(Player player);

    boolean moveDrone(Player player, Vector direction);

    /**
     * Отзыв дрона к оператору
     * @param player игрок-оператор дрона
     * @return true, если дрон успешно отозван
     */
    boolean recall(Player player);
    
    /**
     * Получение текущего заряда батареи дрона
     * @param player игрок, владеющий дроном
     * @return текущий заряд батареи
     */
    int getCurrentBattery(Player player);
} 