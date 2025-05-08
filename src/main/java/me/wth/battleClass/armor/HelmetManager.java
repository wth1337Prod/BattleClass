package me.wth.battleClass.armor;

import me.wth.battleClass.BattleClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Менеджер для управления шлемами
 */
public class HelmetManager {
    private final BattleClass plugin;
    private final Map<String, Helmet> helmets = new HashMap<>();
    private final Map<UUID, String> playerHelmets = new HashMap<>();
    
    /**
     * Конструктор менеджера шлемов
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public HelmetManager(BattleClass plugin) {
        this.plugin = plugin;
        registerHelmets();
    }
    
    /**
     * Регистрирует доступные шлемы
     */
    private void registerHelmets() {
        registerHelmet(new RussianHelmet6B47(plugin));
        
        registerHelmet(new AmericanHelmetECH(plugin));
    }
    
    /**
     * Регистрирует шлем в системе
     * 
     * @param helmet экземпляр шлема
     */
    private void registerHelmet(Helmet helmet) {
        helmets.put(helmet.getId(), helmet);
    }
    
    /**
     * Выдает шлем игроку
     * 
     * @param player игрок, которому выдается шлем
     * @param helmetId идентификатор шлема
     */
    public void giveHelmetToPlayer(Player player, String helmetId) {
        Helmet helmet = helmets.get(helmetId);
        
        if (helmet != null) {
            ItemStack helmetItem = helmet.createItemStack();
            player.getInventory().addItem(helmetItem);
            player.sendMessage("§aВы получили " + helmet.getDisplayName());
        } else {
            player.sendMessage("§cШлем " + helmetId + " не найден!");
        }
    }
    
    /**
     * Проверяет, надет ли на игрока шлем
     * 
     * @param player игрок для проверки
     * @return true, если на игроке есть шлем
     */
    public boolean hasHelmet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null) return false;
        
        ItemMeta meta = helmet.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;
        
        return meta.getLore() != null && meta.getLore().stream()
                .anyMatch(line -> line.contains("§7Тип: §fВоенный шлем"));
    }
    
    /**
     * Получает идентификатор шлема, который надет на игрока
     * 
     * @param player игрок для проверки
     * @return идентификатор шлема или null, если шлем не найден
     */
    public String getPlayerHelmetId(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null) return null;
        
        ItemMeta meta = helmet.getItemMeta();
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
     * Получает шлем по его идентификатору
     * 
     * @param helmetId идентификатор шлема
     * @return экземпляр шлема или null, если не найден
     */
    public Helmet getHelmet(String helmetId) {
        return helmets.get(helmetId);
    }
    
    /**
     * Получает шлем, который надет на игрока
     * 
     * @param player игрок для проверки
     * @return экземпляр шлема или null, если не найден
     */
    public Helmet getPlayerHelmet(Player player) {
        String helmetId = getPlayerHelmetId(player);
        if (helmetId == null) return null;
        
        return getHelmet(helmetId);
    }
    
    /**
     * Получает все зарегистрированные шлемы
     * 
     * @return карта с идентификаторами и экземплярами шлемов
     */
    public Map<String, Helmet> getHelmets() {
        return helmets;
    }
    
    /**
     * Получает список идентификаторов всех зарегистрированных шлемов
     * 
     * @return список идентификаторов шлемов
     */
    public List<String> getHelmetIds() {
        return new ArrayList<>(helmets.keySet());
    }
} 