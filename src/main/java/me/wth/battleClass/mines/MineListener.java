package me.wth.battleClass.mines;

import me.wth.battleClass.BattleClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Обработчик событий, связанных с минами
 */
public class MineListener implements Listener {
    private final BattleClass plugin;
    private final MineManager mineManager;
    
    private final Map<UUID, Long> placingMines = new HashMap<>();
    
    private final Map<UUID, DefusingInfo> defusingMines = new HashMap<>();
    
    /**
     * Конструктор слушателя мин
     * @param plugin экземпляр основного плагина
     * @param mineManager менеджер мин
     */
    public MineListener(BattleClass plugin, MineManager mineManager) {
        this.plugin = plugin;
        this.mineManager = mineManager;
    }
    
    /**
     * Обработчик события установки мины (правый клик с миной в руке)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            if (mineManager.isMine(item)) {
                event.setCancelled(true); 
                
                if (placingMines.containsKey(player.getUniqueId())) {
                    long lastPlacingTime = placingMines.get(player.getUniqueId());
                    if (System.currentTimeMillis() - lastPlacingTime < 3000) { 
                        player.sendMessage("§cВы уже устанавливаете мину!");
                        return;
                    }
                }
                
                Mine mine = mineManager.getMineFromItem(item);
                
                if (mine != null) {
                    placeMine(player, mine, mineManager.getMineInstanceId(item));
                }
            }
        }
        
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Block clickedBlock = event.getClickedBlock();
            
            if (clickedBlock.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE || 
                clickedBlock.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                
                Location clickedLoc = clickedBlock.getLocation();
                
                if (mineManager.hasMinesAt(clickedLoc)) {
                    List<PlacedMine> minesAtLocation = mineManager.getMinesAt(clickedLoc);
                    
                    for (PlacedMine placedMine : minesAtLocation) {
                        if (!placedMine.isExploded()) {
                            if (player.isSneaking()) {
                                event.setCancelled(true); 
                                startDefusingMine(player, placedMine);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Обработчик события редстоун-сигнала (для обработки нажимных пластин)
     */
    @EventHandler
    public void onRedstoneEvent(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        
        if (block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE || 
            block.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                
            Location blockLocation = block.getLocation();
            
            if (mineManager.hasMinesAt(blockLocation)) {
                List<PlacedMine> minesAtLocation = mineManager.getMinesAt(blockLocation);
                
                for (PlacedMine placedMine : minesAtLocation) {
                    if (placedMine.isActivated() && !placedMine.isExploded()) {
                        Player triggeringPlayer = null;
                        double closestDistance = 2.0; 
                        
                        for (Player player : block.getWorld().getPlayers()) {
                            double distance = player.getLocation().distance(blockLocation.clone().add(0.5, 0.5, 0.5));
                            if (distance < closestDistance) {
                                closestDistance = distance;
                                triggeringPlayer = player;
                            }
                        }
                        
                        if (triggeringPlayer != null) {
                            triggerMine(placedMine, triggeringPlayer);
                            return;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Обработчик события движения игрока (для проверки наступания на мину)
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
            event.getFrom().getBlockY() == event.getTo().getBlockY() && 
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        Player player = event.getPlayer();
        Location playerLoc = player.getLocation();
        
        Block blockUnderPlayer = playerLoc.getBlock().getRelative(0, -1, 0);
        
        if (blockUnderPlayer.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE || 
            blockUnderPlayer.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            
            Location blockLocation = blockUnderPlayer.getLocation();
            
            if (mineManager.hasMinesAt(blockLocation)) {
                List<PlacedMine> minesAtLocation = mineManager.getMinesAt(blockLocation);
                
                for (PlacedMine placedMine : minesAtLocation) {
                    if (placedMine.isActivated() && !placedMine.isExploded() && placedMine.isPlayerOnMine(player)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (placedMine.isValid() && !placedMine.isExploded()) {
                                    triggerMine(placedMine, player);
                                }
                            }
                        }.runTaskLater(plugin, 2L); 
                        return;
                    }
                }
            }
        }
        
        if (defusingMines.containsKey(player.getUniqueId())) {
            DefusingInfo info = defusingMines.get(player.getUniqueId());
            PlacedMine mine = info.getPlacedMine();
            
            if (!mine.isPlayerOnMine(player)) {
                cancelDefusing(player);
                player.sendMessage("§cВы отошли от мины! Обезвреживание отменено.");
            }
        }
    }
    
    /**
     * Обработчик события подбора предмета (предотвращаем подбор установленных мин)
     */
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        Item item = event.getItem();
        
        if (item.hasMetadata("mine_instance")) {
            event.setCancelled(true);
        }
    }
    
    /**
     * Обработчик события манипуляции со стендом (предотвращаем взаимодействие с мин-стендами)
     */
    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        ArmorStand stand = event.getRightClicked();
        
        if (stand.hasMetadata("mine_stand")) {
            event.setCancelled(true);
        }
    }
    
    /**
     * Устанавливает мину на место, где стоит игрок
     * @param player игрок, устанавливающий мину
     * @param mine объект мины
     * @param instanceId уникальный идентификатор экземпляра мины
     */
    private void placeMine(Player player, Mine mine, String instanceId) {
        placingMines.put(player.getUniqueId(), System.currentTimeMillis());
        
        player.sendMessage("§aУстановка мины... Не двигайтесь!");
        
        new BukkitRunnable() {
            int timeLeft = 3; 
            
            @Override
            public void run() {
                if (!player.isOnline() || !placingMines.containsKey(player.getUniqueId())) {
                    cancel();
                    return;
                }
                
                if (player.getVelocity().lengthSquared() > 0.01) {
                    player.sendMessage("§cВы двигались! Установка мины отменена.");
                    placingMines.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                
                if (timeLeft > 0) {
                    player.sendMessage("§aУстановка мины... §f" + timeLeft + " §aс.");
                    timeLeft--;
                } else {
                    finishPlacingMine(player, mine, instanceId);
                    placingMines.remove(player.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
    
    /**
     * Завершает установку мины
     * @param player игрок, устанавливающий мину
     * @param mine объект мины
     * @param instanceId уникальный идентификатор экземпляра мины
     */
    private void finishPlacingMine(Player player, Mine mine, String instanceId) {
        Location location = player.getLocation().clone();
        location.setY(location.getY() - 1);
        
        World world = location.getWorld();
        if (world == null) return;
        
        Material originalMaterial = location.getBlock().getType();
        
        Material mineMaterial = mine.isAntiPersonnel() 
            ? Material.LIGHT_WEIGHTED_PRESSURE_PLATE 
            : Material.HEAVY_WEIGHTED_PRESSURE_PLATE; 
            
        location.getBlock().setType(mineMaterial);
        
        PlacedMine placedMine = new PlacedMine(
                instanceId,
                player.getUniqueId().toString(),
                mine.getId(),
                location,
                mine.getLifeTime(),
                null, 
                null  
        );
        
        placedMine.setOriginalMaterial(originalMaterial);
        
        mineManager.registerPlacedMine(instanceId, placedMine);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (placedMine.isValid() && !placedMine.isActivated() && !placedMine.isExploded()) {
                    placedMine.activate();
                    Player owner = plugin.getServer().getPlayer(UUID.fromString(placedMine.getOwnerId()));
                    if (owner != null && owner.isOnline()) {
                        owner.sendMessage("§aВаша мина активирована!");
                    }
                }
            }
        }.runTaskLater(plugin, 100L); 
        
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getAmount() > 1) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
        
        player.sendMessage("§aМина успешно установлена! Она будет активирована через 5 секунд.");
    }
    
    /**
     * Срабатывает, когда игрок наступает на мину
     * @param placedMine объект установленной мины
     * @param triggeringPlayer игрок, наступивший на мину
     */
    private void triggerMine(PlacedMine placedMine, Player triggeringPlayer) {
        if (placedMine.isExploded()) {
            return; 
        }
        
        Mine mineType = mineManager.getMine(placedMine.getMineType());
        
        if (mineType == null) {
            mineManager.removePlacedMine(placedMine.getInstanceId());
            return;
        }
        
        
        triggeringPlayer.sendMessage("§c§lВы наступили на мину!");
        
        int delay = mineType.getTriggerDelay();
        
        triggeringPlayer.getWorld().playSound(
                placedMine.getLocation(),
                org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING,
                1.0f,
                2.0f
        );
        
        triggeringPlayer.getWorld().spawnParticle(
                org.bukkit.Particle.SMOKE,
                placedMine.getLocation().clone().add(0, 0.2, 0),
                10,
                0.2, 0.2, 0.2,
                0.02
        );
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (placedMine.isValid() && !placedMine.isExploded()) {
                    placedMine.explode(triggeringPlayer, mineType.getDamage(), mineType.getBlastRadius());
                    mineManager.removePlacedMine(placedMine.getInstanceId());
                }
            }
        }.runTaskLater(plugin, delay);
    }
    
    /**
     * Начинает процесс обезвреживания мины
     * @param player игрок, обезвреживающий мину
     * @param placedMine объект установленной мины
     */
    private void startDefusingMine(Player player, PlacedMine placedMine) {
        if (defusingMines.containsKey(player.getUniqueId())) {
            player.sendMessage("§cВы уже обезвреживаете мину!");
            return;
        }
        
        player.sendMessage("§eНачинаем обезвреживание мины... Не двигайтесь!");
        
        Mine mineType = mineManager.getMine(placedMine.getMineType());
        
        if (mineType == null) {
            player.sendMessage("§cОшибка! Неизвестный тип мины.");
            return;
        }
        
        int defuseTime = 5; 
        
        if (!mineType.isDetectable()) {
            defuseTime += 3; 
        }
        
        DefusingInfo defusingInfo = new DefusingInfo(placedMine, defuseTime);
        defusingMines.put(player.getUniqueId(), defusingInfo);
        
        BukkitRunnable defuseTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!defusingMines.containsKey(player.getUniqueId()) || !player.isOnline()) {
                    cancel();
                    return;
                }
                
                DefusingInfo info = defusingMines.get(player.getUniqueId());
                int timeLeft = info.decrementTimeLeft();
                
                if (player.getVelocity().lengthSquared() > 0.01) {
                    cancelDefusing(player);
                    player.sendMessage("§cВы двигались! Обезвреживание отменено.");
                    cancel();
                    return;
                }
                
                player.sendMessage("§eОбезвреживание... §f" + timeLeft + " §eсек.");
                
                player.getWorld().playSound(
                        player.getLocation(),
                        org.bukkit.Sound.BLOCK_STONE_BUTTON_CLICK_ON,
                        0.5f,
                        1.0f
                );
                
                player.getWorld().spawnParticle(
                        org.bukkit.Particle.CRIT,
                        placedMine.getLocation().clone().add(0, 0.3, 0),
                        5,
                        0.1, 0.1, 0.1,
                        0.01
                );
                
                if (timeLeft <= 0) {
                    finishDefusing(player, placedMine);
                    cancel();
                }
            }
        };
        
