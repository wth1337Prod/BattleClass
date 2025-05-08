package me.wth.battleClass.donations;

import me.wth.battleClass.BattleClass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Класс для обработки событий инвентаря, связанных с GUI донатов
 */
public class DonateListener implements Listener {
    private final BattleClass plugin;
    private final DonateGUI donateGUI;
    
    /**
     * Конструктор слушателя событий доната
     * 
     * @param plugin экземпляр основного класса плагина
     * @param donateGUI GUI для работы с донатами
     */
    public DonateListener(BattleClass plugin, DonateGUI donateGUI) {
        this.plugin = plugin;
        this.donateGUI = donateGUI;
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Обрабатывает событие клика по инвентарю
     * 
     * @param event событие клика
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        InventoryHolder holder = event.getInventory().getHolder();
        
        if (holder instanceof DonateGUI) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) {
                return;
            }
            
            boolean isRightClick = event.isRightClick();
            donateGUI.handleInventoryClick(player, event.getCurrentItem(), isRightClick);
        }
    }
    
    /**
     * Обрабатывает событие закрытия инвентаря
     * 
     * @param event событие закрытия
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();
        
        if (holder instanceof DonateGUI) {
            donateGUI.handleInventoryClose(player);
        }
    }
} 