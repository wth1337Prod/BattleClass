package me.wth.battleClass.weapons.pistols;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.weapons.AbstractWeapon;
import me.wth.battleClass.weapons.WeaponAttachment;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * Удав
 * Производитель: Центральный научно-исследовательский институт точного машиностроения (ЦНИИТОЧМАШ)
 * Калибр: 9×21 мм
 * Принят на вооружение: 2019 год
 * Страна: Россия
 */
public class Udav extends AbstractWeapon {

    public Udav(BattleClass plugin) {
        super(
            plugin, 
            "udav", 
            "Пистолет Удав", 
            Arrays.asList(
                "§7Производитель: §fЦНИИТОЧМАШ",
                "§7Калибр: §f9×21 мм",
                "§7Принят на вооружение: §f2019 год",
                "§7Страна: §fРоссия"
            ),
            Material.IRON_HOE, 
            getConfigValue(plugin, "udav.damage", 5.5),       
            getConfigValue(plugin, "udav.fire-rate", 3.0),    
            getConfigIntValue(plugin, "udav.range", 50),      
            getConfigIntValue(plugin, "udav.magazine-size", 18), 
            getConfigValue(plugin, "udav.reload-time", 1.8),  
            getConfigValue(plugin, "udav.accuracy", 0.85),    
            getConfigValue(plugin, "udav.recoil", 0.4)        
        );
        
        
        addAvailableAttachment(new WeaponAttachment(
            "udav_collimator", 
            "Коллиматорный прицел ПК-120", 
            Arrays.asList(
                "§7Современный российский коллиматорный прицел",
                "§7Увеличение: §f1x",
                "§7Улучшает точность и скорость прицеливания"
            ), 
            Material.ENDER_EYE, 
            WeaponAttachment.AttachmentType.SCOPE, 
            new WeaponAttachment.AttachmentStats(0.0, 0.1, 0.1, 0.0, 0, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "udav_silencer", 
            "Глушитель для Удава", 
            Arrays.asList(
                "§7Интегрированный глушитель для пистолета Удав",
                "§7Снижает шум и вспышку выстрела",
                "§7Немного снижает урон и дальность"
            ), 
            Material.BLAZE_ROD, 
            WeaponAttachment.AttachmentType.BARREL, 
            new WeaponAttachment.AttachmentStats(-0.3, 0.0, 0.05, -0.1, -5, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "udav_flashlight", 
            "Тактический фонарь ЛТ-6", 
            Arrays.asList(
                "§7Освещает цели в темноте",
                "§7Может временно ослепить противника"
            ), 
            Material.TORCH, 
            WeaponAttachment.AttachmentType.UNDERBARREL, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.05, 0.0, 0, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "udav_grip", 
            "Эргономичная рукоятка", 
            Arrays.asList(
                "§7Улучшенная рукоятка с прорезиненным покрытием",
                "§7Уменьшает отдачу и улучшает контроль"
            ), 
            Material.STICK, 
            WeaponAttachment.AttachmentType.GRIP, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.05, -0.15, 0, 0, 0.0)
        ));
    }
    
    private static double getConfigValue(BattleClass plugin, String path, double defaultValue) {
        return plugin.getConfig().getDouble(path, defaultValue);
    }
    
    private static int getConfigIntValue(BattleClass plugin, String path, int defaultValue) {
        return plugin.getConfig().getInt(path, defaultValue);
    }
} 