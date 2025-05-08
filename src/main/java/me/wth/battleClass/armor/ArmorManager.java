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
 * Менеджер для управления бронежилетами
 */
public class ArmorManager {
    private final BattleClass plugin;
    private final Map<String, Armor> armors = new HashMap<>();
    private final Map<UUID, String> playerArmors = new HashMap<>();
    
    /**
     * Конструктор менеджера бронежилетов
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public ArmorManager(BattleClass plugin) {
        this.plugin = plugin;
        registerArmors();
    }
    
    /**
     * Регистрирует доступные бронежилеты
     */
    private void registerArmors() {
        registerArmor(new RussianVest6B45(plugin));
        
        registerArmor(new AmericanVestIOTV(plugin));
    }
    
    /**
     * Регистрирует бронежилет в системе
     * 
     * @param armor экземпляр бронежилета
     */
    private void registerArmor(Armor armor) {
        armors.put(armor.getId(), armor);
    }
    
    /**
     * Выдает бронежилет игроку
     * 
     * @param player игрок, которому выдается бронежилет
     * @param armorId идентификатор бронежилета
     */
    public void giveArmorToPlayer(Player player, String armorId) {
        Armor armor = armors.get(armorId);
        
        if (armor != null) {
            ItemStack armorItem = armor.createItemStack();
            player.getInventory().addItem(armorItem);
            player.sendMessage("§aВы получили " + armor.getDisplayName());
        } else {
            player.sendMessage("§cБронежилет " + armorId + " не найден!");
        }
    }
    
    /**
     * Проверяет, одет ли на игрока бронежилет
     * 
     * @param player игрок для проверки
     * @return true, если на игроке есть бронежилет
     */
    public boolean hasArmor(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null) return false;
        
        ItemMeta meta = chestplate.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;
        
        return meta.getLore() != null && meta.getLore().stream()
                .anyMatch(line -> line.contains("§7Тип: §fБронежилет"));
    }
    
    /**
     * Получает идентификатор бронежилета, который одет на игрока
     * 
     * @param player игрок для проверки
     * @return идентификатор бронежилета или null, если бронежилет не найден
     */
    public String getPlayerArmorId(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null) return null;
        
        ItemMeta meta = chestplate.getItemMeta();
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
     * Получает бронежилет по его идентификатору
     * 
     * @param armorId идентификатор бронежилета
     * @return экземпляр бронежилета или null, если не найден
     */
    public Armor getArmor(String armorId) {
        return armors.get(armorId);
    }
    
    /**
     * Получает бронежилет, который одет на игрока
     * 
     * @param player игрок для проверки
     * @return экземпляр бронежилета или null, если не найден
     */
    public Armor getPlayerArmor(Player player) {
        String armorId = getPlayerArmorId(player);
        if (armorId == null) return null;
        
        return getArmor(armorId);
    }
    
    /**
     * Получает все зарегистрированные бронежилеты
     * 
     * @return карта с идентификаторами и экземплярами бронежилетов
     */
    public Map<String, Armor> getArmors() {
        return armors;
    }
    
    /**
     * Получает список идентификаторов всех зарегистрированных бронежилетов
     * 
     * @return список идентификаторов бронежилетов
     */
    public List<String> getArmorIds() {
        return new ArrayList<>(armors.keySet());
    }
} 