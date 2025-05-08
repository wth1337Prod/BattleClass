package me.wth.battleClass.listeners;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.armor.Boots;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

/**
 * Обработчик событий для ботинок
 */
public class BootsListener implements Listener {
    private final BattleClass plugin;
    private final Map<UUID, Double> playerSpeedBonuses = new HashMap<>();
    
    /**
     * Конструктор слушателя ботинок
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public BootsListener(BattleClass plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Обработчик нажатия правой кнопки мыши с ботинками
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
        
        boolean isBoots = lore.stream().anyMatch(line -> line.contains("§7Тип: §fБоевые ботинки"));
        if (!isBoots) {
            return;
        }
        
        String bootsId = null;
        for (String line : lore) {
            if (line.startsWith("§8Идентификатор: §7")) {
                bootsId = line.substring("§8Идентификатор: §7".length());
                break;
            }
        }
        
        if (bootsId == null) {
            return;
        }
        
        Boots boots = plugin.getBootsManager().getBoots(bootsId);
        if (boots == null) {
            return;
        }
        
        ItemStack currentBoots = player.getInventory().getBoots();
        if (currentBoots != null && currentBoots.getType() != Material.AIR) {
            player.sendMessage("§cСначала снимите текущие ботинки!");
            event.setCancelled(true);
            return;
        }
        
        player.getInventory().setBoots(item.clone());
        item.setAmount(item.getAmount() - 1);
        
        playerSpeedBonuses.put(player.getUniqueId(), boots.getSpeedBonus());
        
        applySpeedBonus(player, boots.getSpeedBonus());
        
        player.sendMessage("§aВы надели " + boots.getDisplayName());
        player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_IRON, 1.0f, 1.0f);
        
        event.setCancelled(true);
    }
    
    /**
     * Применяет бонус скорости к игроку
     * 
     * @param player игрок для применения бонуса
     * @param speedBonus величина бонуса
     */
    private void applySpeedBonus(Player player, double speedBonus) {
        if (speedBonus <= 0) return;
        
        int amplifier = (int) Math.floor(speedBonus * 5);
        if (amplifier < 0) amplifier = 0;
        
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.SPEED,
            Integer.MAX_VALUE, 
            amplifier,
            false,  
            false,  
            true    
        ));
    }
    
    /**
     * Обработчик движения игрока (для проверки ботинок)
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        if (!playerSpeedBonuses.containsKey(playerId) && plugin.getBootsManager().hasBoots(player)) {
            Boots boots = plugin.getBootsManager().getPlayerBoots(player);
            if (boots != null) {
                double speedBonus = boots.getSpeedBonus();
                playerSpeedBonuses.put(playerId, speedBonus);
                applySpeedBonus(player, speedBonus);
            }
        }
        else if (playerSpeedBonuses.containsKey(playerId) && !plugin.getBootsManager().hasBoots(player)) {
            player.removePotionEffect(PotionEffectType.SPEED);
            playerSpeedBonuses.remove(playerId);
        }
    }
} 