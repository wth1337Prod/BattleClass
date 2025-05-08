package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Менеджер для управления ботинками
 */
public class BootsManager {
    private final BattleClass plugin;
    private final Map<String, Boots> boots = new HashMap<>();
    
    /**
     * Конструктор менеджера ботинок
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public BootsManager(BattleClass plugin) {
        this.plugin = plugin;
        registerBoots();
    }
    
    /**
     * Регистрирует доступные ботинки
     */
    private void registerBoots() {
        registerBoots(new RussianBootsRatnik(plugin));
        
        registerBoots(new AmericanBootsCombat(plugin));
    }
    
    /**
     * Регистрирует ботинки в системе
     * 
     * @param boots экземпляр ботинок
     */
    private void registerBoots(Boots boots) {
        this.boots.put(boots.getId(), boots);
    }
    
    /**
     * Выдает ботинки игроку
     * 
     * @param player игрок, которому выдаются ботинки
     * @param bootsId идентификатор ботинок
     */
    public void giveBootsToPlayer(Player player, String bootsId) {
        Boots boots = this.boots.get(bootsId);
        
        if (boots != null) {
            ItemStack bootsItem = boots.createItemStack();
            player.getInventory().addItem(bootsItem);
            player.sendMessage("§aВы получили " + boots.getDisplayName());
        } else {
            player.sendMessage("§cБотинки " + bootsId + " не найдены!");
        }
    }
    
    /**
     * Проверяет, надеты ли на игрока ботинки из плагина
     * 
     * @param player игрок для проверки
     * @return true, если на игроке есть ботинки из плагина
     */
    public boolean hasBoots(Player player) {
        ItemStack boots = player.getInventory().getBoots();
        if (boots == null) return false;
        
        ItemMeta meta = boots.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;
        
        return meta.getLore() != null && meta.getLore().stream()
                .anyMatch(line -> line.contains("§7Тип: §fБоевые ботинки"));
    }
    
    /**
     * Получает идентификатор ботинок, которые надеты на игрока
     * 
     * @param player игрок для проверки
     * @return идентификатор ботинок или null, если ботинки не найдены
     */
    public String getPlayerBootsId(Player player) {
        ItemStack boots = player.getInventory().getBoots();
        if (boots == null) return null;
        
        ItemMeta meta = boots.getItemMeta();
        if (meta == null || !meta.hasLore()) return null;
        
        if (meta.getLore() != null) {
            for (String line : meta.getLore()) {
                if (line.startsWith("§8Идентификатор: §7")) {
                    return line.substring("§8Идентификатор: §7".length());
                }
            }
        }
        
        return null;
    }
    
    /**
     * Получает ботинки по их идентификатору
     * 
     * @param bootsId идентификатор ботинок
     * @return экземпляр ботинок или null, если не найден
     */
    public Boots getBoots(String bootsId) {
        return boots.get(bootsId);
    }
    
    /**
     * Получает ботинки, которые надеты на игрока
     * 
     * @param player игрок для проверки
     * @return экземпляр ботинок или null, если не найден
     */
    public Boots getPlayerBoots(Player player) {
        String bootsId = getPlayerBootsId(player);
        if (bootsId == null) return null;
        
        return getBoots(bootsId);
    }
    
    /**
     * Получает все зарегистрированные ботинки
     * 
     * @return карта с идентификаторами и экземплярами ботинок
     */
    public Map<String, Boots> getAllBoots() {
        return boots;
    }
    
    /**
     * Получает список идентификаторов всех зарегистрированных ботинок
     * 
     * @return список идентификаторов ботинок
     */
    public List<String> getBootsIds() {
        return new ArrayList<>(boots.keySet());
    }
} 