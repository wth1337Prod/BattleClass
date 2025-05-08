package me.wth.battleClass.flamethrowers;

import me.wth.battleClass.BattleClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Обработчик событий для системы огнеметов
 */
public class FlamethrowerListener implements Listener {
    private final BattleClass plugin;
    private final FlamethrowerManager flamethrowerManager;
    
    private final Map<UUID, BukkitTask> activeFlamethrowers = new HashMap<>();
    
    private final Map<UUID, Boolean> refuelingPlayers = new HashMap<>();
    
    /**
     * Конструктор слушателя огнеметов
     * 
     * @param plugin экземпляр основного плагина
     * @param flamethrowerManager менеджер огнеметов
     */
    public FlamethrowerListener(BattleClass plugin, FlamethrowerManager flamethrowerManager) {
        this.plugin = plugin;
        this.flamethrowerManager = flamethrowerManager;
    }
    
    /**
     * Обрабатывает нажатия правой кнопкой мыши для использования огнеметов
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        Flamethrower flamethrower = flamethrowerManager.getFlamethrowerFromItemStack(itemInHand);
        
        if (flamethrower != null) {
            event.setCancelled(true);
            
            if (!flamethrowerManager.canPlayerUseFlamethrower(player, flamethrower)) {
                player.sendMessage(ChatColor.RED + "Вы не можете использовать огнемет другой фракции!");
                return;
            }
            
            if (player.isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                attemptRefuel(player, flamethrower);
                return;
            }
            
            if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && 
                !isPlayerFiring(player)) {
                startFiring(player, flamethrower);
            }
        }
        
        if (isCanister(itemInHand) && player.isSneaking() && 
            (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            Flamethrower offHandFlamethrower = flamethrowerManager.getFlamethrowerFromItemStack(offHandItem);
            
            if (offHandFlamethrower != null) {
                event.setCancelled(true);
                refuelFlamethrower(player, offHandFlamethrower, itemInHand);
            }
        }
    }
    
    /**
     * Обрабатывает смену слота инвентаря для остановки стрельбы
     */
    @EventHandler
    public void onPlayerChangeItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        if (isPlayerFiring(player)) {
            stopFiring(player);
        }
    }
    
    /**
     * Обрабатывает переключение режима приседания для остановки/начала стрельбы
     */
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (isPlayerFiring(player) && event.isSneaking()) {
            stopFiring(player);
            refuelingPlayers.put(player.getUniqueId(), true);
            player.sendMessage(ChatColor.YELLOW + "Режим перезаправки огнемета. Используйте ПКМ с канистрой.");
        } else if (!event.isSneaking()) {
            refuelingPlayers.remove(player.getUniqueId());
        }
    }
    
    /**
     * Проверяет, является ли предмет канистрой с топливом
     * 
     * @param item предмет для проверки
     * @return true, если предмет является канистрой
     */
    private boolean isCanister(ItemStack item) {
        return flamethrowerManager.getFuelAmountFromCanister(item) > 0;
    }
    
    /**
     * Запускает процесс стрельбы из огнемета
     * 
     * @param player игрок, стреляющий из огнемета
     * @param flamethrower огнемет для стрельбы
     */
    private void startFiring(Player player, Flamethrower flamethrower) {
        UUID playerUUID = player.getUniqueId();
        
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack currentItem = player.getInventory().getItemInMainHand();
                Flamethrower currentFlamethrower = flamethrowerManager.getFlamethrowerFromItemStack(currentItem);
                
                if (currentFlamethrower == null || currentFlamethrower.getId() != flamethrower.getId()) {
                    stopFiring(player);
                    return;
                }
                
                if (!flamethrower.fire(player)) {
                    stopFiring(player);
                    return;
                }
                
                if (flamethrower instanceof AbstractFlamethrower) {
                    AbstractFlamethrower abstractFlame = (AbstractFlamethrower) flamethrower;
                    int currentFuel = abstractFlame.getCurrentFuel(playerUUID);
                    
                    float fuelPercent = (float) currentFuel / abstractFlame.getFuelCapacity();
                    player.setExp(fuelPercent);
                    player.setLevel(currentFuel);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L); 
        
        activeFlamethrowers.put(playerUUID, task);
        
        if (flamethrower instanceof AbstractFlamethrower) {
            AbstractFlamethrower abstractFlame = (AbstractFlamethrower) flamethrower;
            int currentFuel = abstractFlame.getCurrentFuel(playerUUID);
            float fuelPercent = (float) currentFuel / abstractFlame.getFuelCapacity();
            player.setExp(fuelPercent);
            player.setLevel(currentFuel);
        }
    }
    
    /**
     * Останавливает процесс стрельбы из огнемета
     * 
     * @param player игрок, прекращающий стрельбу
     */
    private void stopFiring(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        BukkitTask task = activeFlamethrowers.remove(playerUUID);
        if (task != null) {
            task.cancel();
        }
        
        player.setExp(player.getExp());
        player.setLevel(player.getLevel());
    }
    
    /**
     * Проверяет, стреляет ли игрок в данный момент
     * 
     * @param player игрок для проверки
     * @return true, если игрок стреляет
     */
    public boolean isPlayerFiring(Player player) {
        return activeFlamethrowers.containsKey(player.getUniqueId());
    }
    
    /**
     * Пытается перезаправить огнемет игрока
     * 
     * @param player игрок, перезаправляющий огнемет
     * @param flamethrower огнемет для заправки
     */
    private void attemptRefuel(Player player, Flamethrower flamethrower) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            
            if (isCanister(item)) {
                refuelFlamethrower(player, flamethrower, item);
                return;
            }
        }
        
        player.sendMessage(ChatColor.RED + "У вас нет канистры с топливом для заправки!");
    }
    
    /**
     * Заправляет огнемет из канистры
     * 
     * @param player игрок, перезаправляющий огнемет
     * @param flamethrower огнемет для заправки
     * @param canister канистра с топливом
     */
    private void refuelFlamethrower(Player player, Flamethrower flamethrower, ItemStack canister) {
        int fuelAmount = flamethrowerManager.getFuelAmountFromCanister(canister);
        
        if (fuelAmount <= 0) {
            player.sendMessage(ChatColor.RED + "Канистра пуста!");
            return;
        }
        
        if (flamethrower.refuel(player, fuelAmount)) {
            if (canister.getAmount() > 1) {
                canister.setAmount(canister.getAmount() - 1);
            } else {
                if (player.getInventory().getItemInMainHand().equals(canister)) {
                    player.getInventory().setItemInMainHand(null);
                } else if (player.getInventory().getItemInOffHand().equals(canister)) {
                    player.getInventory().setItemInOffHand(null);
                } else {
                    player.getInventory().remove(canister);
                }
            }
            
            if (flamethrower instanceof AbstractFlamethrower) {
                AbstractFlamethrower abstractFlame = (AbstractFlamethrower) flamethrower;
                int currentFuel = abstractFlame.getCurrentFuel(player.getUniqueId());
                float fuelPercent = (float) currentFuel / abstractFlame.getFuelCapacity();
                player.setExp(fuelPercent);
                player.setLevel(currentFuel);
            }
        }
    }
} 