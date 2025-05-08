package me.wth.battleClass.weapons.rifles;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.weapons.AbstractWeapon;
import me.wth.battleClass.weapons.WeaponAttachment;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.List;

/**
 * XM7 (SIG MCX Spear)
 * Производитель: SIG Sauer
 * Калибр: 6.8×51 мм (.277 SIG Fury)
 * Принята на вооружение: 2022 год
 * Начало полевых поставок: март 2024 г. в 101-ю воздушно-штурмовую дивизию и 75-й полк рейнджеров
 * Назначение: замена карабина M4A1 и легкого пулемета M249 в пехотных подразделениях
 */
public class XM7 extends AbstractWeapon {

    public XM7(BattleClass plugin) {
        super(
            plugin, 
            "xm7", 
            "XM7 (SIG MCX Spear)", 
            Arrays.asList(
                "§7Производитель: §fSIG Sauer",
                "§7Калибр: §f6.8×51 мм (.277 SIG Fury)",
                "§7Принята на вооружение: §f2022 год",
                "§7Страна: §fСША"
            ),
            Material.NETHERITE_HOE, 
            getConfigValue(plugin, "xm7.damage", 8.5),   
            getConfigValue(plugin, "xm7.fire-rate", 8.0),   
            getConfigIntValue(plugin, "xm7.range", 120),   
            getConfigIntValue(plugin, "xm7.magazine-size", 20),    
            getConfigValue(plugin, "xm7.reload-time", 2.5),   
            getConfigValue(plugin, "xm7.accuracy", 0.85),   
            getConfigValue(plugin, "xm7.recoil", 0.7)    
        );
        
        
        addAvailableAttachment(new WeaponAttachment(
            "xm7_acog", 
            "Trijicon ACOG TA31RCO", 
            Arrays.asList(
                "§7Стандартный оптический прицел армии США",
                "§7Увеличение: §f4x",
                "§7Улучшает точность на дальних дистанциях"
            ), 
            Material.SPYGLASS, 
            WeaponAttachment.AttachmentType.SCOPE, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.1, -0.05, 20, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "xm7_eotech", 
            "EOTech EXPS3", 
            Arrays.asList(
                "§7Голографический прицел",
                "§7Увеличение: §f1x",
                "§7Улучшает скорость прицеливания"
            ), 
            Material.ENDER_EYE, 
            WeaponAttachment.AttachmentType.SCOPE, 
            new WeaponAttachment.AttachmentStats(0.0, 0.1, 0.05, 0.0, 0, 0, -0.1)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "xm7_suppressor", 
            "SIG SLX Suppressor", 
            Arrays.asList(
                "§7Тактический глушитель",
                "§7Снижает шум и вспышку выстрела",
                "§7Немного снижает урон и скорость пули"
            ), 
            Material.IRON_BARS, 
            WeaponAttachment.AttachmentType.BARREL, 
            new WeaponAttachment.AttachmentStats(-0.5, 0.0, 0.05, -0.1, -10, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "xm7_flash_hider", 
            "SIG Flash Hider", 
            Arrays.asList(
                "§7Пламегаситель",
                "§7Уменьшает вспышку при стрельбе",
                "§7Улучшает контроль оружия"
            ), 
            Material.BLAZE_ROD, 
            WeaponAttachment.AttachmentType.BARREL, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.0, -0.15, 0, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "xm7_vertical_grip", 
            "Тактическая вертикальная рукоятка", 
            Arrays.asList(
                "§7Улучшает контроль и устойчивость оружия",
                "§7Снижает отдачу при стрельбе"
            ), 
            Material.STICK, 
            WeaponAttachment.AttachmentType.GRIP, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.1, -0.2, 0, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "xm7_extended_mag", 
            "Увеличенный магазин SIG", 
            Arrays.asList(
                "§7Магазин повышенной емкости",
                "§7Увеличивает количество патронов",
                "§7Немного увеличивает время перезарядки"
            ), 
            Material.IRON_INGOT, 
            WeaponAttachment.AttachmentType.MAGAZINE, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.0, 0.0, 0, 10, 0.3)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "xm7_adjustable_stock", 
            "Регулируемый приклад SIG", 
            Arrays.asList(
                "§7Улучшает эргономику и контроль оружия",
                "§7Снижает отдачу при стрельбе"
            ), 
            Material.BONE, 
            WeaponAttachment.AttachmentType.STOCK, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.05, -0.15, 0, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "xm7_grenade_launcher", 
            "M320 Подствольный гранатомет", 
            Arrays.asList(
                "§7Позволяет запускать 40-мм гранаты",
                "§7Наносит урон по площади"
            ), 
            Material.TNT, 
            WeaponAttachment.AttachmentType.UNDERBARREL, 
            new WeaponAttachment.AttachmentStats(5.0, -0.5, -0.1, 0.2, -10, 0, 0.5)
        ));
    }
    
    private static double getConfigValue(BattleClass plugin, String path, double defaultValue) {
        return plugin.getConfig().getDouble(path, defaultValue);
    }
    
    private static int getConfigIntValue(BattleClass plugin, String path, int defaultValue) {
        return plugin.getConfig().getInt(path, defaultValue);
    }
} 