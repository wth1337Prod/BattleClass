package me.wth.battleClass.tablet;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Слушатель событий для обработки взаимодействия с военным планшетом
 */
public class TabletListener implements Listener {
    private final BattleClass plugin;
    private final TabletManager tabletManager;
    private final TabletGUI tabletGUI;
    
    private final Map<UUID, String> openedTablets = new HashMap<>();
    
    private final Map<UUID, Integer> mapZoomLevels = new HashMap<>();
    
    private static final int MIN_ZOOM_LEVEL = 1;
    private static final int MAX_ZOOM_LEVEL = 3;
    
    /**
     * Конструктор для TabletListener
     * 
     * @param plugin экземпляр главного класса плагина
     * @param tabletManager менеджер планшетов
     * @param tabletGUI интерфейс планшета
     */
    public TabletListener(BattleClass plugin, TabletManager tabletManager, TabletGUI tabletGUI) {
        this.plugin = plugin;
        this.tabletManager = tabletManager;
        this.tabletGUI = tabletGUI;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Обрабатывает событие взаимодействия игрока с предметом
     * Открывает интерфейс планшета при нажатии ПКМ
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        ItemStack item = event.getItem();
        
        if (item != null && tabletManager.isTablet(item)) {
            if (!plugin.getRankManager().canUseTablet(player)) {
                event.setCancelled(true);
                
                player.sendMessage(ChatColor.RED + "У вас нет доступа к военному планшету! " + 
                        "Только командиры имеют право использовать этот предмет.");
                
                if (plugin.getRankManager().getPlayerRank(player) != null) {
                    player.sendMessage(ChatColor.RED + "Ваш текущий ранг: " + 
                            plugin.getRankManager().getPlayerRank(player).getFormattedName() + 
                            ChatColor.RED + " (недостаточно для использования планшета)");
                }
                
                return;
            }
            
            String tabletType = tabletManager.getTabletType(item);
            
            tabletGUI.openMainMenu(player, tabletType);
            
            openedTablets.put(player.getUniqueId(), tabletType);
            
            event.setCancelled(true);
        }
    }
    
    /**
     * Обрабатывает событие закрытия инвентаря
     * Очищает информацию о том, какой планшет был открыт у игрока
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            
            if (openedTablets.containsKey(player.getUniqueId())) {
                openedTablets.remove(player.getUniqueId());
            }
        }
    }
    
    /**
     * Обрабатывает клики в инвентаре
     * Обрабатывает взаимодействие с элементами меню планшета
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        boolean isTabletInventory = title.contains("Планшет") || title.contains("планшет") || 
                                   title.contains("Травмы") || title.contains("Вооружение") || 
                                   title.contains("Боеприпасы") || title.contains("Экипировка") ||
                                   title.contains("Медикаменты") || title.contains("Тактическая карта") ||
                                   title.contains("Тактические заметки");
                                   
        if (isTabletInventory) {
            event.setCancelled(true);
            
            ItemStack clickedItem = event.getCurrentItem();
            
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }
            
            String tabletType = "us"; 
            if (title.contains("РФ") || title.contains("красный")) {
                tabletType = "ru";
            }
            
            if (!openedTablets.containsKey(player.getUniqueId())) {
                openedTablets.put(player.getUniqueId(), tabletType);
            } else {
                tabletType = openedTablets.get(player.getUniqueId());
            }
            
            String itemName = "";
            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                itemName = clickedItem.getItemMeta().getDisplayName();
            }
            
            if (clickedItem.getType() == Material.BARRIER && itemName.contains("Закрыть")) {
                player.closeInventory();
            } else if (clickedItem.getType() == Material.ARROW && itemName.contains("Назад")) {
                tabletGUI.openMainMenu(player, tabletType);
            } else if (title.contains("Планшет США") || title.contains("Планшет РФ")) {
                handleMainMenuClick(player, clickedItem, tabletType);
            } else if (title.contains("Тактическая карта")) {
                handleMapMenuClick(player, clickedItem, tabletType);
            } else if (title.contains("Травмы")) {
                handleInjuriesMenuClick(player, clickedItem, tabletType);
            } else if (title.contains("Вооружение")) {
                handleWeaponsMenuClick(player, clickedItem, tabletType);
            } else if (title.contains("Боеприпасы")) {
                handleAmmoMenuClick(player, clickedItem, tabletType);
            } else if (title.contains("Экипировка")) {
                handleArmorMenuClick(player, clickedItem, tabletType);
            } else if (title.contains("Медикаменты")) {
                handleMedicalMenuClick(player, clickedItem, tabletType);
            } else if (title.contains("Тактические заметки")) {
                handleNotesMenuClick(player, clickedItem, tabletType);
            }
        }
    }
    
    /**
     * Обрабатывает клик в главном меню планшета
     */
    private void handleMainMenuClick(Player player, ItemStack clickedItem, String tabletType) {
        if (clickedItem.getType() == Material.BARRIER) {
            player.closeInventory();
        } else if (clickedItem.getType() == Material.REDSTONE) {
            tabletGUI.openInjuriesDetails(player, tabletType);
        } else if (clickedItem.getType() == Material.ARROW) {
            tabletGUI.openAmmoDetails(player, tabletType);
        } else if (clickedItem.getType() == Material.IRON_SWORD) {
            tabletGUI.openWeaponsDetails(player, tabletType);
        } else if (clickedItem.getType() == Material.IRON_CHESTPLATE) {
            tabletGUI.openArmorDetails(player, tabletType);
        } else if (clickedItem.getType() == Material.GHAST_TEAR) {
            tabletGUI.openMedicalDetails(player, tabletType);
        } else if (clickedItem.getType() == Material.FILLED_MAP) {
            tabletGUI.openMapDetails(player, tabletType);
        } else if (clickedItem.getType() == Material.NOTE_BLOCK) {
            player.sendMessage("§aОткрытие радиосвязи...");
        } else if (clickedItem.getType() == Material.PAPER) {
            player.sendMessage("§aОткрытие информации о задании...");
        } else if (clickedItem.getType() == Material.WRITABLE_BOOK) {
            tabletGUI.openNotesDetails(player, tabletType);
        }
    }
    
