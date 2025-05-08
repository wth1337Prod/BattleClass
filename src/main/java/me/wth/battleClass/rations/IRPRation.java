package me.wth.battleClass.rations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IRPRation extends Ration {
    
    public IRPRation() {
        super(
            "irp", 
            ChatColor.RED + "ИРП - Индивидуальный рацион питания (РФ)", 
            10,     
            5.0,    
            5.0,    
            40,     
            Material.COOKED_BEEF 
        );
        
        addEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 1)); 
        addEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0)); 
        
        addLoreText(ChatColor.RED + "Боевой рацион российской армии");
        addLoreText(ChatColor.YELLOW + "Усиленный состав калорий и белков");
        addLoreText(ChatColor.YELLOW + "Быстрое восстановление сил");
        addLoreText(ChatColor.AQUA + "Эффекты: усиленная регенерация (15с), огнестойкость (60с)");
    }
} 