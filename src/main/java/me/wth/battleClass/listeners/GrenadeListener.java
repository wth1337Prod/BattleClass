package me.wth.battleClass.listeners;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.grenades.Grenade;
import me.wth.battleClass.grenades.GrenadeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GrenadeListener implements Listener {
    private final BattleClass plugin;
    private final GrenadeManager grenadeManager;
    
    public GrenadeListener(BattleClass plugin, GrenadeManager grenadeManager) {
        this.plugin = plugin;
        this.grenadeManager = grenadeManager;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (grenadeManager.isGrenade(item)) {
                event.setCancelled(true); 
                
                Grenade grenade = grenadeManager.getGrenadeFromItem(item);
                
                if (grenade != null) {
                    grenadeManager.throwGrenade(player, grenade);
                }
            }
        }
    }
} 