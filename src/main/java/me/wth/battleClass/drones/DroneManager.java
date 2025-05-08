package me.wth.battleClass.drones;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.ranks.Faction;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления всеми дронами в плагине
 */
public class DroneManager {
    private final BattleClass plugin;
    private final Map<String, Drone> droneMap = new HashMap<>();
    
    /**
     * Конструктор менеджера дронов
     * 
     * @param plugin экземпляр основного плагина
     */
    public DroneManager(BattleClass plugin) {
        this.plugin = plugin;
        registerDrones();
    }
    
    /**
     * Регистрирует все дроны в системе
     */
    private void registerDrones() {
        LancetKamikaze lancet = new LancetKamikaze(plugin);
        droneMap.put(lancet.getId(), lancet);
        
        PredatorBattleDrone predator = new PredatorBattleDrone(plugin);
        droneMap.put(predator.getId(), predator);
    }
    
    /**
     * Получение дрона по его идентификатору
     * 
     * @param id идентификатор дрона
     * @return дрон или null, если не найден
     */
    public Drone getDroneById(String id) {
        return droneMap.get(id);
    }
    
    /**
     * Получение дрона из ItemStack
     * 
     * @param itemStack предмет для проверки
     * @return дрон или null, если предмет не является дроном
     */
    public Drone getDroneFromItemStack(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return null;
        }
        
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "drone_id");
        
        if (container.has(key, PersistentDataType.STRING)) {
            String droneID = container.get(key, PersistentDataType.STRING);
            return getDroneById(droneID);
        }
        
        return null;
    }
    
    /**
     * Проверяет, может ли игрок использовать данный дрон в зависимости от фракции
     * 
     * @param player игрок для проверки
     * @param drone дрон для проверки
     * @return true, если игрок может использовать дрон
     */
    public boolean canPlayerUseDrone(Player player, Drone drone) {
        String factionId = plugin.getRankManager().getPlayerFaction(player.getUniqueId());
        Faction playerFaction = Faction.getByID(factionId);
        
        if (playerFaction == null) {
            return false;
        }
        
        if (drone instanceof LancetKamikaze && playerFaction != Faction.RUSSIA) {
            return false;
        }
        
        if (drone instanceof PredatorBattleDrone && playerFaction != Faction.USA) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Создает ItemStack с дроном для указанной фракции
     * 
     * @param faction фракция для которой создается дрон
     * @param isKamikaze true для создания дрона-камикадзе, false для боевого дрона
     * @return ItemStack с дроном или null, если для фракции нет нужного типа дрона
     */
    public ItemStack createDroneForFaction(Faction faction, boolean isKamikaze) {
        if (faction == Faction.RUSSIA && isKamikaze) {
            return droneMap.get("lancet_kamikaze").createItemStack();
        } else if (faction == Faction.USA && !isKamikaze) {
            return droneMap.get("predator_battle_drone").createItemStack();
        }
        return null;
    }
    
    /**
     * Получает все доступные дроны
     * 
     * @return карта всех дронов
     */
    public Map<String, Drone> getAllDrones() {
        return new HashMap<>(droneMap);
    }
    
    /**
     * Создает батарею для дронов
     * 
     * @param amount количество заряда в батарее
     * @return ItemStack с батареей
     */
    public ItemStack createDroneBattery(int amount) {
        ItemStack battery = new ItemStack(Material.REDSTONE);
        ItemMeta meta = battery.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§bБатарея для дрона");
            
            java.util.List<String> lore = new java.util.ArrayList<>();
            lore.add("§7Содержит §f" + amount + "§7 единиц заряда");
            lore.add("§7для питания дронов");
            lore.add("");
            lore.add("§eПрисесть+ПКМ с дроном - зарядить");
            
            meta.setLore(lore);
            
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "battery_charge"), PersistentDataType.INTEGER, amount);
            
            battery.setItemMeta(meta);
        }
        
        return battery;
    }
    
    /**
     * Получает количество заряда в батарее
     * 
     * @param battery батарея для проверки
     * @return количество заряда или 0, если предмет не является батареей
     */
    public int getChargeFromBattery(ItemStack battery) {
        if (battery == null || !battery.hasItemMeta() || battery.getType() != Material.REDSTONE) {
            return 0;
        }
        
        ItemMeta meta = battery.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "battery_charge");
        
        if (container.has(key, PersistentDataType.INTEGER)) {
            return container.get(key, PersistentDataType.INTEGER);
        }
        
        return 0;
    }
    
    /**
     * Получает дрон-камикадзе "Ланцет"
     * 
     * @return экземпляр дрона-камикадзе
     */
    public LancetKamikaze getLancetDrone() {
        return (LancetKamikaze) droneMap.get("lancet_kamikaze");
    }
    
    /**
     * Получает боевой дрон "Predator"
     * 
     * @return экземпляр боевого дрона
     */
    public PredatorBattleDrone getPredatorDrone() {
        return (PredatorBattleDrone) droneMap.get("predator_battle_drone");
    }
} 