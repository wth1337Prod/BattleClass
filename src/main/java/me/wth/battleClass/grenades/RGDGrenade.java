package me.wth.battleClass.grenades;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class RGDGrenade extends Grenade {
    
    public RGDGrenade() {
        super(
            "rgd5", 
            ChatColor.RED + "РГД-5 осколочная граната (РФ)", 
            70.0,   
            5.0,    
            40,     
            Material.FIREWORK_STAR 
        );
        
        addLoreText(ChatColor.DARK_RED + "Противопехотная осколочная граната РФ");
        addLoreText(ChatColor.YELLOW + "Быстрая детонация, высокий урон, компактный радиус");
    }
} 