package me.wth.battleClass.drones;

import me.wth.battleClass.BattleClass;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Обработчик событий для системы дронов
 */
public class DroneListener implements Listener {
    private final BattleClass plugin;
    private final DroneManager droneManager;
    
    private final Map<UUID, Boolean> droneControllers = new HashMap<>();
    
    private final Map<UUID, Boolean> rechargingPlayers = new HashMap<>();
    
    private final Map<UUID, Map<String, Boolean>> keyPresses = new HashMap<>();
    
    /**
     * Конструктор слушателя дронов
     * 
     * @param plugin экземпляр основного плагина
     * @param droneManager менеджер дронов
     */
    public DroneListener(BattleClass plugin, DroneManager droneManager) {
        this.plugin = plugin;
        this.droneManager = droneManager;
    }
    
    /**
     * Обрабатывает нажатия правой/левой кнопкой мыши для взаимодействия с дронами
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        Drone drone = droneManager.getDroneFromItemStack(itemInHand);
        
        if (drone != null) {
            event.setCancelled(true);
            
            if (!droneManager.canPlayerUseDrone(player, drone)) {
                player.sendMessage(ChatColor.RED + "Вы не можете использовать дрон другой фракции!");
                return;
            }
            
            if (player.isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                attemptRecharge(player, drone);
                return;
            }
            
            if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                if (drone.launch(player)) {
                    droneControllers.put(playerUUID, true);
                }
            }
        }
        
        if (isBattery(itemInHand) && player.isSneaking() && 
            (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            Drone offHandDrone = droneManager.getDroneFromItemStack(offHandItem);
            
            if (offHandDrone != null) {
                event.setCancelled(true);
                rechargeDrone(player, offHandDrone, itemInHand);
            }
        }
        
        if (droneControllers.getOrDefault(playerUUID, false)) {
            Drone activeDrone = getActiveDroneForPlayer(player);
            
            if (activeDrone != null) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                    Block targetBlock = player.getTargetBlockExact(activeDrone.getOperationalRange());
                    if (targetBlock != null) {
                        Location targetLocation = targetBlock.getLocation().add(0.5, 1.0, 0.5);
                        
                        if (activeDrone instanceof AbstractDrone) {
                            ((AbstractDrone) activeDrone).setDroneTarget(playerUUID, targetLocation);
                            player.sendMessage(ChatColor.GREEN + "Дрон перемещается к указанной точке.");
                        }
                    }
                }
                else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (activeDrone instanceof LancetKamikaze) {
                        activeDrone.detonate(player);
                        droneControllers.remove(playerUUID);
                    }
                    else if (activeDrone instanceof PredatorBattleDrone) {
                        Block targetBlock = player.getTargetBlockExact(activeDrone.getOperationalRange());
                        if (targetBlock != null) {
                            Location targetLocation = targetBlock.getLocation().add(0.5, 1.0, 0.5);
                            ((PredatorBattleDrone) activeDrone).fireMissile(player, targetLocation);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Обрабатывает смену слота инвентаря для выхода из режима управления дроном
     */
    @EventHandler
    public void onPlayerChangeItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        if (droneControllers.getOrDefault(playerUUID, false)) {
            droneControllers.remove(playerUUID);
            player.sendMessage(ChatColor.YELLOW + "Режим управления дроном завершен.");
        }
    }
    
    /**
     * Обрабатывает переключение режима приседания для отзыва дрона
     */
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        if (droneControllers.getOrDefault(playerUUID, false) && event.isSneaking()) {
            Drone activeDrone = getActiveDroneForPlayer(player);
            
            if (activeDrone != null) {
                activeDrone.recall(player);
                droneControllers.remove(playerUUID);
                player.sendMessage(ChatColor.YELLOW + "Дрон отозван.");
            }
        } else if (!event.isSneaking()) {
            rechargingPlayers.remove(playerUUID);
        }
    }
    
    /**
     * Обрабатывает выход игрока с сервера для автоматического отзыва дрона
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        Drone activeDrone = getActiveDroneForPlayer(player);
        if (activeDrone != null) {
            activeDrone.recall(player);
            droneControllers.remove(playerUUID);
        }
    }
    
    /**
     * Обрабатывает движение игрока для управления дроном в режиме от первого лица
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        Drone activeDrone = getActiveDroneForPlayer(player);
        
        if (activeDrone instanceof AbstractDrone && ((AbstractDrone) activeDrone).isInFirstPersonMode(playerUUID)) {
            Vector direction = player.getLocation().getDirection().normalize();
            
            Map<String, Boolean> playerKeys = keyPresses.computeIfAbsent(playerUUID, k -> new HashMap<>());
            
            if (playerKeys.getOrDefault("W", false)) {
                activeDrone.moveDrone(player, direction);
            }
            
            if (playerKeys.getOrDefault("S", false)) {
                activeDrone.moveDrone(player, direction.multiply(-1));
            }
            
            if (playerKeys.getOrDefault("A", false)) {
                Vector sideDirection = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
                activeDrone.moveDrone(player, sideDirection);
            }
            
            if (playerKeys.getOrDefault("D", false)) {
                Vector sideDirection = new Vector(direction.getZ(), 0, -direction.getX()).normalize();
                activeDrone.moveDrone(player, sideDirection);
            }
            
            if (playerKeys.getOrDefault("SPACE", false)) {
                activeDrone.moveDrone(player, new Vector(0, 0.5, 0));
            }
            
            if (playerKeys.getOrDefault("SHIFT", false)) {
                activeDrone.moveDrone(player, new Vector(0, -0.5, 0));
            }
        }
    }
    
    /**
     * Обрабатывает нажатие клавиши F для переключения режима от первого лица
     */
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        if (droneControllers.getOrDefault(playerUUID, false)) {
            event.setCancelled(true);
            
            Drone activeDrone = getActiveDroneForPlayer(player);
            
            if (activeDrone != null) {
                activeDrone.toggleFirstPersonView(player);
            }
        }
    }
    
    /**
     * Симулирует нажатие клавиши для управления дроном
     * 
     * @param player игрок
     * @param key название клавиши (W, A, S, D, SPACE, SHIFT)
     * @param pressed true, если клавиша нажата, false если отпущена
     */
    public void simulateKeyPress(Player player, String key, boolean pressed) {
        UUID playerUUID = player.getUniqueId();
        
        Map<String, Boolean> playerKeys = keyPresses.computeIfAbsent(playerUUID, k -> new HashMap<>());
        
        playerKeys.put(key, pressed);
    }
    
    /**
     * Получает активный дрон игрока
     * 
     * @param player игрок
     * @return активный дрон или null
     */
    private Drone getActiveDroneForPlayer(Player player) {
        for (Drone drone : droneManager.getAllDrones().values()) {
            if (drone instanceof AbstractDrone && ((AbstractDrone) drone).hasActiveDrone(player.getUniqueId())) {
                return drone;
            }
        }
        return null;
    }
    
    /**
     * Проверяет, является ли предмет батареей для дрона
     * 
     * @param item предмет для проверки
     * @return true, если предмет является батареей
     */
    private boolean isBattery(ItemStack item) {
        return droneManager.getChargeFromBattery(item) > 0;
    }
    
    /**
     * Попытка найти батарею в инвентаре игрока для перезарядки дрона
     * 
     * @param player игрок
     * @param drone дрон для зарядки
     */
    private void attemptRecharge(Player player, Drone drone) {
        UUID playerUUID = player.getUniqueId();
        
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isBattery(item)) {
                rechargeDrone(player, drone, item);
                return;
            }
        }
        
        player.sendMessage(ChatColor.RED + "У вас нет батареи для зарядки дрона.");
        rechargingPlayers.put(playerUUID, false);
    }
    
    /**
     * Перезарядка дрона батареей
     * 
     * @param player игрок
     * @param drone дрон для зарядки
     * @param battery батарея
     */
    private void rechargeDrone(Player player, Drone drone, ItemStack battery) {
        int charge = droneManager.getChargeFromBattery(battery);
        
        if (charge <= 0) {
            player.sendMessage(ChatColor.RED + "Батарея разряжена.");
            return;
        }
        
        if (drone.recharge(player, charge)) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                if (battery.getAmount() > 1) {
                    battery.setAmount(battery.getAmount() - 1);
                } else {
                    if (player.getInventory().getItemInMainHand().equals(battery)) {
                        player.getInventory().setItemInMainHand(null);
                    } else if (player.getInventory().getItemInOffHand().equals(battery)) {
                        player.getInventory().setItemInOffHand(null);
                    } else {
                        player.getInventory().removeItem(battery);
                    }
                }
            }
            
            player.sendMessage(ChatColor.GREEN + "Дрон успешно заряжен.");
        }
    }
} 