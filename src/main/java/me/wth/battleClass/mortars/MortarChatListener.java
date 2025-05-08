package me.wth.battleClass.mortars;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Обработчик чата для управления минометами
 */
public class MortarChatListener implements Listener {
    private final MortarListener mortarListener;
    
    /**
     * Конструктор слушателя чата для минометов
     * 
     * @param mortarListener основной слушатель минометов
     */
    public MortarChatListener(MortarListener mortarListener) {
        this.mortarListener = mortarListener;
    }
    
    /**
     * Обрабатывает сообщения игроков для настройки минометов
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (mortarListener.isConfiguringMortar(player)) {
            event.setCancelled(true);
            
            org.bukkit.Bukkit.getScheduler().runTask(
                mortarListener.getPlugin(), 
                () -> mortarListener.handleMortarConfiguration(player, event.getMessage())
            );
        }
    }
} 