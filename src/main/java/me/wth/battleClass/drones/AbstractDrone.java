package me.wth.battleClass.drones;

import me.wth.battleClass.BattleClass;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Базовый абстрактный класс для реализации дронов
 */
public abstract class AbstractDrone implements Drone {
    protected final BattleClass plugin;
    protected final String id;
    protected final String displayName;
    protected final List<String> description;
    protected final Material material;
    protected final int maxHeight;
    protected final int operationalRange;
    protected final double speed;
    protected final int batteryCapacity;
    protected final double batteryConsumption;
    protected final int rechargeTime;
    protected final double explosionDamage;
    protected final double explosionRadius;
    protected final String weaponType;
    
    private static final Map<UUID, ArmorStand> activeDrones = new HashMap<>();
    
    private static final Map<UUID, BukkitTask> droneTasks = new HashMap<>();
    
    private static final Map<UUID, Integer> playerBatteryLevels = new HashMap<>();
    
    private static final Map<UUID, Location> droneTargets = new HashMap<>();
    
    private static final Map<UUID, Boolean> firstPersonControllers = new HashMap<>();
    
    private static final Map<UUID, Location> previousPlayerLocations = new HashMap<>();
    
    private static final Map<UUID, GameMode> previousGameModes = new HashMap<>();
    
    /**
     * Конструктор для создания дрона
     *
     * @param plugin экземпляр плагина
     * @param id идентификатор дрона
     * @param displayName отображаемое название
     * @param description описание дрона
     * @param material материал для отображения в инвентаре
     * @param maxHeight максимальная высота полета
     * @param operationalRange дальность действия от оператора
     * @param speed скорость перемещения (блоков в секунду)
     * @param batteryCapacity емкость батареи
     * @param batteryConsumption расход энергии (единиц в минуту)
     * @param rechargeTime время перезарядки для камикадзе (секунды)
     * @param explosionDamage урон от взрыва
     * @param explosionRadius радиус взрыва
     * @param weaponType тип вооружения (для боевых дронов)
     */
    public AbstractDrone(BattleClass plugin, String id, String displayName, List<String> description,
                          Material material, int maxHeight, int operationalRange, double speed,
                          int batteryCapacity, double batteryConsumption, int rechargeTime,
                          double explosionDamage, double explosionRadius, String weaponType) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.maxHeight = maxHeight;
        this.operationalRange = operationalRange;
        this.speed = speed;
        this.batteryCapacity = batteryCapacity;
        this.batteryConsumption = batteryConsumption;
        this.rechargeTime = rechargeTime;
        this.explosionDamage = explosionDamage;
        this.explosionRadius = explosionRadius;
        this.weaponType = weaponType;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public List<String> getDescription() {
        return description;
    }
    
    @Override
    public int getMaxHeight() {
        return maxHeight;
    }
    
    @Override
    public int getOperationalRange() {
        return operationalRange;
    }
    
    @Override
    public double getSpeed() {
        return speed;
    }
    
    @Override
    public int getBatteryCapacity() {
        return batteryCapacity;
    }
    
    @Override
    public double getBatteryConsumption() {
        return batteryConsumption;
    }
    
    @Override
    public int getRechargeTime() {
        return rechargeTime;
    }
    
    @Override
    public double getExplosionDamage() {
        return explosionDamage;
    }
    
    @Override
    public double getExplosionRadius() {
        return explosionRadius;
    }
    
    @Override
    public String getWeaponType() {
        return weaponType;
    }
    
    @Override
    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b" + displayName);
            
            List<String> lore = new ArrayList<>();
            lore.addAll(description);
            lore.add("");
            lore.add("§7Макс. высота: §f" + maxHeight + " блоков");
            lore.add("§7Дальность: §f" + operationalRange + " блоков");
            lore.add("§7Скорость: §f" + speed + " блоков/сек");
            lore.add("§7Емкость батареи: §f" + batteryCapacity + " единиц");
            lore.add("§7Расход батареи: §f" + batteryConsumption + " ед./мин");
            
            if (explosionDamage > 0) {
                lore.add("§7Урон от взрыва: §f" + explosionDamage);
                lore.add("§7Радиус взрыва: §f" + explosionRadius + " блоков");
                lore.add("§7Время перезарядки: §f" + rechargeTime + " сек.");
            }
            
