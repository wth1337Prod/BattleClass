package me.wth.battleClass.weapons;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Weapon {
    /**
     * Получение уникального идентификатора оружия
     * @return строковый идентификатор
     */
    String getId();
    
    /**
     * Получение отображаемого имени оружия
     * @return название оружия
     */
    String getDisplayName();
    
    /**
     * Получение описания оружия
     * @return список строк с описанием
     */
    List<String> getDescription();
    
    /**
     * Создание ItemStack для данного оружия
     * @return ItemStack объект оружия
     */
    ItemStack createItemStack();
    
    /**
     * Получение урона оружия
     * @return значение урона
     */
    double getDamage();
    
    /**
     * Получение скорости стрельбы (выстрелов в секунду)
     * @return скорость стрельбы
     */
    double getFireRate();
    
    /**
     * Получение максимальной дальности стрельбы
     * @return максимальная дальность в блоках
     */
    int getRange();
    
    /**
     * Получение размера магазина
     * @return количество патронов в магазине
     */
    int getMagazineSize();
    
    /**
     * Получение времени перезарядки в секундах
     * @return время перезарядки
     */
    double getReloadTime();
    
    /**
     * Получение точности оружия (0.0-1.0)
     * @return точность
     */
    double getAccuracy();
    
    /**
     * Получение силы отдачи оружия (0.0-1.0)
     * @return сила отдачи
     */
    double getRecoil();
    
    /**
     * Получение списка доступных аксессуаров
     * @return список аксессуаров
     */
    List<WeaponAttachment> getAvailableAttachments();
} 