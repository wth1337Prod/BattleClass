package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;

/**
 * Российский бронежилет 6Б45
 */
public class RussianVest6B45 extends AbstractArmor {
    
    /**
     * Конструктор для российского бронежилета 6Б45
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public RussianVest6B45(BattleClass plugin) {
        super(
            plugin,
            "6b45",
            "Бронежилет 6Б45 (Россия)",
            Material.IRON_CHESTPLATE,
            plugin.getConfig().getDouble("armor.6b45.protection-level", 0.65),       
            plugin.getConfig().getDouble("armor.6b45.armor-piercing-protection", 0.40), 
            plugin.getConfig().getInt("armor.6b45.durability", 800)         
        );
    }
} 