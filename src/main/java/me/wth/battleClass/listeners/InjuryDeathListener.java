package me.wth.battleClass.listeners;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.medical.InjuryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Слушатель событий смерти и возрождения игроков для управления травмами
 */
public class InjuryDeathListener implements Listener {
    private final BattleClass plugin;
    private final InjuryManager injuryManager;
    
    /**
     * Конструктор слушателя событий смерти и травм
     * 
     * @param plugin экземпляр главного класса плагина
     * @param injuryManager менеджер травм
     */
    public InjuryDeathListener(BattleClass plugin, InjuryManager injuryManager) {
        this.plugin = plugin;
        this.injuryManager = injuryManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Обрабатывает событие смерти игрока
     * Сразу очищаем все травмы при смерти, чтобы предотвратить урон после смерти
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        injuryManager.clearAllInjuries(player);
        
        plugin.getLogger().info("Очищены все травмы у игрока " + player.getName() + " при смерти");
    }
    
    /**
     * Обрабатывает событие возрождения игрока
     * Дополнительно очищаем все травмы при возрождении
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                injuryManager.clearAllInjuries(player);
                
                plugin.getLogger().info("Очищены все травмы у игрока " + player.getName() + " при возрождении");
            }
        }.runTaskLater(plugin, 5L); 
    }
} 