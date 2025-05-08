package me.wth.battleClass.weapons.pistols;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.weapons.AbstractWeapon;
import me.wth.battleClass.weapons.WeaponAttachment;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * Sig Sauer M17
 * Производитель: Sig Sauer Inc.
 * Калибр: 9×19 мм Parabellum
 * Принят на вооружение: 2017 год
 * Страна: США
 */
public class SigSauerM17 extends AbstractWeapon {

    public SigSauerM17(BattleClass plugin) {
        super(
            plugin, 
            "sigm17", 
            "Sig Sauer M17", 
            Arrays.asList(
                "§7Производитель: §fSig Sauer Inc.",
                "§7Калибр: §f9×19 мм Parabellum",
                "§7Принят на вооружение: §f2017 год",
                "§7Страна: §fСША"
            ),
            Material.IRON_HOE, 
            getConfigValue(plugin, "sigm17.damage", 5.0),       
            getConfigValue(plugin, "sigm17.fire-rate", 3.2),    
            getConfigIntValue(plugin, "sigm17.range", 45),      
            getConfigIntValue(plugin, "sigm17.magazine-size", 17), 
            getConfigValue(plugin, "sigm17.reload-time", 1.6),  
            getConfigValue(plugin, "sigm17.accuracy", 0.9),     
            getConfigValue(plugin, "sigm17.recoil", 0.35)       
        );
        
        
        addAvailableAttachment(new WeaponAttachment(
            "sigm17_rmr", 
            "Коллиматорный прицел Trijicon RMR", 
            Arrays.asList(
                "§7Тактический коллиматорный прицел",
                "§7Увеличение: §f1x",
                "§7Существенно улучшает точность прицеливания"
            ), 
            Material.ENDER_EYE, 
            WeaponAttachment.AttachmentType.SCOPE, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.15, -0.05, 5, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "sigm17_suppressor", 
            "Глушитель SRD9", 
            Arrays.asList(
                "§7Тактический глушитель для M17",
                "§7Снижает шум и вспышку выстрела",
                "§7Немного снижает урон и дальность"
            ), 
            Material.BLAZE_ROD, 
            WeaponAttachment.AttachmentType.BARREL, 
            new WeaponAttachment.AttachmentStats(-0.2, 0.0, 0.0, -0.1, -5, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "sigm17_laser", 
            "Лазерный целеуказатель AN/PEQ-15", 
            Arrays.asList(
                "§7Тактический лазерный целеуказатель",
                "§7Улучшает точность стрельбы с бедра",
                "§7Видим противникам"
            ), 
            Material.REDSTONE_TORCH, 
            WeaponAttachment.AttachmentType.UNDERBARREL, 
            new WeaponAttachment.AttachmentStats(0, 0.1, 0.1, 0.0, 0, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "sigm17_flashlight", 
            "Тактический фонарь Surefire X300", 
            Arrays.asList(
                "§7Высокоинтенсивный тактический фонарь",
                "§7Освещает цели в темноте",
                "§7Может временно ослепить противника"
            ), 
            Material.TORCH, 
            WeaponAttachment.AttachmentType.UNDERBARREL, 
            new WeaponAttachment.AttachmentStats(0.0, 0.0, 0.05, 0.0, 0, 0, 0.0)
        ));
        
        addAvailableAttachment(new WeaponAttachment(
            "sigm17_extended_mag", 
            "Расширенный магазин на 21 патрон", 
            Arrays.asList(
                "§7Увеличенный магазин для M17",
                "§7Увеличивает количество патронов",
                "§7Немного увеличивает время перезарядки"
            ), 
            Material.IRON_INGOT, 
            WeaponAttachment.AttachmentType.MAGAZINE, 
            new WeaponAttachment.AttachmentStats(0.0, -0.1, 0.0, 0.05, 0, 4, 0.2)
        ));
    }
    
    private static double getConfigValue(BattleClass plugin, String path, double defaultValue) {
        return plugin.getConfig().getDouble(path, defaultValue);
    }
    
    private static int getConfigIntValue(BattleClass plugin, String path, int defaultValue) {
        return plugin.getConfig().getInt(path, defaultValue);
    }
} 