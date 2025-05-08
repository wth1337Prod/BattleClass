package me.wth.battleClass.mines;

import me.wth.battleClass.BattleClass;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Менеджер для управления минами в плагине
 */
public class MineManager {
    private final BattleClass plugin;
    private final Map<String, Mine> mines;
    private final Map<String, PlacedMine> placedMines; 
    private final Map<Location, Set<String>> minesByLocation; 

    /**
     * Конструктор для менеджера мин
     * @param plugin экземпляр плагина
     */
    public MineManager(BattleClass plugin) {
        this.plugin = plugin;
        this.mines = new HashMap<>();
        this.placedMines = new HashMap<>();
        this.minesByLocation = new HashMap<>();
        
        registerMines();
    }

    /**
     * Регистрирует все типы мин
     */
    private void registerMines() {
        MPM3Mine mpm3Mine = new MPM3Mine();
        mines.put(mpm3Mine.getId(), mpm3Mine);
        
        M7SpiderMine m7SpiderMine = new M7SpiderMine();
        mines.put(m7SpiderMine.getId(), m7SpiderMine);
    }

    /**
     * Получает все зарегистрированные типы мин
     * @return карта с минами (id -> Mine)
     */
    public Map<String, Mine> getMines() {
        return mines;
    }
    
    /**
     * Получает список идентификаторов всех зарегистрированных мин
     * 
     * @return список идентификаторов мин
     */
    public List<String> getMineIds() {
        return new ArrayList<>(mines.keySet());
    }
    
    /**
     * Получает мину по её идентификатору
     * @param id идентификатор мины
     * @return объект мины или null, если не найдена
     */
    public Mine getMine(String id) {
        return mines.get(id);
    }
    
    /**
     * Выдает мину игроку
     * @param player игрок, которому нужно выдать мину
     * @param mineId идентификатор мины
     * @param amount количество мин
     */
    public void giveMineToPlayer(Player player, String mineId, int amount) {
        Mine mine = mines.get(mineId);
        
        if (mine != null) {
            ItemStack mineItem = mine.createItemStack(plugin);
            mineItem.setAmount(amount);
            
            player.getInventory().addItem(mineItem);
            player.sendMessage("§aВы получили §f" + mine.getDisplayName() + " §a(" + amount + " шт.)");
        } else {
            player.sendMessage("§cМина " + mineId + " не найдена!");
        }
    }
    
    /**
     * Проверяет, является ли предмет миной
     * @param item предмет для проверки
     * @return true, если предмет - мина, иначе false
     */
    public boolean isMine(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "mine_id");
        
        return container.has(key, PersistentDataType.STRING);
    }
    
    /**
     * Получает тип мины из предмета
     * @param item предмет для проверки
     * @return объект мины или null, если это не мина
     */
    public Mine getMineFromItem(ItemStack item) {
        if (!isMine(item)) {
            return null;
        }
        
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "mine_id");
        
        String mineId = container.get(key, PersistentDataType.STRING);
        
        return mineId != null ? mines.get(mineId) : null;
    }
    
    /**
     * Получает идентификатор экземпляра мины из предмета
     * @param item предмет для проверки
     * @return уникальный идентификатор экземпляра мины или null, если это не мина
     */
    public String getMineInstanceId(ItemStack item) {
        if (!isMine(item)) {
            return null;
        }
        
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "mine_instance_id");
        
        return container.get(key, PersistentDataType.STRING);
    }
    
    /**
     * Проверяет, есть ли мины в указанном месте
     * @param location место для проверки
     * @return true, если есть хотя бы одна мина, иначе false
     */
    public boolean hasMinesAt(Location location) {
        Location blockLocation = new Location(
            location.getWorld(), 
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ()
        );
        
        return minesByLocation.containsKey(blockLocation) && !minesByLocation.get(blockLocation).isEmpty();
    }
    
    /**
     * Получает все мины в указанном месте
     * @param location место для проверки
     * @return список мин или пустой список, если мин нет
     */
    public List<PlacedMine> getMinesAt(Location location) {
        Location blockLocation = new Location(
            location.getWorld(), 
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ()
        );
        
        Set<String> mineIds = minesByLocation.getOrDefault(blockLocation, Collections.emptySet());
        List<PlacedMine> result = new ArrayList<>(mineIds.size());
        
        for (String id : mineIds) {
            PlacedMine mine = placedMines.get(id);
            if (mine != null) {
                result.add(mine);
            }
        }
        
        return result;
    }
    
    /**
     * Получает все установленные мины
     * @return карта с установленными минами (instanceId -> PlacedMine)
     */
    public Map<String, PlacedMine> getAllPlacedMines() {
        return placedMines;
    }
    
    /**
     * Проверяет установленные мины на истечение времени жизни
     * Вызывается периодически для удаления старых мин
     */
    public void checkMinesToExpire() {
        long currentTime = System.currentTimeMillis();
        Set<String> minesToRemove = new HashSet<>();
        
        for (Map.Entry<String, PlacedMine> entry : placedMines.entrySet()) {
            PlacedMine mine = entry.getValue();
            
            if (mine.getExpirationTime() > 0 && mine.getExpirationTime() <= currentTime) {
                minesToRemove.add(entry.getKey());
            }
        }
        
        for (String id : minesToRemove) {
            PlacedMine mine = placedMines.get(id);
            
            if (mine != null) {
                mine.remove();
                removePlacedMine(id);
            }
        }
    }
    
    /**
     * Регистрирует установленную мину
     * @param instanceId идентификатор экземпляра мины
     * @param placedMine информация об установленной мине
     */
    public void registerPlacedMine(String instanceId, PlacedMine placedMine) {
        placedMines.put(instanceId, placedMine);
        
        Location loc = placedMine.getLocation();
        Location blockLocation = new Location(
            loc.getWorld(), 
            loc.getBlockX(),
            loc.getBlockY(),
            loc.getBlockZ()
        );
        
        minesByLocation.computeIfAbsent(blockLocation, k -> new HashSet<>()).add(instanceId);
    }
    
    /**
     * Удаляет информацию об установленной мине
     * @param instanceId идентификатор экземпляра мины
     */
    public void removePlacedMine(String instanceId) {
        PlacedMine mine = placedMines.remove(instanceId);
        
        if (mine != null) {
            Location loc = mine.getLocation();
            Location blockLocation = new Location(
                loc.getWorld(), 
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()
            );
            
            Set<String> minesAtLocation = minesByLocation.get(blockLocation);
            
            if (minesAtLocation != null) {
                minesAtLocation.remove(instanceId);
                
                if (minesAtLocation.isEmpty()) {
                    minesByLocation.remove(blockLocation);
                }
            }
        }
    }
} 