        defusingInfo.setTaskId(defuseTask.runTaskTimer(plugin, 20L, 20L).getTaskId());
    }
    
    /**
     * Завершает процесс обезвреживания мины
     * @param player игрок, обезвредивший мину
     * @param placedMine объект установленной мины
     */
    private void finishDefusing(Player player, PlacedMine placedMine) {
        defusingMines.remove(player.getUniqueId());
        
        if (!placedMine.isValid() || placedMine.isExploded()) {
            player.sendMessage("§cМина уже не существует или взорвалась!");
            return;
        }
        
        Mine mineType = mineManager.getMine(placedMine.getMineType());
        
        if (mineType == null) {
            player.sendMessage("§cОшибка! Неизвестный тип мины.");
            return;
        }
        
        if (ThreadLocalRandom.current().nextDouble() < 0.05) {
            player.sendMessage("§c§lЧто-то пошло не так! Мина сейчас взорвется!");
            
            player.getWorld().playSound(
                    placedMine.getLocation(),
                    org.bukkit.Sound.ENTITY_CREEPER_PRIMED,
                    1.0f,
                    1.0f
            );
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (placedMine.isValid() && !placedMine.isExploded()) {
                        placedMine.explode(player, mineType.getDamage(), mineType.getBlastRadius());
                        mineManager.removePlacedMine(placedMine.getInstanceId());
                    }
                }
            }.runTaskLater(plugin, 10L); 
            
            return;
        }
        
        player.sendMessage("§a§lМина успешно обезврежена!");
        
        player.getWorld().playSound(
                player.getLocation(),
                org.bukkit.Sound.BLOCK_NOTE_BLOCK_CHIME,
                1.0f,
                1.0f
        );
        
        placedMine.getMineBlock().setType(placedMine.getOriginalMaterial());
        
        mineManager.removePlacedMine(placedMine.getInstanceId());
        
        player.giveExp(10);
        
        if (ThreadLocalRandom.current().nextDouble() < 0.3) {
            ItemStack mineItem = mineType.createItemStack(plugin);
            player.getInventory().addItem(mineItem);
            player.sendMessage("§aВам удалось сохранить мину!");
        }
    }
    
    /**
     * Отменяет процесс обезвреживания мины
     * @param player игрок, обезвреживающий мину
     */
    private void cancelDefusing(Player player) {
        DefusingInfo info = defusingMines.remove(player.getUniqueId());
        
        if (info != null) {
            int taskId = info.getTaskId();
            if (taskId > 0) {
                plugin.getServer().getScheduler().cancelTask(taskId);
            }
        }
    }
    
    /**
     * Внутренний класс для хранения информации об обезвреживании мины
     */
    private static class DefusingInfo {
        private final PlacedMine placedMine;
        private int timeLeft;
        private int taskId;
        
        public DefusingInfo(PlacedMine placedMine, int timeLeft) {
            this.placedMine = placedMine;
            this.timeLeft = timeLeft;
            this.taskId = -1;
        }
        
        public PlacedMine getPlacedMine() {
            return placedMine;
        }
        
        public int getTimeLeft() {
            return timeLeft;
        }
        
        public int decrementTimeLeft() {
            return --timeLeft;
        }
        
        public int getTaskId() {
            return taskId;
        }
        
        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }
    }
} 