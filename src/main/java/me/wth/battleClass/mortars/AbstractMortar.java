package me.wth.battleClass.mortars;

import me.wth.battleClass.BattleClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
 * Базовый абстрактный класс для реализации минометов
 */
public abstract class AbstractMortar implements Mortar {
    protected final BattleClass plugin;
    protected final String id;
    protected final String displayName;
    protected final List<String> description;
    protected final Material material;
    protected final int maxRange;
    protected final int minRange;
    protected final double directDamage;
    protected final double explosionRadius;
    protected final int reloadTime;
    protected final boolean silent;
    protected final int weight;
    
    private static final Map<UUID, Long> lastFireTime = new HashMap<>();
    
    /**
     * Конструктор для создания миномета
     *
     * @param plugin экземпляр плагина
     * @param id идентификатор миномета
     * @param displayName отображаемое название
     * @param description описание миномета
     * @param material материал для отображения в инвентаре
     * @param maxRange максимальная дальность стрельбы в блоках
     * @param minRange минимальная дальность стрельбы в блоках
     * @param directDamage урон от прямого попадания
     * @param explosionRadius радиус взрыва в блоках
     * @param reloadTime время перезарядки в секундах
     * @param silent является ли миномет бесшумным
     * @param weight вес миномета (1-10)
     */
    public AbstractMortar(BattleClass plugin, String id, String displayName, List<String> description,
                          Material material, int maxRange, int minRange, double directDamage,
                          double explosionRadius, int reloadTime, boolean silent, int weight) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.maxRange = maxRange;
        this.minRange = minRange;
        this.directDamage = directDamage;
        this.explosionRadius = explosionRadius;
        this.reloadTime = reloadTime;
        this.silent = silent;
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
    public int getMaxRange() {
        return maxRange;
    }
    
    @Override
    public int getMinRange() {
        return minRange;
    }
    
    @Override
    public double getDirectDamage() {
        return directDamage;
    }
    
    @Override
    public double getExplosionRadius() {
        return explosionRadius;
    }
    
    @Override
    public int getReloadTime() {
        return reloadTime;
    }
    
    @Override
    public boolean isSilent() {
        return silent;
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
            meta.setDisplayName("§a" + displayName);
            
            List<String> lore = new ArrayList<>();
            lore.addAll(description);
            lore.add("");
            lore.add("§7Дальность: §f" + minRange + "-" + maxRange + " блоков");
            lore.add("§7Урон: §f" + directDamage);
            lore.add("§7Радиус взрыва: §f" + explosionRadius + " блоков");
            lore.add("§7Перезарядка: §f" + reloadTime + " сек.");
            lore.add("§7Бесшумность: " + (silent ? "§aДа" : "§cНет"));
            lore.add("§7Вес: §f" + weight + "/10");
            lore.add("");
            lore.add("§eПКМ - установить миномет");
            lore.add("§eПрисесть+ПКМ - настроить угол и силу");
            
            meta.setLore(lore);
            
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            
            NamespacedKey key = new NamespacedKey(plugin, "mortar_id");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, id);
            
            itemStack.setItemMeta(meta);
        }
        
        return itemStack;
    }
    
    @Override
    public boolean fire(Player player, double angle, int power) {
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastFire = lastFireTime.getOrDefault(playerUUID, 0L);
        
        if (currentTime - lastFire < reloadTime * 1000) {
            long remainingTime = (reloadTime * 1000 - (currentTime - lastFire)) / 1000;
            player.sendMessage("§cМиномет перезаряжается! Осталось §f" + remainingTime + "§c сек.");
            return false;
        }
        
        power = Math.max(1, Math.min(100, power));
        
        angle = Math.max(30, Math.min(85, angle));
        
        double angleRad = Math.toRadians(angle);
        
        double velocityMultiplier = power / 100.0 * 2.0; 
        double velocityY = Math.sin(angleRad) * velocityMultiplier;
        double velocityXZ = Math.cos(angleRad) * velocityMultiplier;
        
        Vector direction = player.getLocation().getDirection().normalize();
        direction.setY(0).normalize(); 
        
        Vector velocity = new Vector(
                direction.getX() * velocityXZ,
                velocityY,
                direction.getZ() * velocityXZ
        );
        
        launchProjectile(player, velocity);
        
        lastFireTime.put(playerUUID, currentTime);
        
        player.sendMessage("§aВыстрел из миномета §f" + displayName + "§a! Угол: §f" + angle + "°§a, мощность: §f" + power + "%");
        
        if (!silent) {
            player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        } else {
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_STONE_BREAK, 0.3f, 0.5f);
        }
        
        return true;
    }
    
    /**
     * Запускает снаряд миномета с заданной скоростью
     * 
     * @param player игрок, запустивший снаряд
     * @param velocity вектор начальной скорости снаряда
     */
    protected void launchProjectile(Player player, Vector velocity) {
        org.bukkit.entity.Projectile projectile = player.launchProjectile(
                org.bukkit.entity.Snowball.class, velocity
        );
        
        projectile.setMetadata("mortar_projectile", new org.bukkit.metadata.FixedMetadataValue(plugin, id));
        
        trackProjectileTrajectory(projectile);
    }
    
    /**
     * Отслеживает траекторию снаряда и создает эффект взрыва при попадании
     * 
     * @param projectile снаряд для отслеживания
     */
    protected void trackProjectileTrajectory(org.bukkit.entity.Projectile projectile) {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                if (projectile.isDead() || !projectile.isValid()) {
                    Location impactLocation = projectile.getLocation();
                    createExplosion(impactLocation);
                    this.cancel();
                    return;
                }
                
                projectile.getWorld().spawnParticle(
                        org.bukkit.Particle.SMOKE,
                        projectile.getLocation(),
                        1, 0, 0, 0, 0.01
                );
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
    
    /**
     * Создает взрыв в указанной локации
     * 
     * @param location место взрыва
     */
    protected void createExplosion(Location location) {
        location.getWorld().spawnParticle(
                org.bukkit.Particle.EXPLOSION,
                location,
                10, 0.5, 0.5, 0.5, 0.1
        );
        
        if (!silent) {
            location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
        }
        
        for (org.bukkit.entity.Entity entity : location.getWorld().getNearbyEntities(location, explosionRadius, explosionRadius, explosionRadius)) {
            if (entity instanceof org.bukkit.entity.LivingEntity && !(entity instanceof org.bukkit.entity.ArmorStand)) {
                org.bukkit.entity.LivingEntity livingEntity = (org.bukkit.entity.LivingEntity) entity;
                
                double distance = livingEntity.getLocation().distance(location);
                if (distance <= explosionRadius) {
                    double damage = directDamage * (1 - distance / explosionRadius);
                    livingEntity.damage(damage);
                    
                    Vector knockback = livingEntity.getLocation().toVector().subtract(location.toVector()).normalize().multiply(1.5);
                    livingEntity.setVelocity(knockback);
                }
            }
        }
    }
}