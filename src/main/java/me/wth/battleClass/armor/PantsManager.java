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
 * Менеджер для управления камуфляжными штанами
 */
public class PantsManager {
    private final BattleClass plugin;
    private final Map<String, Pants> pants = new HashMap<>();
    
    /**
     * Конструктор менеджера штанов
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public PantsManager(BattleClass plugin) {
        this.plugin = plugin;
        registerPants();
    }
    
    /**
     * Регистрирует доступные камуфляжные штаны
     */
    private void registerPants() {
        registerPants(new CamouflagePantsForest(plugin));
    }
    
    /**
     * Регистрирует штаны в системе
     * 
     * @param pants экземпляр штанов
     */
    private void registerPants(Pants pants) {
        this.pants.put(pants.getId(), pants);
    }
    
    /**
     * Выдает штаны игроку
     * 
     * @param player игрок, которому выдаются штаны
     * @param pantsId идентификатор штанов
     */
    public void givePantsToPlayer(Player player, String pantsId) {
        Pants pants = this.pants.get(pantsId);
        
        if (pants != null) {
            ItemStack pantsItem = pants.createItemStack();
            player.getInventory().addItem(pantsItem);
            player.sendMessage("§aВы получили " + pants.getDisplayName());
        } else {
            player.sendMessage("§cШтаны " + pantsId + " не найдены!");
        }
    }
    
    /**
     * Проверяет, надеты ли на игрока камуфляжные штаны из плагина
     * 
     * @param player игрок для проверки
     * @return true, если на игроке есть камуфляжные штаны из плагина
     */
    public boolean hasPants(Player player) {
        ItemStack leggings = player.getInventory().getLeggings();
        if (leggings == null) return false;
        
        ItemMeta meta = leggings.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;
        
        return meta.getLore() != null && meta.getLore().stream()
                .anyMatch(line -> line.contains("§7Тип: §fКамуфляжные штаны"));
    }
    
    /**
     * Получает идентификатор штанов, которые надеты на игрока
     * 
     * @param player игрок для проверки
     * @return идентификатор штанов или null, если штаны не найдены
     */
    public String getPlayerPantsId(Player player) {
        ItemStack leggings = player.getInventory().getLeggings();
        if (leggings == null) return null;
        
        ItemMeta meta = leggings.getItemMeta();
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
     * Получает штаны по их идентификатору
     * 
     * @param pantsId идентификатор штанов
     * @return экземпляр штанов или null, если не найден
     */
    public Pants getPants(String pantsId) {
        return pants.get(pantsId);
    }
    
    /**
     * Получает штаны, которые надеты на игрока
     * 
     * @param player игрок для проверки
     * @return экземпляр штанов или null, если не найден
     */
    public Pants getPlayerPants(Player player) {
        String pantsId = getPlayerPantsId(player);
        if (pantsId == null) return null;
        
        return getPants(pantsId);
    }
    
    /**
     * Получает все зарегистрированные штаны
     * 
     * @return карта с идентификаторами и экземплярами штанов
     */
    public Map<String, Pants> getAllPants() {
        return pants;
    }
    
    /**
     * Получает список идентификаторов всех зарегистрированных штанов
     * 
     * @return список идентификаторов штанов
     */
    public List<String> getPantsIds() {
        return new ArrayList<>(pants.keySet());
    }
} 