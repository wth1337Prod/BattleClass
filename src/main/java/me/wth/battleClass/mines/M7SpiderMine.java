package me.wth.battleClass.mines;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Мина противопехотная XM-7 "Spider"
 * Производитель: Textron Defense Systems
 * Год принятия на вооружение: 2020
 * Страна: США
 * 
 * Современная американская противопехотная мина "умного" типа.
 * Оснащена сенсорами движения и дистанционным управлением.
 */
public class M7SpiderMine extends Mine {
    
    public M7SpiderMine() {
        super(
            "m7_spider", 
            ChatColor.BLUE + "XM-7 \"Spider\" противопехотная мина (США)", 
            60.0,   
            4.0,    
            10,     
            72000,  
            Material.SPIDER_EYE, 
            true,   
            "Textron Defense Systems", 
            "США", 
            2020,   
            true    
        );
        
        addLoreText(ChatColor.BLUE + "Современная \"умная\" противопехотная мина");
        addLoreText(ChatColor.YELLOW + "• Сенсоры движения");
        addLoreText(ChatColor.YELLOW + "• Расширенный радиус действия");
        addLoreText(ChatColor.YELLOW + "• Электронная система активации");
        addLoreText(ChatColor.YELLOW + "• Самодеактивация после часа работы");
    }
    
    /**
     * Дополнительная информация о мине
     * @return строка с дополнительной информацией
     */
    public String getAdditionalInfo() {
        return "XM-7 'Spider' - современная противопехотная мина с электронной системой активации. " +
               "Оснащена сенсорами движения, способна различать типы целей. " +
               "В целях безопасности имеет таймер самодеактивации спустя час после установки.";
    }
} 