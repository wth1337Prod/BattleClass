package me.wth.battleClass.flamethrowers;

import me.wth.battleClass.BattleClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Базовый абстрактный класс для реализации огнеметов
 */
public abstract class AbstractFlamethrower implements Flamethrower {
    protected final BattleClass plugin;
    protected final String id;
    protected final String displayName;
    protected final List<String> description;
    protected final Material material;
    protected final int range;
    protected final double damagePerSecond;
    protected final double radius;
    protected final int burnDuration;
    protected final int fuelCapacity;
    protected final double fuelConsumption;
    protected final int weight;
    
    private static final Map<UUID, Integer> playerFuelLevels = new HashMap<>();
    
    private static final Map<UUID, Long> lastUseTime = new HashMap<>();
    
    /**
     * Конструктор для создания огнемета
     *
     * @param plugin экземпляр плагина
     * @param id идентификатор огнемета
     * @param displayName отображаемое название
     * @param description описание огнемета
     * @param material материал для отображения в инвентаре
     * @param range дальность действия в блоках
     * @param damagePerSecond урон в секунду от прямого попадания
     * @param radius радиус действия в блоках
     * @param burnDuration длительность горения цели в секундах
     * @param fuelCapacity емкость топливного бака
     * @param fuelConsumption расход топлива (единиц в секунду)
     * @param weight вес огнемета (1-10)
     */
    public AbstractFlamethrower(BattleClass plugin, String id, String displayName, List<String> description,
                          Material material, int range, double damagePerSecond, double radius,
                          int burnDuration, int fuelCapacity, double fuelConsumption, int weight) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.range = range;
        this.damagePerSecond = damagePerSecond;
        this.radius = radius;
        this.burnDuration = burnDuration;
        this.fuelCapacity = fuelCapacity;
        this.fuelConsumption = fuelConsumption;
        this.weight = weight;
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
    public int getRange() {
        return range;
    }
    
    @Override
    public double getDamagePerSecond() {
        return damagePerSecond;
    }
    
    @Override
    public double getRadius() {
        return radius;
    }
    
    @Override
    public int getBurnDuration() {
        return burnDuration;
    }
    
    @Override
    public int getFuelCapacity() {
        return fuelCapacity;
    }
    
    @Override
    public double getFuelConsumption() {
        return fuelConsumption;
    }
    
    @Override
    public int getWeight() {
        return weight;
    }
    
    @Override
    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6" + displayName);
            
            List<String> lore = new ArrayList<>();
            lore.addAll(description);
            lore.add("");
            lore.add("§7Дальность: §f" + range + " блоков");
            lore.add("§7Урон: §f" + damagePerSecond + "/сек");
            lore.add("§7Радиус действия: §f" + radius + " блоков");
            lore.add("§7Длительность горения: §f" + burnDuration + " сек.");
            lore.add("§7Емкость бака: §f" + fuelCapacity + " единиц");
            lore.add("§7Расход топлива: §f" + fuelConsumption + " ед./сек");
            lore.add("§7Вес: §f" + weight + "/10");
            lore.add("");
            lore.add("§eПКМ - выстрел из огнемета");
            lore.add("§eШифт+ПКМ - перезаправка (нужен канистра)");
            
