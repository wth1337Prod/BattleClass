package me.wth.battleClass.drones;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Класс российского дрона-камикадзе "Ланцет"
 */
public class LancetKamikaze extends AbstractDrone {

    /**
     * Конструктор для создания дрона-камикадзе "Ланцет"
     *
     * @param plugin экземпляр плагина
     */
    public LancetKamikaze(BattleClass plugin) {
        super(
            plugin,
            "lancet_kamikaze",    
            "Дрон-камикадзе \"Ланцет-3\"",  
            createDescription(),   
            Material.DRAGON_BREATH, 
            150,                   
            300,                   
            4.5,                   
            600,                   
            10.0,                  
            180,                   
            20.0,                  
            5.0,                   
            null                   
        );
    }
    
    /**
     * Создает список строк с описанием дрона
     *
     * @return список строк описания
     */
    private static List<String> createDescription() {
        return Arrays.asList(
            "§7Российский барражирующий боеприпас",
            "§7с функцией самонаведения.",
            "§7Предназначен для уничтожения",
            "§7высокоценных целей путем",
            "§7направленного подрыва."
        );
    }
    
    @Override
    public boolean detonate(Player player) {
        boolean result = super.detonate(player);
        
        if (result) {
            player.sendMessage("§2Дрон \"Ланцет-3\" успешно поразил цель!");
        }
        
        return result;
    }
} 