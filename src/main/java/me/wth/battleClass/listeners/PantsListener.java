package me.wth.battleClass.listeners;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.armor.Pants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

/**
 * Обработчик событий для камуфляжных штанов
 */
public class PantsListener implements Listener {
    private final BattleClass plugin;
    private final Random random = new Random();
    
    /**
     * Конструктор слушателя штанов
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public PantsListener(BattleClass plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Обработчик нажатия правой кнопки мыши с штанами
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
        
        boolean isPants = lore.stream().anyMatch(line -> line.contains("§7Тип: §fКамуфляжные штаны"));
        if (!isPants) {
            return;
        }
        
        String pantsId = null;
        for (String line : lore) {
            if (line.startsWith("§8Идентификатор: §7")) {
                pantsId = line.substring("§8Идентификатор: §7".length());
                break;
            }
        }
        
        if (pantsId == null) {
            return;
        }
        
        Pants pants = plugin.getPantsManager().getPants(pantsId);
        if (pants == null) {
            return;
        }
        
        ItemStack currentLeggings = player.getInventory().getLeggings();
        if (currentLeggings != null && currentLeggings.getType() != Material.AIR) {
            player.sendMessage("§cСначала снимите текущие штаны!");
            event.setCancelled(true);
            return;
        }
        
        player.getInventory().setLeggings(item.clone());
        item.setAmount(item.getAmount() - 1);
        
        player.sendMessage("§aВы надели " + pants.getDisplayName());
        player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.0f, 1.0f);
        
        event.setCancelled(true);
    }
    
    /**
     * Обработчик нацеливания мобов на игрока
     * Камуфляжные штаны снижают шанс обнаружения игрока
     */
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            
            if (plugin.getPantsManager().hasPants(player)) {
                Pants pants = plugin.getPantsManager().getPlayerPants(player);
                if (pants != null) {
                    double camoLevel = pants.getCamouflageLevel();
                    
                    if (random.nextDouble() < camoLevel) {
                        event.setCancelled(true); 
                    }
                }
            }
        }
    }
} 