package me.wth.battleClass.ranks;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.tablet.TabletManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Слушатель событий для системы рангов
 */
public class RankListener implements Listener {
    private final BattleClass plugin;
    private final RankManager rankManager;
    private final TabletManager tabletManager;
    
    /**
     * Конструктор для слушателя событий системы рангов
     * 
     * @param plugin экземпляр главного класса плагина
     * @param rankManager менеджер рангов
     * @param tabletManager менеджер планшетов
     */
    public RankListener(BattleClass plugin, RankManager rankManager, TabletManager tabletManager) {
        this.plugin = plugin;
        this.rankManager = rankManager;
        this.tabletManager = tabletManager;
    }
    
    /**
     * Обрабатывает событие входа игрока на сервер
     * Применяет отображение ранга в имени игрока
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        rankManager.updatePlayerDisplayName(player);
    }
    
    /**
     * Обрабатывает событие взаимодействия с планшетом
     * Предотвращает использование планшета игроками без командирского ранга
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item != null && tabletManager.isTablet(item)) {
            if (!rankManager.canUseTablet(player)) {
                event.setCancelled(true);
                
                player.sendMessage(ChatColor.RED + "У вас нет доступа к военному планшету! " + 
                        "Только командиры имеют право использовать этот предмет.");
                
                Rank rank = rankManager.getPlayerRank(player);
                if (rank != null) {
                    player.sendMessage(ChatColor.RED + "Ваш текущий ранг: " + rank.getColor() + 
                            rank.getDisplayName() + ChatColor.RED + " (недостаточно для использования планшета)");
                }
            }
        }
    }
    
    /**
     * Обрабатывает событие подбора предмета игроком
     * Запрещает подбирать планшет игрокам без командирского ранга
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        
        if (tabletManager.isTablet(itemStack) && !rankManager.canUseTablet(player)) {
            event.setCancelled(true);
            
            item.setVelocity(player.getLocation().getDirection().multiply(-0.5));
            
            player.sendMessage(ChatColor.RED + "Вы не можете подобрать военный планшет! " +
                    "Только командиры имеют право использовать этот предмет.");
        }
    }
    
    /**
     * Обрабатывает событие выбрасывания предмета игроком
     * Уведомляет игрока о выбрасывании планшета
     */
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();
        
        if (tabletManager.isTablet(itemStack)) {
            player.sendMessage(ChatColor.YELLOW + "Вы выбросили военный планшет.");
            
            if (rankManager.canUseTablet(player)) {
                player.sendMessage(ChatColor.GOLD + "Внимание: Только командиры могут использовать этот планшет.");
            }
        }
    }
    
    /**
     * Обрабатывает событие клика в инвентаре
     * Запрещает перемещение планшета игрокам без командирского ранга
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem != null && tabletManager.isTablet(clickedItem) && !rankManager.canUseTablet(player)) {
            event.setCancelled(true);
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getInventory().remove(clickedItem);
                    player.updateInventory();
                    
                    player.getWorld().dropItemNaturally(player.getLocation(), clickedItem);
                }
            }.runTaskLater(plugin, 1L);
            
            player.sendMessage(ChatColor.RED + "Вы не можете использовать военный планшет! " +
                    "Только командиры имеют право использовать этот предмет.");
        }
    }
    
    /**
     * Обрабатывает событие смерти игрока
     * Сохраняет планшет в инвентаре, если игрок командир
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        if (rankManager.canUseTablet(player)) {
            for (ItemStack item : event.getDrops()) {
                if (tabletManager.isTablet(item)) {
                    event.getDrops().remove(item);
                    
                    final ItemStack tabletCopy = item.clone();
                    
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (player.isOnline()) {
                                player.getInventory().addItem(tabletCopy);
                                player.sendMessage(ChatColor.GREEN + "Ваш военный планшет был сохранен.");
                            }
                        }
                    }.runTaskLater(plugin, 5L);
                    
                    break;
                }
            }
        }
    }
} 