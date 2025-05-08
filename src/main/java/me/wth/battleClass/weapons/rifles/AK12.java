package me.wth.battleClass.weapons.rifles;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.weapons.AbstractWeapon;
import me.wth.battleClass.weapons.WeaponAttachment;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * АК-12
 * Производитель: Концерн "Калашников"
 * Калибр: 5,45×39 мм
 * Принят на вооружение: 2018 год
 * Страна: Россия
 */
public class AK12 extends AbstractWeapon {

    public AK12(BattleClass plugin) {
        super(
            plugin, 
            "ak12", 
            "АК-12", 
            Arrays.asList(
                "§7Производитель: §fКонцерн \"Калашников\"",
                "§7Калибр: §f5,45×39 мм",
                "§7Принят на вооружение: §f2018 год",
                "§7Страна: §fРоссия"
            ),
            Material.IRON_HOE, 
            getConfigValue(plugin, "ak12.damage", 7.5),   
            getConfigValue(plugin, "ak12.fire-rate", 7.0),   
            getConfigIntValue(plugin, "ak12.range", 100),   
            getConfigIntValue(plugin, "ak12.magazine-size", 30),    
            getConfigValue(plugin, "ak12.reload-time", 2.2),   
            getConfigValue(plugin, "ak12.accuracy", 0.8),   
            getConfigValue(plugin, "ak12.recoil", 0.65)   
        );
        
        
        addAvailableAttachment(new WeaponAttachment(
            "ak12_1p87", 
            "Коллиматорный прицел 1П87", 
            Arrays.asList(
                "§7Современный российский коллиматорный прицел",
                "§7Увеличение: §f1x",
                "§7Улучшает точность и скорость прицеливания"
            ), 
            Material.ENDER_EYE, 
            WeaponAttachment.AttachmentType.SCOPE, 
            new WeaponAttachment.AttachmentStats(0.0, 0.1, 0.05, 0.0, 0, 0, -0.1)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "ak12_1p78", 
            "Оптический прицел 1П78 Кашtan", 
            Arrays.asList(
                "§7Штатный оптический прицел российской армии",
                "§7Увеличение: §f2.8x",
                "§7Значительно улучшает точность на средних дистанциях"
            ), 
            Material.SPYGLASS, 
            WeaponAttachment.AttachmentType.SCOPE, 
            new WeaponAttachment.AttachmentStats(0.0, -0.1, 0.15, -0.05, 15, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "ak12_dtk4", 
            "Дульный тормоз-компенсатор ДТК-4", 
            Arrays.asList(
                "§7Эффективно снижает отдачу и подброс оружия",
                "§7Незначительно снижает дальность"
            ), 
            Material.IRON_BARS, 
            WeaponAttachment.AttachmentType.BARREL, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.0, -0.2, -5, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "ak12_pbs1", 
            "Глушитель ПБС-1", 
            Arrays.asList(
                "§7Тактический глушитель для АК",
                "§7Снижает шум и вспышку выстрела",
                "§7Немного снижает урон и дальность"
            ), 
            Material.BLAZE_ROD, 
            WeaponAttachment.AttachmentType.BARREL, 
            new WeaponAttachment.AttachmentStats(-0.5, 0.0, 0.05, -0.1, -10, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "ak12_rvg", 
            "Тактическая рукоятка РВГ", 
            Arrays.asList(
                "§7Улучшает контроль и устойчивость оружия",
                "§7Снижает отдачу при стрельбе"
            ), 
            Material.STICK, 
            WeaponAttachment.AttachmentType.GRIP, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.1, -0.15, 0, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "ak12_rpk_mag", 
            "Магазин РПК на 45 патронов", 
            Arrays.asList(
                "§7Увеличенный магазин от РПК",
                "§7Увеличивает количество патронов",
                "§7Немного увеличивает время перезарядки"
            ), 
            Material.IRON_INGOT, 
            WeaponAttachment.AttachmentType.MAGAZINE, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, -0.05, 0.1, 0, 15, 0.4)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "ak12_telescopic_stock", 
            "Телескопический приклад АК-12", 
            Arrays.asList(
                "§7Складной телескопический приклад",
                "§7Улучшает эргономику и мобильность"
            ), 
            Material.BONE, 
            WeaponAttachment.AttachmentType.STOCK, 
            new WeaponAttachment.AttachmentStats(0.0, 0.1, 0.0, -0.1, 0, 0, -0.1)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "ak12_gp25", 
            "Подствольный гранатомет ГП-25", 
            Arrays.asList(
                "§7Позволяет запускать 40-мм гранаты",
                "§7Наносит урон по площади",
                "§7Замедляет скорость передвижения"
            ), 
            Material.TNT, 
            WeaponAttachment.AttachmentType.UNDERBARREL, 
            new WeaponAttachment.AttachmentStats(5.0, -0.5, -0.1, 0.1, -5, 0, 0.5)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "ak12_bayonet", 
            "Штык-нож 6Х9", 
            Arrays.asList(
                "§7Стандартный штык-нож для АК-12",
                "§7Увеличивает урон в ближнем бою"
            ), 
            Material.IRON_SWORD, 
            WeaponAttachment.AttachmentType.UNDERBARREL, 
            new WeaponAttachment.AttachmentStats(2.0, 0.0, 0.0, 0.0, -5, 0, 0.0)
        ));
    }
    
    private static double getConfigValue(BattleClass plugin, String path, double defaultValue) {
        return plugin.getConfig().getDouble(path, defaultValue);
    }
    
    private static int getConfigIntValue(BattleClass plugin, String path, int defaultValue) {
        return plugin.getConfig().getInt(path, defaultValue);
    }
} 