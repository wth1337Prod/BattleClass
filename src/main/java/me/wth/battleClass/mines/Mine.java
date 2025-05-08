package me.wth.battleClass.mines;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Базовый абстрактный класс для всех мин
 */
public abstract class Mine {
    protected String id;
    protected String displayName;
    protected List<String> lore;
    protected double damage;
    protected double blastRadius;
    protected int triggerDelay; 
    protected int lifeTime; 
    protected Material material;
    protected boolean isAntiPersonnel; 
    protected String manufacturer; 
    protected String country; 
    protected int year; 
    protected boolean isDetectable; 
    
    /**
     * Базовый конструктор для мин
     * 
     * @param id уникальный идентификатор мины
     * @param displayName отображаемое название мины
     * @param damage урон от взрыва мины
     * @param blastRadius радиус взрыва мины в блоках
     * @param triggerDelay задержка срабатывания в тиках после наступания на мину
     * @param lifeTime время жизни мины в тиках (0 - бесконечное)
     * @param material материал для отображения в инвентаре
     * @param isAntiPersonnel тип мины: противопехотная (true) или противотанковая (false)
     * @param manufacturer производитель мины
     * @param country страна производства
     * @param year год принятия на вооружение
     * @param isDetectable может ли мина быть обнаружена миноискателем
     */
    public Mine(String id, String displayName, double damage, double blastRadius, int triggerDelay, 
            int lifeTime, Material material, boolean isAntiPersonnel, String manufacturer, 
            String country, int year, boolean isDetectable) {
        this.id = id;
        this.displayName = displayName;
        this.damage = damage;
        this.blastRadius = blastRadius;
        this.triggerDelay = triggerDelay;
        this.lifeTime = lifeTime;
        this.material = material;
        this.isAntiPersonnel = isAntiPersonnel;
        this.manufacturer = manufacturer;
        this.country = country;
        this.year = year;
        this.isDetectable = isDetectable;
        
        this.lore = new ArrayList<>();
        
        this.lore.add(ChatColor.GRAY + "Урон: " + ChatColor.RED + damage);
        this.lore.add(ChatColor.GRAY + "Радиус взрыва: " + ChatColor.YELLOW + blastRadius + "м");
        this.lore.add(ChatColor.GRAY + "Задержка активации: " + ChatColor.AQUA + (triggerDelay / 20.0) + "с");
        this.lore.add("");
        this.lore.add(ChatColor.GRAY + "Тип: " + ChatColor.WHITE + (isAntiPersonnel ? "Противопехотная" : "Противотанковая"));
        this.lore.add(ChatColor.GRAY + "Производитель: " + ChatColor.WHITE + manufacturer);
        this.lore.add(ChatColor.GRAY + "Страна: " + ChatColor.WHITE + country);
        this.lore.add(ChatColor.GRAY + "Год: " + ChatColor.WHITE + year);
        this.lore.add("");
        this.lore.add(ChatColor.GRAY + "ПКМ - установить мину");
    }

    /**
     * Получает идентификатор мины
     * @return идентификатор мины
     */
    public String getId() {
        return id;
    }

    /**
     * Получает отображаемое название мины
     * @return отображаемое название мины
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Получает урон от взрыва мины
     * @return урон от взрыва мины
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Получает радиус взрыва мины
     * @return радиус взрыва мины в блоках
     */
    public double getBlastRadius() {
        return blastRadius;
    }

    /**
     * Получает задержку срабатывания мины
     * @return задержка срабатывания в тиках
     */
    public int getTriggerDelay() {
        return triggerDelay;
    }

    /**
     * Получает время жизни мины
     * @return время жизни мины в тиках (0 - бесконечное)
     */
    public int getLifeTime() {
        return lifeTime;
    }

    /**
     * Проверяет, является ли мина противопехотной
     * @return true, если мина противопехотная, иначе false
     */
    public boolean isAntiPersonnel() {
        return isAntiPersonnel;
    }

    /**
     * Проверяет, может ли мина быть обнаружена миноискателем
     * @return true, если мина может быть обнаружена, иначе false
     */
    public boolean isDetectable() {
        return isDetectable;
    }

    /**
     * Добавляет дополнительную строку в описание мины
     * @param text текст для добавления в описание
     */
    protected void addLoreText(String text) {
        lore.add(text);
    }

    /**
     * Создает ItemStack для мины, который можно дать игроку
     * @param plugin экземпляр плагина для создания NamespacedKey
     * @return ItemStack для мины
     */
    public ItemStack createItemStack(JavaPlugin plugin) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            NamespacedKey key = new NamespacedKey(plugin, "mine_id");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, id);
            
            NamespacedKey instanceKey = new NamespacedKey(plugin, "mine_instance_id");
            container.set(instanceKey, PersistentDataType.STRING, UUID.randomUUID().toString());
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
} 