    /**
     * Обрабатывает клик в меню травм
     */
    private void handleInjuriesMenuClick(Player player, ItemStack clickedItem, String tabletType) {
        if (clickedItem.getType() == Material.ARROW) {
            player.sendMessage("§aВозвращаемся в главное меню из травм...");
            tabletGUI.openMainMenu(player, tabletType);
        }
    }
    
    /**
     * Обрабатывает клик в меню оружия
     */
    private void handleWeaponsMenuClick(Player player, ItemStack clickedItem, String tabletType) {
        if (clickedItem.getType() == Material.ARROW) {
            player.sendMessage("§aВозвращаемся в главное меню из оружия...");
            tabletGUI.openMainMenu(player, tabletType);
        }
    }
    
    /**
     * Обрабатывает клик в меню боеприпасов
     */
    private void handleAmmoMenuClick(Player player, ItemStack clickedItem, String tabletType) {
        if (clickedItem.getType() == Material.ARROW) {
            player.sendMessage("§aВозвращаемся в главное меню из боеприпасов...");
            tabletGUI.openMainMenu(player, tabletType);
        }
    }
    
    /**
     * Обрабатывает клик в меню экипировки
     */
    private void handleArmorMenuClick(Player player, ItemStack clickedItem, String tabletType) {
        if (clickedItem.getType() == Material.ARROW) {
            player.sendMessage("§aВозвращаемся в главное меню из экипировки...");
            tabletGUI.openMainMenu(player, tabletType);
        }
    }
    
    /**
     * Обрабатывает клик в меню медикаментов
     */
    private void handleMedicalMenuClick(Player player, ItemStack clickedItem, String tabletType) {
        if (clickedItem.getType() == Material.ARROW) {
            player.sendMessage("§aВозвращаемся в главное меню из медикаментов...");
            tabletGUI.openMainMenu(player, tabletType);
        }
    }
    
    /**
     * Обрабатывает клик в меню карты
     */
    private void handleMapMenuClick(Player player, ItemStack clickedItem, String tabletType) {
        if (!mapZoomLevels.containsKey(player.getUniqueId())) {
            mapZoomLevels.put(player.getUniqueId(), 1); 
        }
        
        int currentZoom = mapZoomLevels.get(player.getUniqueId());
        
        if (clickedItem.getType() == Material.ARROW) {
            tabletGUI.openMainMenu(player, tabletType);
        } else if (clickedItem.getType() == Material.ENDER_EYE) {
            if (currentZoom < MAX_ZOOM_LEVEL) {
                currentZoom++;
                mapZoomLevels.put(player.getUniqueId(), currentZoom);
                player.sendMessage("§aПриближение карты (x" + currentZoom + ")");
                
                tabletGUI.openMapDetails(player, tabletType);
            } else {
                player.sendMessage("§cМаксимальное приближение");
            }
        } else if (clickedItem.getType() == Material.ENDER_PEARL) {
            if (currentZoom > MIN_ZOOM_LEVEL) {
                currentZoom--;
                mapZoomLevels.put(player.getUniqueId(), currentZoom);
                player.sendMessage("§aОтдаление карты (x" + currentZoom + ")");
                
                tabletGUI.openMapDetails(player, tabletType);
            } else {
                player.sendMessage("§cМаксимальное отдаление");
            }
        }
    }
    
    /**
     * Обрабатывает клик в меню заметок
     */
    private void handleNotesMenuClick(Player player, ItemStack clickedItem, String tabletType) {
        if (clickedItem.getType() == Material.ARROW) {
            player.sendMessage("§aВозвращаемся в главное меню из заметок...");
            tabletGUI.openMainMenu(player, tabletType);
        } else if (clickedItem.getType() == Material.WRITABLE_BOOK) {
            player.sendMessage("§aСоздание новой заметки...");
        }
    }

    /**
     * Возвращает текущий уровень масштабирования карты для указанного игрока
     * 
     * @param playerUUID UUID игрока
     * @return уровень масштабирования (1-3), по умолчанию 1
     */
    public int getMapZoomLevel(UUID playerUUID) {
        return mapZoomLevels.getOrDefault(playerUUID, 1);
    }
} 