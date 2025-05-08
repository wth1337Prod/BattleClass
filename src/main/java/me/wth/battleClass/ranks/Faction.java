package me.wth.battleClass.ranks;

/**
 * Перечисление доступных фракций в игре
 */
public enum Faction {
    /**
     * Российская Федерация
     */
    RUSSIA("ru", "РФ"),
    
    /**
     * Соединенные Штаты Америки
     */
    USA("us", "США");
    
    private final String id;
    private final String displayName;
    
    /**
     * Конструктор для фракции
     * 
     * @param id внутренний идентификатор фракции
     * @param displayName отображаемое название фракции
     */
    Faction(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    
    /**
     * Получает идентификатор фракции
     * 
     * @return идентификатор фракции
     */
    public String getId() {
        return id;
    }
    
    /**
     * Получает отображаемое название фракции
     * 
     * @return отображаемое название
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Получает фракцию по ее идентификатору
     * 
     * @param id идентификатор фракции
     * @return объект фракции или null, если не найдена
     */
    public static Faction getByID(String id) {
        if (id == null) return null;
        
        for (Faction faction : values()) {
            if (faction.id.equalsIgnoreCase(id)) {
                return faction;
            }
        }
        return null;
    }
} 