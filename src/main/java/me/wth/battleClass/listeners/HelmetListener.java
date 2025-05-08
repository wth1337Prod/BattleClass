package me.wth.battleClass.listeners;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.armor.Helmet;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Обработчик событий для шлемов
 */
public class HelmetListener implements Listener {
    private final BattleClass plugin;
    
    /**
     * Конструктор слушателя шлемов
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public HelmetListener(BattleClass plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Обработчик нажатия правой кнопки мыши с шлемом
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || 
            (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) ||
            event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return;
        }
        
        List<String> lore = meta.getLore();
        if (lore == null) {
            return;
        }
        
        boolean isHelmet = lore.stream().anyMatch(line -> line.contains("§7Тип: §fВоенный шлем"));
        if (!isHelmet) {
            return;
        }
        
        String helmetId = null;
        for (String line : lore) {
            if (line.startsWith("§8Идентификатор: §7")) {
                helmetId = line.substring("§8Идентификатор: §7".length());
                break;
            }
        }
        
        if (helmetId == null) {
            return;
        }
        
        Helmet helmet = plugin.getHelmetManager().getHelmet(helmetId);
        if (helmet == null) {
            return;
        }
        
        ItemStack currentHelmet = player.getInventory().getHelmet();
        if (currentHelmet != null && currentHelmet.getType() != Material.AIR) {
            player.sendMessage("§cСначала снимите текущий шлем!");
            event.setCancelled(true);
            return;
        }
        
        player.getInventory().setHelmet(item.clone());
        item.setAmount(item.getAmount() - 1);
        
        player.sendMessage("§aВы надели " + helmet.getDisplayName());
        player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_IRON, 1.0f, 1.0f);
        
        event.setCancelled(true);
    }
} 