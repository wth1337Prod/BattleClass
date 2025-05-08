package me.wth.battleClass.medical;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.medical.items.Adrenaline;
import me.wth.battleClass.medical.items.Bandage;
import me.wth.battleClass.medical.items.MedKit;
import me.wth.battleClass.medical.items.Painkiller;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Менеджер для управления медицинскими предметами
 */
public class MedicalManager {
    private final BattleClass plugin;
    private final Map<String, MedicalItem> medicalItems = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> usingMedicalItems = new HashMap<>();
    
    /**
     * Конструктор менеджера медицинских предметов
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public MedicalManager(BattleClass plugin) {
        this.plugin = plugin;
        registerMedicalItems();
    }
    
    /**
     * Регистрирует доступные медицинские предметы
     */
    private void registerMedicalItems() {
        registerMedicalItem(new Bandage(plugin));
        
        registerMedicalItem(new MedKit(plugin));
        
        registerMedicalItem(new Painkiller(plugin));
        
        registerMedicalItem(new Adrenaline(plugin));
    }
    
    /**
     * Регистрирует медицинский предмет в системе
     * 
     * @param medicalItem экземпляр медицинского предмета
     */
    private void registerMedicalItem(MedicalItem medicalItem) {
        medicalItems.put(medicalItem.getId(), medicalItem);
    }
    
    /**
     * Выдает медицинский предмет игроку
     * 
     * @param player игрок, которому выдается предмет
     * @param medicalId идентификатор медицинского предмета
     * @param amount количество предметов
     */
    public void giveMedicalItemToPlayer(Player player, String medicalId, int amount) {
        MedicalItem medicalItem = medicalItems.get(medicalId);
        
        if (medicalItem != null) {
            ItemStack medicalItemStack = medicalItem.createItemStack();
            medicalItemStack.setAmount(amount);
            player.getInventory().addItem(medicalItemStack);
            player.sendMessage("§aВы получили " + medicalItem.getDisplayName() + " §ax" + amount);
        } else {
            player.sendMessage("§cМедицинский предмет " + medicalId + " не найден!");
        }
    }
    
    /**
     * Проверяет, является ли предмет медицинским
     * 
     * @param item предмет для проверки
     * @return true, если предмет является медицинским
     */
    public boolean isMedicalItem(ItemStack item) {
        if (item == null) return false;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        
        NamespacedKey key = new NamespacedKey(plugin, "medical_id");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        return container.has(key, PersistentDataType.STRING);
    }
    
    /**
     * Получает идентификатор медицинского предмета из ItemStack
     * 
     * @param item предмет для получения идентификатора
     * @return идентификатор или null, если не найден
     */
    public String getMedicalItemId(ItemStack item) {
        if (item == null) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        
        NamespacedKey key = new NamespacedKey(plugin, "medical_id");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        if (container.has(key, PersistentDataType.STRING)) {
            return container.get(key, PersistentDataType.STRING);
        }
        
        return null;
    }
    
    /**
     * Получает медицинский предмет по его идентификатору
     * 
     * @param medicalId идентификатор медицинского предмета
     * @return экземпляр медицинского предмета или null, если не найден
     */
    public MedicalItem getMedicalItem(String medicalId) {
        return medicalItems.get(medicalId);
    }
    
    /**
     * Получает все зарегистрированные медицинские предметы
     * 
     * @return карта с идентификаторами и экземплярами медицинских предметов
     */
    public Map<String, MedicalItem> getMedicalItems() {
        return medicalItems;
    }
    
    /**
     * Отмечает, что игрок начал использовать медицинский предмет
     * 
     * @param player игрок, использующий предмет
     * @param medicalId идентификатор медицинского предмета
     * @param taskId ID задачи планировщика для отслеживания
     */
    public void setPlayerUsingMedicalItem(Player player, String medicalId, int taskId) {
        UUID playerId = player.getUniqueId();
        usingMedicalItems.computeIfAbsent(playerId, k -> new HashMap<>())
                         .put(medicalId, taskId);
    }
    
    /**
     * Проверяет, использует ли игрок медицинский предмет
     * 
     * @param player игрок для проверки
     * @param medicalId идентификатор медицинского предмета (или null для любого)
     * @return true, если игрок использует указанный предмет
     */
    public boolean isPlayerUsingMedicalItem(Player player, String medicalId) {
        UUID playerId = player.getUniqueId();
        Map<String, Integer> playerItems = usingMedicalItems.get(playerId);
        
        if (playerItems == null) return false;
        
        if (medicalId != null) {
            return playerItems.containsKey(medicalId);
        } else {
            return !playerItems.isEmpty();
        }
    }
    
    /**
     * Получает ID задачи планировщика для используемого предмета
     * 
     * @param player игрок
     * @param medicalId идентификатор предмета
     * @return ID задачи или -1, если не найдено
     */
    public int getTaskId(Player player, String medicalId) {
        UUID playerId = player.getUniqueId();
        Map<String, Integer> playerItems = usingMedicalItems.get(playerId);
        
        if (playerItems == null || !playerItems.containsKey(medicalId)) {
            return -1;
        }
        
        return playerItems.get(medicalId);
    }
    
    /**
     * Отмечает, что игрок закончил использовать медицинский предмет
     * 
     * @param player игрок
     * @param medicalId идентификатор предмета
     */
    public void removePlayerUsingMedicalItem(Player player, String medicalId) {
        UUID playerId = player.getUniqueId();
        Map<String, Integer> playerItems = usingMedicalItems.get(playerId);
        
        if (playerItems != null) {
            playerItems.remove(medicalId);
            
            if (playerItems.isEmpty()) {
                usingMedicalItems.remove(playerId);
            }
        }
    }
    
    /**
     * Получает список идентификаторов всех зарегистрированных медицинских предметов
     * 
     * @return список идентификаторов медицинских предметов
     */
    public List<String> getMedicalItemIds() {
        return new ArrayList<>(medicalItems.keySet());
    }
} 