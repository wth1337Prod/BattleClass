package me.wth.battleClass.mortars;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Интерфейс для минометов
 */
public interface Mortar {
    /**
     * Получение уникального идентификатора миномета
     * @return строковый идентификатор
     */
    String getId();
    
    /**
     * Получение отображаемого имени миномета
     * @return название миномета
     */
    String getDisplayName();
    
    /**
     * Получение описания миномета
     * @return список строк с описанием
     */
    java.util.List<String> getDescription();
    
    /**
     * Создание ItemStack для данного миномета
     * @return ItemStack объект миномета
     */
    ItemStack createItemStack();
    
    /**
     * Получение дальности стрельбы миномета в блоках
     * @return максимальная дальность
     */
    int getMaxRange();
    
    /**
     * Получение минимальной дальности стрельбы миномета в блоках
     * @return минимальная дальность
     */
    int getMinRange();
    
    /**
     * Получение урона от прямого попадания миномета
     * @return величина урона
     */
    double getDirectDamage();
    
    /**
     * Получение радиуса взрыва миномета в блоках
     * @return радиус взрыва
     */
    double getExplosionRadius();
    
    /**
     * Получение времени перезарядки миномета в секундах
     * @return время перезарядки
     */
    int getReloadTime();
    
    /**
     * Проверка, является ли миномет бесшумным
     * @return true, если миномет бесшумный
     */
    boolean isSilent();
    
    /**
     * Получение веса миномета (влияет на скорость передвижения)
     * @return вес в условных единицах (1-10)
     */
    int getWeight();
    
    /**
     * Выстрел из миномета
     * @param player игрок, стреляющий из миномета
     * @param angle угол наклона миномета (градусы)
     * @param power сила выстрела (1-100%)
     * @return true, если выстрел произведен успешно
     */
    boolean fire(Player player, double angle, int power);
}