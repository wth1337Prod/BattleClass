package me.wth.battleClass.grenades;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class M67Grenade extends Grenade {
    
    public M67Grenade() {
        super(
            "m67", 
            ChatColor.GREEN + "М67 осколочная граната (США)", 
            65.0,   
            6.0,    
            60,     
            Material.FIREWORK_STAR 
        );
        
        addLoreText(ChatColor.DARK_GREEN + "Стандартная осколочная граната армии США");
        addLoreText(ChatColor.YELLOW + "Среднее время детонации, хороший радиус поражения");
    }
} 