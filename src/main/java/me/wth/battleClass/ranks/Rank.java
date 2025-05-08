package me.wth.battleClass.ranks;

import org.bukkit.ChatColor;

/**
 * Класс, представляющий военный ранг
 */
public class Rank {
    private final String id;
    private final String displayName;
    private final ChatColor color;
    private final int level;
    private final boolean isCommander;
    
    /**
     * Конструктор для создания ранга
     * 
     * @param id уникальный идентификатор ранга
     * @param displayName отображаемое название ранга
     * @param color цвет ранга для отображения в чате
     * @param level уровень ранга (используется для сравнения рангов)
     * @param isCommander является ли ранг командирским (даёт доступ к планшету)
     */
    public Rank(String id, String displayName, ChatColor color, int level, boolean isCommander) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
        this.level = level;
        this.isCommander = isCommander;
    }
    
    /**
     * Получает идентификатор ранга
     * 
     * @return строковый идентификатор ранга
     */
    public String getId() {
        return id;
    }
    
    /**
     * Получает отображаемое название ранга
     * 
     * @return название ранга для отображения игрокам
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Получает цвет ранга
     * 
     * @return цвет для отображения в чате/табе
     */
    public ChatColor getColor() {
        return color;
    }
    
    /**
     * Получает уровень ранга
     * 
     * @return числовое значение уровня ранга
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Проверяет, является ли ранг командирским
     * 
     * @return true, если ранг даёт права командира
     */
    public boolean isCommander() {
        return isCommander;
    }
    
    /**
     * Форматированное отображение ранга с цветом
     * 
     * @return строка с цветным названием ранга
     */
    public String getFormattedName() {
        return color + displayName;
    }
    
    /**
     * Проверяет, равен ли данный ранг указанному
     * 
     * @param other другой ранг для сравнения
     * @return true, если ранги равны
     */
    public boolean equals(Rank other) {
        if (other == null) return false;
        return this.id.equals(other.id);
    }
    
    /**
     * Проверяет, выше ли данный ранг указанного
     * 
     * @param other другой ранг для сравнения
     * @return true, если текущий ранг выше
     */
    public boolean isHigherThan(Rank other) {
        if (other == null) return true;
        return this.level > other.level;
    }
    
    /**
     * Проверяет, ниже ли данный ранг указанного
     * 
     * @param other другой ранг для сравнения
     * @return true, если текущий ранг ниже
     */
    public boolean isLowerThan(Rank other) {
        if (other == null) return false;
        return this.level < other.level;
    }
} 