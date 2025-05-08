package me.wth.battleClass.mortars;

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
 * Класс для управления всеми минометами в плагине
 */
public class MortarManager {
    private final BattleClass plugin;
    private final Map<String, Mortar> mortarMap = new HashMap<>();
    
    /**
     * Конструктор менеджера минометов
     * 
     * @param plugin экземпляр основного плагина
     */
    public MortarManager(BattleClass plugin) {
        this.plugin = plugin;
        registerMortars();
    }
    
    /**
     * Регистрирует все миномёты в системе
     */
    private void registerMortars() {
        Mortar2B14Podnos podnos = new Mortar2B14Podnos(plugin);
        mortarMap.put(podnos.getId(), podnos);
        
        MortarM224 m224 = new MortarM224(plugin);
        mortarMap.put(m224.getId(), m224);
    }
    
    /**
     * Получение миномета по его идентификатору
     * 
     * @param id идентификатор миномета
     * @return миномет или null, если не найден
     */
    public Mortar getMortarById(String id) {
        return mortarMap.get(id);
    }
    
    /**
     * Получение миномета из ItemStack
     * 
     * @param itemStack предмет для проверки
     * @return миномет или null, если предмет не является минометом
     */
    public Mortar getMortarFromItemStack(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return null;
        }
        
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "mortar_id");
        
        if (container.has(key, PersistentDataType.STRING)) {
            String mortarId = container.get(key, PersistentDataType.STRING);
            return getMortarById(mortarId);
        }
        
        return null;
    }
    
    /**
     * Проверяет, может ли игрок использовать данный миномет в зависимости от фракции
     * 
     * @param player игрок для проверки
     * @param mortar миномет для проверки
     * @return true, если игрок может использовать миномет
     */
    public boolean canPlayerUseMortar(Player player, Mortar mortar) {
        String factionId = plugin.getRankManager().getPlayerFaction(player.getUniqueId());
        Faction playerFaction = Faction.getByID(factionId);
        
        if (playerFaction == null) {
            return false;
        }
        
        if (mortar instanceof Mortar2B14Podnos && playerFaction != Faction.RUSSIA) {
            return false;
        }
        
        if (mortar instanceof MortarM224 && playerFaction != Faction.USA) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Создает ItemStack с минометом для указанной фракции
     * 
     * @param faction фракция для которой создается миномет
     * @return ItemStack с минометом или null, если для фракции нет миномета
     */
    public ItemStack createMortarForFaction(Faction faction) {
        if (faction == Faction.RUSSIA) {
            return mortarMap.get("2b14_podnos").createItemStack();
        } else if (faction == Faction.USA) {
            return mortarMap.get("m224").createItemStack();
        }
        return null;
    }
    
    /**
     * Получает все доступные миномёты
     * 
     * @return карта всех миномётов
     */
    public Map<String, Mortar> getAllMortars() {
        return new HashMap<>(mortarMap);
    }
} 