            meta.setLore(lore);
            
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            
            NamespacedKey key = new NamespacedKey(plugin, "flamethrower_id");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, id);
            
            itemStack.setItemMeta(meta);
        }
        
        return itemStack;
    }
    
    @Override
    public boolean fire(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        int currentFuel = playerFuelLevels.getOrDefault(playerUUID, fuelCapacity);
        
        if (currentFuel <= 0) {
            player.sendMessage("§cВ огнемете закончилось топливо! Перезаправьте его.");
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long lastUse = lastUseTime.getOrDefault(playerUUID, 0L);
        
        if (currentTime - lastUse < 100) { 
            return false;
        }
        
        double fuelUsed = fuelConsumption / 10.0;
        
        currentFuel -= Math.ceil(fuelUsed);
        if (currentFuel < 0) currentFuel = 0;
        playerFuelLevels.put(playerUUID, currentFuel);
        
        lastUseTime.put(playerUUID, currentTime);
        
        createFlameEffect(player);
        
        applyDamageToTargets(player);
        
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 2.0f, 0.5f);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 0.8f);
        
        return true;
    }
    
    /**
     * Создает визуальный эффект пламени
     * 
     * @param player игрок, стреляющий из огнемета
     */
    protected void createFlameEffect(Player player) {
        World world = player.getWorld();
        Location startLocation = player.getEyeLocation();
        Vector direction = startLocation.getDirection().normalize();
        
        for (double i = 0.5; i < range; i += 0.5) {
            Location currentLocation = startLocation.clone().add(direction.clone().multiply(i));
            
            double spreadFactor = i / 5.0; 
            double offsetX = (Math.random() - 0.5) * spreadFactor;
            double offsetY = (Math.random() - 0.5) * spreadFactor;
            double offsetZ = (Math.random() - 0.5) * spreadFactor;
            
            world.spawnParticle(
                Particle.FLAME,
                currentLocation.clone().add(offsetX, offsetY, offsetZ),
                1, 0.0, 0.0, 0.0, 0.01
            );
            
            if (Math.random() < 0.3) { 
                world.spawnParticle(
                    Particle.SMOKE,
                    currentLocation.clone().add(offsetX, offsetY, offsetZ),
                    1, 0.0, 0.0, 0.0, 0.01
                );
            }
            
            if (Math.random() < 0.05 && currentLocation.getBlock().getType().isSolid()) {
                Location fireLocation = currentLocation.clone();
                fireLocation.setY(fireLocation.getY() + 1.0); 
                
                if (fireLocation.getBlock().getType() == Material.AIR) {
                    fireLocation.getBlock().setType(Material.FIRE);
                    
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (fireLocation.getBlock().getType() == Material.FIRE) {
                            fireLocation.getBlock().setType(Material.AIR);
                        }
                    }, burnDuration * 20L); 
                }
            }
        }
    }
    
    /**
     * Применяет урон к целям в зоне действия огнемета
     * 
     * @param player игрок, стреляющий из огнемета
     */
    protected void applyDamageToTargets(Player player) {
        Location playerLocation = player.getEyeLocation();
        Vector direction = playerLocation.getDirection().normalize();
        
        for (Entity entity : player.getWorld().getNearbyEntities(playerLocation, range, range, range)) {
            if (!(entity instanceof LivingEntity) || entity == player) {
                continue;
            }
            
            LivingEntity target = (LivingEntity) entity;
            Location targetLocation = target.getLocation();
            
            Vector toTarget = targetLocation.clone().subtract(playerLocation).toVector();
            double distance = toTarget.length();
            
            if (distance <= range) {
                double dot = toTarget.normalize().dot(direction);
                double angle = Math.acos(dot);
                
                double angleThreshold = Math.atan(radius / distance);
                
                if (angle <= angleThreshold) {
                    if (!hasLineOfSight(playerLocation, targetLocation)) {
                        continue; 
                    }
                    
                    double damageMultiplier = 1.0 - (distance / range * 0.7); 
                    double damage = damagePerSecond * damageMultiplier / 10.0; 
                    
                    target.damage(damage, player);
                    
                    if (!target.isDead()) {
                        target.setFireTicks(burnDuration * 20); 
                    }
                }
            }
        }
    }
    
    /**
     * Проверяет, есть ли прямая видимость между двумя точками
     * 
     * @param from начальная точка
     * @param to конечная точка
     * @return true, если прямая видимость есть
     */
    private boolean hasLineOfSight(Location from, Location to) {
        Vector direction = to.clone().subtract(from).toVector();
        double distance = direction.length();
        direction.normalize();
        
        for (double i = 0.5; i < distance; i += 0.5) {
            Location check = from.clone().add(direction.clone().multiply(i));
            
            if (check.getBlock().getType().isOccluding()) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean refuel(Player player, int amount) {
        UUID playerUUID = player.getUniqueId();
        int currentFuel = playerFuelLevels.getOrDefault(playerUUID, 0);
        
        if (currentFuel >= fuelCapacity) {
            player.sendMessage("§cОгнемет уже полностью заправлен!");
            return false;
        }
        
        int newFuel = Math.min(currentFuel + amount, fuelCapacity);
        playerFuelLevels.put(playerUUID, newFuel);
        
        player.sendMessage("§aОгнемет заправлен! Текущий уровень топлива: " + newFuel + "/" + fuelCapacity);
        
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL, 1.0f, 1.0f);
        
        return true;
    }
    
    /**
     * Получает текущий уровень топлива для игрока
     * 
     * @param playerUUID UUID игрока
     * @return текущий уровень топлива
     */
    public int getCurrentFuel(UUID playerUUID) {
        return playerFuelLevels.getOrDefault(playerUUID, fuelCapacity);
    }
    
    /**
     * Устанавливает уровень топлива для игрока
     * 
     * @param playerUUID UUID игрока
     * @param fuel новый уровень топлива
     */
    public void setCurrentFuel(UUID playerUUID, int fuel) {
        playerFuelLevels.put(playerUUID, Math.max(0, Math.min(fuel, fuelCapacity)));
    }
} 