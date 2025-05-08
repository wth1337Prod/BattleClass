package me.wth.battleClass.rations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MRERation extends Ration {
    
    public MRERation() {
        super(
            "mre", 
            ChatColor.BLUE + "MRE - Meal Ready to Eat (США)", 
            8,      
            4.0,    
            6.0,    
            60,     
            Material.BREAD 
        );
        
        addEffect(new PotionEffect(PotionEffectType.REGENERATION, 400, 0)); 
        addEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 0)); 
        
        addLoreText(ChatColor.BLUE + "Боевой рацион армии США");
        addLoreText(ChatColor.YELLOW + "Включает основное блюдо, гарнир, десерт");
        addLoreText(ChatColor.YELLOW + "Повышенная энергетическая ценность");
        addLoreText(ChatColor.AQUA + "Эффекты: регенерация (20с), защита (30с)");
    }
} 