package me.wth.battleClass.listeners;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.armor.Armor;
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
 * Обработчик событий для бронежилетов
 */
public class ArmorListener implements Listener {
    private final BattleClass plugin;
    
    /**
     * Конструктор слушателя бронежилетов
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public ArmorListener(BattleClass plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Обработчик нажатия правой кнопки мыши с бронежилетом
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
        
        boolean isArmor = lore.stream().anyMatch(line -> line.contains("§7Тип: §fБронежилет"));
        if (!isArmor) {
            return;
        }
        
        String armorId = null;
        for (String line : lore) {
            if (line.startsWith("§8Идентификатор: §7")) {
                armorId = line.substring("§8Идентификатор: §7".length());
                break;
            }
        }
        
        if (armorId == null) {
            return;
        }
        
        Armor armor = plugin.getArmorManager().getArmor(armorId);
        if (armor == null) {
            return;
        }
        
        ItemStack currentChestplate = player.getInventory().getChestplate();
        if (currentChestplate != null && currentChestplate.getType() != Material.AIR) {
            player.sendMessage("§cСначала снимите текущий нагрудник!");
            event.setCancelled(true);
            return;
        }
        
        player.getInventory().setChestplate(item.clone());
        item.setAmount(item.getAmount() - 1);
        
        player.sendMessage("§aВы надели " + armor.getDisplayName());
        player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_CHAIN, 1.0f, 1.0f);
        
        event.setCancelled(true);
    }
} 