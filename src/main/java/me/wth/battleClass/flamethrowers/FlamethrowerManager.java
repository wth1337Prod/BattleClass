package me.wth.battleClass.flamethrowers;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.ranks.Faction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления всеми огнеметами в плагине
 */
public class FlamethrowerManager {
    private final BattleClass plugin;
    private final Map<String, Flamethrower> flamethrowerMap = new HashMap<>();
    
    /**
     * Конструктор менеджера огнеметов
     * 
     * @param plugin экземпляр основного плагина
     */
    public FlamethrowerManager(BattleClass plugin) {
        this.plugin = plugin;
        registerFlamethrowers();
    }
    
    /**
     * Регистрирует все огнеметы в системе
     */
    private void registerFlamethrowers() {
        RPO94Shmel shmel = new RPO94Shmel(plugin);
        flamethrowerMap.put(shmel.getId(), shmel);
        
        XM42Vulcan vulcan = new XM42Vulcan(plugin);
        flamethrowerMap.put(vulcan.getId(), vulcan);
    }
    
    /**
     * Получение огнемета по его идентификатору
     * 
     * @param id идентификатор огнемета
     * @return огнемет или null, если не найден
     */
    public Flamethrower getFlamethrowerById(String id) {
        return flamethrowerMap.get(id);
    }
    
    /**
     * Получение огнемета из ItemStack
     * 
     * @param itemStack предмет для проверки
     * @return огнемет или null, если предмет не является огнеметом
     */
    public Flamethrower getFlamethrowerFromItemStack(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return null;
        }
        
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "flamethrower_id");
        
        if (container.has(key, PersistentDataType.STRING)) {
            String flamethrowerID = container.get(key, PersistentDataType.STRING);
            return getFlamethrowerById(flamethrowerID);
        }
        
        return null;
    }
    
    /**
     * Проверяет, может ли игрок использовать данный огнемет в зависимости от фракции
     * 
     * @param player игрок для проверки
     * @param flamethrower огнемет для проверки
     * @return true, если игрок может использовать огнемет
     */
    public boolean canPlayerUseFlamethrower(Player player, Flamethrower flamethrower) {
        String factionId = plugin.getRankManager().getPlayerFaction(player.getUniqueId());
        Faction playerFaction = Faction.getByID(factionId);
        
        if (playerFaction == null) {
            return false;
        }
        
        if (flamethrower instanceof RPO94Shmel && playerFaction != Faction.RUSSIA) {
            return false;
        }
        
        if (flamethrower instanceof XM42Vulcan && playerFaction != Faction.USA) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Создает ItemStack с огнеметом для указанной фракции
     * 
     * @param faction фракция для которой создается огнемет
     * @return ItemStack с огнеметом или null, если для фракции нет огнемета
     */
    public ItemStack createFlamethrowerForFaction(Faction faction) {
        if (faction == Faction.RUSSIA) {
            return flamethrowerMap.get("rpo94_shmel").createItemStack();
        } else if (faction == Faction.USA) {
            return flamethrowerMap.get("xm42_vulcan").createItemStack();
        }
        return null;
    }
    
    /**
     * Получает все доступные огнеметы
     * 
     * @return карта всех огнеметов
     */
    public Map<String, Flamethrower> getAllFlamethrowers() {
        return new HashMap<>(flamethrowerMap);
    }
    
    /**
     * Создает канистру с топливом для огнеметов
     * 
     * @param amount количество топлива в канистре
     * @return ItemStack с канистрой
     */
    public ItemStack createFuelCanister(int amount) {
        ItemStack canister = new ItemStack(org.bukkit.Material.HONEYCOMB);
        ItemMeta meta = canister.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6Канистра с топливом");
            
            java.util.List<String> lore = new java.util.ArrayList<>();
            lore.add("§7Содержит §f" + amount + "§7 единиц топлива");
            lore.add("§7для заправки огнеметов");
            lore.add("");
            lore.add("§eПрисесть+ПКМ с огнеметом - заправить");
            
            meta.setLore(lore);
            
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LURE, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "fuel_amount"), PersistentDataType.INTEGER, amount);
            
            canister.setItemMeta(meta);
        }
        
        return canister;
    }
    
    /**
     * Получает количество топлива в канистре
     * 
     * @param canister канистра для проверки
     * @return количество топлива или 0, если предмет не является канистрой
     */
    public int getFuelAmountFromCanister(ItemStack canister) {
        if (canister == null || !canister.hasItemMeta() || canister.getType() != org.bukkit.Material.HONEYCOMB) {
            return 0;
        }
        
        ItemMeta meta = canister.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "fuel_amount");
        
        if (container.has(key, PersistentDataType.INTEGER)) {
            return container.get(key, PersistentDataType.INTEGER);
        }
        
        return 0;
    }
} 