            if (weaponType != null && !weaponType.isEmpty()) {
                lore.add("§7Вооружение: §f" + weaponType);
            }
            
            lore.add("");
            lore.add("§eПКМ - запустить дрон");
            lore.add("§eШифт+ПКМ - перезарядить батарею");
            
            meta.setLore(lore);
            
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            
            NamespacedKey key = new NamespacedKey(plugin, "drone_id");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, id);
            
            itemStack.setItemMeta(meta);
        }
        
        return itemStack;
    }
    
    @Override
    public boolean launch(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        if (hasActiveDrone(playerUUID)) {
            player.sendMessage(ChatColor.RED + "У вас уже запущен дрон. Сначала отзовите его.");
            return false;
        }
        
        int currentBattery = playerBatteryLevels.getOrDefault(playerUUID, batteryCapacity);
        
        if (currentBattery <= 0) {
            player.sendMessage(ChatColor.RED + "Батарея дрона разряжена! Перезарядите ее.");
            return false;
        }
        
        Location spawnLocation = player.getLocation().add(0, 2, 0);
        ArmorStand drone = (ArmorStand) player.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
        
        configureDroneEntity(drone, player);
        
        activeDrones.put(playerUUID, drone);
        
        startDroneTask(player, drone);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BEE_LOOP, 1.0f, 1.5f);
        
        player.sendMessage(ChatColor.GREEN + "Дрон запущен!");
        
        player.sendMessage(ChatColor.YELLOW + "===== Управление дроном =====");
        player.sendMessage(ChatColor.YELLOW + "F - переключиться в режим управления от первого лица");
        player.sendMessage(ChatColor.YELLOW + "WASD - управление движением дрона");
        player.sendMessage(ChatColor.YELLOW + "Пробел/Шифт - подъем/спуск дрона");
        player.sendMessage(ChatColor.YELLOW + "ЛКМ - направить дрон в точку (в обычном режиме)");
        player.sendMessage(ChatColor.YELLOW + "ПКМ - подорвать дрон-камикадзе");
        player.sendMessage(ChatColor.YELLOW + "Шифт (удерживать) - отозвать дрон");
        player.sendMessage(ChatColor.YELLOW + "ESC - выйти из режима управления от первого лица");
        player.sendMessage(ChatColor.YELLOW + "===========================");
        
        return true;
    }
    
    /**
     * Настройка визуального отображения сущности дрона
     * 
     * @param drone сущность ArmorStand для настройки
     * @param owner владелец дрона
     */
    protected void configureDroneEntity(ArmorStand drone, Player owner) {
        drone.setVisible(false);
        drone.setSmall(true);
        drone.setInvulnerable(true);
        drone.setGravity(false);
        drone.setBasePlate(false);
        drone.setCustomName(ChatColor.AQUA + "Дрон " + owner.getName());
        drone.setCustomNameVisible(true);
        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        
        drone.getEquipment().setHelmet(head);
        
        drone.getPersistentDataContainer().set(
            new NamespacedKey(plugin, "drone_owner"),
            PersistentDataType.STRING,
            owner.getUniqueId().toString()
        );
        
        drone.getPersistentDataContainer().set(
            new NamespacedKey(plugin, "drone_type"),
            PersistentDataType.STRING,
            this.getId()
        );
    }
    
    /**
     * Запуск задачи для управления дроном
     * 
     * @param player владелец дрона
     * @param drone сущность дрона
     */
    protected void startDroneTask(Player player, ArmorStand drone) {
        UUID playerUUID = player.getUniqueId();
        
        BukkitTask task = new BukkitRunnable() {
            private int ticks = 0;
            
            @Override
            public void run() {
                if (!player.isOnline() || drone.isDead()) {
                    cancel();
                    removeDrone(playerUUID);
                    return;
                }
                
                updateDronePosition(player, drone);
                
                if (ticks % 20 == 0) {
                    double consumptionPerSecond = batteryConsumption / 60.0;
                    int currentBattery = playerBatteryLevels.getOrDefault(playerUUID, batteryCapacity);
                    
                    currentBattery -= Math.max(1, (int) Math.ceil(consumptionPerSecond));
                    
                    if (currentBattery <= 0) {
                        currentBattery = 0;
                        player.sendMessage(ChatColor.RED + "Батарея дрона разрядилась! Дрон возвращается.");
                        recall(player);
                    }
                    
                    playerBatteryLevels.put(playerUUID, currentBattery);
                    
                    float batteryPercent = (float) currentBattery / batteryCapacity;
                    player.setExp(batteryPercent);
                    player.setLevel(currentBattery);
                }
                
                createDroneEffects(drone);
                
                if (drone.getLocation().distance(player.getLocation()) > operationalRange) {
                    player.sendMessage(ChatColor.RED + "Дрон вне зоны действия! Автоматический возврат...");
                    droneTargets.put(playerUUID, player.getLocation().add(0, 2, 0));
                }
                
                if (drone.getLocation().getY() > player.getLocation().getY() + maxHeight) {
                    Location newTarget = drone.getLocation();
                    newTarget.setY(player.getLocation().getY() + maxHeight);
                    droneTargets.put(playerUUID, newTarget);
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L); 
        
        droneTasks.put(playerUUID, task);
    }
    
    /**
     * Создает визуальные эффекты для дрона
     * 
     * @param drone сущность дрона
     */
    protected void createDroneEffects(ArmorStand drone) {
        Location location = drone.getLocation();
        World world = location.getWorld();
        
        world.spawnParticle(Particle.DUST, location, 3, 0.2, 0.1, 0.2, 0.01,
                new org.bukkit.Particle.DustOptions(org.bukkit.Color.AQUA, 1.0f));
        
        if (Math.random() < 0.05) {
            world.playSound(location, Sound.BLOCK_NOTE_BLOCK_HAT, 0.2f, 1.5f);
        }
    }
    
    /**
     * Обновляет позицию дрона, двигая его к целевой точке если она задана
     * 
     * @param player владелец дрона
     * @param drone сущность дрона
     */
    protected void updateDronePosition(Player player, ArmorStand drone) {
        UUID playerUUID = player.getUniqueId();
        Location target = droneTargets.get(playerUUID);
        
        if (target != null) {
            Location current = drone.getLocation();
            
            if (current.distance(target) < 0.5) {
                droneTargets.remove(playerUUID);
                return;
            }
            
            Vector direction = target.toVector().subtract(current.toVector()).normalize();
            
            direction.multiply(Math.min(speed / 20.0, current.distance(target)));
            
            Location newLocation = current.add(direction);
            drone.teleport(newLocation);
            
            if (direction.lengthSquared() > 0) {
                Location lookLocation = newLocation.clone();
                lookLocation.setDirection(direction);
                drone.teleport(lookLocation);
            }
        }
    }
    
    /**
     * Переключает режим управления дроном от первого лица
     * 
     * @param player игрок-оператор дрона
     * @return true если режим успешно переключен
     */
    @Override
    public boolean toggleFirstPersonView(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        if (!hasActiveDrone(playerUUID)) {
            player.sendMessage(ChatColor.RED + "У вас нет активного дрона.");
            return false;
        }
        
        ArmorStand drone = activeDrones.get(playerUUID);
        
        if (isInFirstPersonMode(playerUUID)) {
            exitFirstPersonMode(player);
            player.sendMessage(ChatColor.YELLOW + "Вы вышли из режима управления от первого лица.");
            return true;
        } else {
            enterFirstPersonMode(player, drone);
            player.sendMessage(ChatColor.GREEN + "Вы вошли в режим управления от первого лица.");
            player.sendMessage(ChatColor.YELLOW + "Используйте WASD для управления, Пробел/Шифт для подъема/спуска.");
            player.sendMessage(ChatColor.YELLOW + "Нажмите ESC для выхода из режима управления от первого лица.");
            return true;
        }
    }
    
    /**
     * Проверяет, находится ли игрок в режиме управления от первого лица
     * 
     * @param playerUUID UUID игрока
     * @return true, если игрок в режиме от первого лица
     */
    public boolean isInFirstPersonMode(UUID playerUUID) {
        return firstPersonControllers.getOrDefault(playerUUID, false);
    }
    
    /**
     * Перемещает игрока в режим управления дроном от первого лица
     * 
     * @param player игрок-оператор
     * @param drone сущность дрона
     */
    private void enterFirstPersonMode(Player player, ArmorStand drone) {
        UUID playerUUID = player.getUniqueId();
        
        previousPlayerLocations.put(playerUUID, player.getLocation().clone());
        previousGameModes.put(playerUUID, player.getGameMode());
        
        player.setGameMode(GameMode.SPECTATOR);
        
        player.teleport(drone.getLocation());
        
        firstPersonControllers.put(playerUUID, true);
    }
    
    /**
     * Выводит игрока из режима управления от первого лица
     * 
     * @param player игрок-оператор
     */
    private void exitFirstPersonMode(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        Location previousLocation = previousPlayerLocations.remove(playerUUID);
        GameMode previousGameMode = previousGameModes.remove(playerUUID);
        
        if (previousLocation != null) {
            player.teleport(previousLocation);
        }
        
        if (previousGameMode != null) {
            player.setGameMode(previousGameMode);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }
        
        firstPersonControllers.remove(playerUUID);
    }
    
    /**
     * Перемещает дрон в указанном направлении (для управления от первого лица)
     * 
     * @param player игрок, управляющий дроном
     * @param direction вектор направления движения
     * @return true, если перемещение выполнено успешно
     */
    @Override
    public boolean moveDrone(Player player, Vector direction) {
        UUID playerUUID = player.getUniqueId();
        
        if (!hasActiveDrone(playerUUID)) {
            return false;
        }
        
        if (!isInFirstPersonMode(playerUUID)) {
            return false;
        }
        
        ArmorStand drone = activeDrones.get(playerUUID);
        Location currentLocation = drone.getLocation();
        
        Vector normalizedDirection = direction.normalize().multiply(speed / 10.0);
        
        Location newLocation = currentLocation.clone().add(normalizedDirection);
        
        if (newLocation.distance(previousPlayerLocations.get(playerUUID)) > operationalRange) {
            player.sendMessage(ChatColor.RED + "Дрон достиг максимальной дальности!");
            return false;
        }
        
        if (newLocation.getY() > previousPlayerLocations.get(playerUUID).getY() + maxHeight) {
            newLocation.setY(previousPlayerLocations.get(playerUUID).getY() + maxHeight);
            player.sendMessage(ChatColor.RED + "Дрон достиг максимальной высоты!");
        }
        
        drone.teleport(newLocation);
        
        player.teleport(newLocation);
        
        return true;
    }
    
    /**
     * Обрабатывает выход из режима от первого лица при отзыве дрона
     * 
     * @param playerUUID UUID игрока
     */
    private void handleFirstPersonExitOnRecall(UUID playerUUID) {
        Player player = plugin.getServer().getPlayer(playerUUID);
        
        if (player != null && isInFirstPersonMode(playerUUID)) {
            exitFirstPersonMode(player);
        }
    }
    
    @Override
    public boolean recall(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        if (!hasActiveDrone(playerUUID)) {
            player.sendMessage(ChatColor.RED + "У вас нет активного дрона для отзыва.");
            return false;
        }
        
        if (isInFirstPersonMode(playerUUID)) {
            exitFirstPersonMode(player);
        }
        
        ArmorStand drone = activeDrones.get(playerUUID);
        
        removeDrone(playerUUID);
        
        
        player.sendMessage(ChatColor.GREEN + "Дрон успешно отозван.");
        
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.5f, 1.2f);
        
        return true;
    }
    
    @Override
    public boolean recharge(Player player, int amount) {
        UUID playerUUID = player.getUniqueId();
        
        if (hasActiveDrone(playerUUID)) {
            player.sendMessage(ChatColor.RED + "Нельзя заряжать активный дрон. Сначала отзовите его.");
            return false;
        }
        
        int currentBattery = playerBatteryLevels.getOrDefault(playerUUID, 0);
        
        if (currentBattery >= batteryCapacity) {
            player.sendMessage(ChatColor.RED + "Батарея дрона уже полностью заряжена.");
            return false;
        }
        
        int newBattery = Math.min(currentBattery + amount, batteryCapacity);
        
        playerBatteryLevels.put(playerUUID, newBattery);
        
        float batteryPercent = (float) newBattery / batteryCapacity;
        player.setExp(batteryPercent);
        player.setLevel(newBattery);
        
        player.sendMessage(ChatColor.GREEN + "Батарея дрона заряжена до " + newBattery + "/" + batteryCapacity + " единиц.");
        
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.5f, 1.5f);
        
        return true;
    }
    
    @Override
    public int getCurrentBattery(Player player) {
        return playerBatteryLevels.getOrDefault(player.getUniqueId(), batteryCapacity);
    }
    
    @Override
    public boolean detonate(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        if (!hasActiveDrone(playerUUID)) {
            player.sendMessage(ChatColor.RED + "У вас нет активного дрона для подрыва.");
            return false;
        }
        
        if (explosionDamage <= 0 || explosionRadius <= 0) {
            player.sendMessage(ChatColor.RED + "Этот дрон не предназначен для самоподрыва.");
            return false;
        }
        
        if (isInFirstPersonMode(playerUUID)) {
            exitFirstPersonMode(player);
        }
        
        ArmorStand drone = activeDrones.get(playerUUID);
        Location explosionLocation = drone.getLocation();
        
        boolean result = createExplosion(player, explosionLocation);
        
        removeDrone(playerUUID);
        
        playerBatteryLevels.put(playerUUID, 0);
        
        return result;
    }
    
    /**
     * Создает взрыв в указанной локации
     * 
     * @param player игрок, активировавший взрыв
     * @param location место взрыва
     * @return true если взрыв был создан успешно
     */
    protected boolean createExplosion(Player player, Location location) {
        World world = location.getWorld();
        
        world.spawnParticle(Particle.EXPLOSION, location, 10, 0.5, 0.5, 0.5, 0.1);
        world.spawnParticle(Particle.FLAME, location, 50, 1.0, 1.0, 1.0, 0.1);
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        
        List<Entity> nearbyEntities = world.getNearbyEntities(location, explosionRadius, explosionRadius, explosionRadius)
            .stream()
            .filter(entity -> entity instanceof LivingEntity && !(entity instanceof ArmorStand) && !entity.equals(player))
            .toList();
        
        for (Entity entity : nearbyEntities) {
            LivingEntity living = (LivingEntity) entity;
            
            double distance = location.distance(living.getLocation());
            
            if (distance <= explosionRadius) {
                double damage = explosionDamage * (1 - (distance / explosionRadius));
                
                living.damage(damage, player);
                
                Vector knockback = living.getLocation().toVector()
                        .subtract(location.toVector())
                        .normalize()
                        .multiply(2.0); 
                knockback.setY(0.5); 
                
                living.setVelocity(living.getVelocity().add(knockback));
            }
        }
        
        return true;
    }
    
    /**
     * Удаляет дрон игрока
     * 
     * @param playerUUID UUID игрока
     */
    protected void removeDrone(UUID playerUUID) {
        handleFirstPersonExitOnRecall(playerUUID);
        
        ArmorStand drone = activeDrones.remove(playerUUID);
        if (drone != null && !drone.isDead()) {
            drone.remove();
        }
        
        BukkitTask task = droneTasks.remove(playerUUID);
        if (task != null) {
            task.cancel();
        }
        
        droneTargets.remove(playerUUID);
        
        firstPersonControllers.remove(playerUUID);
        previousPlayerLocations.remove(playerUUID);
        previousGameModes.remove(playerUUID);
    }
    
    /**
     * Проверяет, имеет ли игрок активный дрон
     * 
     * @param playerUUID UUID игрока
     * @return true, если у игрока есть активный дрон
     */
    public boolean hasActiveDrone(UUID playerUUID) {
        return activeDrones.containsKey(playerUUID) && activeDrones.get(playerUUID) != null && !activeDrones.get(playerUUID).isDead();
    }
    
    /**
     * Устанавливает цель для перемещения дрона
     * 
     * @param playerUUID UUID игрока
     * @param target целевая точка
     */
    public void setDroneTarget(UUID playerUUID, Location target) {
        droneTargets.put(playerUUID, target);
    }
    
    /**
     * Получает сущность дрона для указанного игрока
     * 
     * @param playerUUID UUID игрока
     * @return сущность ArmorStand дрона или null
     */
    public ArmorStand getDroneEntity(UUID playerUUID) {
        return activeDrones.get(playerUUID);
    }
} 