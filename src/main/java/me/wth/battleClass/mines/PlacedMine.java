package me.wth.battleClass.mines;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс, представляющий установленную мину в мире
 */
public class PlacedMine {
    private final String instanceId;  
    private final String ownerId;     
    private final String mineType;    
    private final Location location;  
    private final long placementTime; 
    private final long expirationTime; 
    private final Item itemEntity;    
    private final ArmorStand stand;   
    private Material originalMaterial; 
    private boolean activated;        
    private boolean exploded;         
    
    /**
     * Создает объект установленной мины
     * 
     * @param instanceId уникальный идентификатор экземпляра мины
     * @param ownerId UUID игрока, установившего мину
     * @param mineType тип мины
     * @param location местоположение мины
     * @param lifeTimeInTicks время жизни мины в тиках (0 - бесконечно)
     * @param itemEntity сущность предмета мины в мире (может быть null)
     * @param stand стенд для визуального отображения (может быть null)
     */
    public PlacedMine(String instanceId, String ownerId, String mineType, Location location, 
                      int lifeTimeInTicks, Item itemEntity, ArmorStand stand) {
        this.instanceId = instanceId;
        this.ownerId = ownerId;
        this.mineType = mineType;
        this.location = location.clone();
        this.placementTime = System.currentTimeMillis();
        this.itemEntity = itemEntity;
        this.stand = stand;
        this.activated = false;
        this.exploded = false;
        this.originalMaterial = Material.STONE; 
        
        this.expirationTime = lifeTimeInTicks > 0 ? 
                placementTime + (lifeTimeInTicks * 50) : 0; 
    }
    
    /**
     * Устанавливает оригинальный материал блока
     * @param material оригинальный материал блока до установки мины
     */
    public void setOriginalMaterial(Material material) {
        this.originalMaterial = material;
    }
    
    /**
     * Получает оригинальный материал блока
     * @return оригинальный материал блока
     */
    public Material getOriginalMaterial() {
        return originalMaterial;
    }
    
    /**
     * Получает уникальный идентификатор экземпляра мины
     * @return уникальный идентификатор
     */
    public String getInstanceId() {
        return instanceId;
    }
    
    /**
     * Получает UUID игрока, установившего мину
     * @return UUID игрока
     */
    public String getOwnerId() {
        return ownerId;
    }
    
    /**
     * Получает тип мины
     * @return тип мины
     */
    public String getMineType() {
        return mineType;
    }
    
    /**
     * Получает местоположение мины
     * @return местоположение мины
     */
    public Location getLocation() {
        return location.clone();
    }
    
    /**
     * Получает блок, на котором установлена мина
     * @return блок мины
     */
    public Block getMineBlock() {
        return location.getBlock();
    }
    
    /**
     * Получает время установки мины
     * @return время установки в миллисекундах
     */
    public long getPlacementTime() {
        return placementTime;
    }
    
    /**
     * Получает время истечения срока мины
     * @return время истечения в миллисекундах или 0, если срок бесконечен
     */
    public long getExpirationTime() {
        return expirationTime;
    }
    
    /**
     * Проверяет, активирована ли мина
     * @return true, если мина активирована, иначе false
     */
    public boolean isActivated() {
        return activated;
    }
    
    /**
     * Устанавливает состояние активации мины
     * @param activated новое состояние активации
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }
    
    /**
     * Проверяет, взорвалась ли мина
     * @return true, если мина взорвалась, иначе false
     */
    public boolean isExploded() {
        return exploded;
    }
    
    /**
     * Устанавливает состояние взрыва мины
     * @param exploded новое состояние взрыва
     */
    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }
    
    /**
     * Получает сущность предмета мины (устарело)
     * @return сущность предмета
     */
    public Item getItemEntity() {
        return itemEntity;
    }
    
    /**
     * Получает стенд для визуального отображения (устарело)
     * @return стенд или null, если не используется
     */
    public ArmorStand getStand() {
        return stand;
    }
    
    /**
     * Удаляет мину из мира и восстанавливает оригинальный блок
     */
    public void remove() {
        if (itemEntity != null && itemEntity.isValid()) {
            itemEntity.remove();
        }
        
        if (stand != null && stand.isValid()) {
            stand.remove();
        }
        
        Block mineBlock = getMineBlock();
        if (mineBlock != null && (mineBlock.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE 
                || mineBlock.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
            mineBlock.setType(originalMaterial);
        }
    }
    
    /**
     * Проверяет, находится ли игрок на мине
     * @param player игрок для проверки
     * @return true, если игрок находится на мине, иначе false
     */
    public boolean isPlayerOnMine(Player player) {
        Location playerLoc = player.getLocation();
        Block blockUnderPlayer = playerLoc.clone().subtract(0, 1, 0).getBlock();
        
        return blockUnderPlayer.getLocation().getBlockX() == location.getBlockX() &&
               blockUnderPlayer.getLocation().getBlockY() == location.getBlockY() &&
               blockUnderPlayer.getLocation().getBlockZ() == location.getBlockZ();
    }
    
    /**
     * Проверяет, действительна ли все еще эта мина
     * @return true, если мина все еще действительна (существует в мире и не взорвалась), иначе false
     */
    public boolean isValid() {
        if (exploded) return false;
        
        Block mineBlock = getMineBlock();
        boolean blockExists = mineBlock != null && 
                (mineBlock.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE || 
                 mineBlock.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        
        return blockExists && (expirationTime == 0 || System.currentTimeMillis() < expirationTime);
    }
    
    /**
     * Активирует мину
     */
    public void activate() {
        if (!activated && !exploded) {
            activated = true;
            
            World world = location.getWorld();
            if (world != null) {
                world.spawnParticle(org.bukkit.Particle.SMOKE, 
                                    location.clone().add(0, 0.1, 0), 
                                    5, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }
    
    /**
     * Взрывает мину
     * @param triggeringEntity сущность, вызвавшая взрыв
     * @param damage урон от взрыва
     * @param blastRadius радиус взрыва
     */
    public void explode(Entity triggeringEntity, double damage, double blastRadius) {
        if (exploded) {
            return; 
        }
        
        exploded = true;
        
        World world = location.getWorld();
        if (world != null) {
            world.createExplosion(location, 0, false, false);
            
            boolean isRussianMine = "mpm3".equals(mineType); 
            boolean isAmericanMine = "m7_spider".equals(mineType); 
            
            if (isRussianMine) {
                createRandomCrater(world, location, blastRadius, 1, 3, 0.8);
                
                createShrapnelEffect(world, location, blastRadius, 16, 3.0);
            } else if (isAmericanMine) {
                createRandomCrater(world, location, blastRadius, 2, 3, 0.6);
                
                createShrapnelEffect(world, location, blastRadius, 12, 3.5);
            } else {
                createRandomCrater(world, location, blastRadius, 1, 2, 0.7);
                
                createShrapnelEffect(world, location, blastRadius, 8, 2.5);
            }
            
            world.spawnParticle(org.bukkit.Particle.EXPLOSION, location, 1);
            world.spawnParticle(org.bukkit.Particle.LARGE_SMOKE, location, 30, 0.8, 0.8, 0.8, 0.15);
            world.spawnParticle(org.bukkit.Particle.FLAME, location, 15, 0.6, 0.6, 0.6, 0.1);
            
            if (isRussianMine) {
                world.spawnParticle(Particle.LARGE_SMOKE, location, 40, 1.0, 0.8, 1.0, 0.2);
            } else if (isAmericanMine) {
                world.spawnParticle(org.bukkit.Particle.LAVA, location, 20, 0.7, 0.5, 0.7, 0.1);
                world.spawnParticle(org.bukkit.Particle.FLAME, location, 25, 0.8, 0.5, 0.8, 0.15);
            }
            
            for (Entity entity : world.getNearbyEntities(location, blastRadius, blastRadius, blastRadius)) {
                if (entity instanceof org.bukkit.entity.LivingEntity && !(entity instanceof ArmorStand)) {
                    org.bukkit.entity.LivingEntity livingEntity = (org.bukkit.entity.LivingEntity) entity;
                    
                    double distance = location.distance(livingEntity.getLocation());
                    
                    if (distance <= blastRadius) {
                        double scaledDamage = damage * (1 - distance / blastRadius);
                        
                        if (scaledDamage > 0) {
                            livingEntity.damage(scaledDamage);
                            
                            org.bukkit.util.Vector knockback = livingEntity.getLocation().toVector()
                                    .subtract(location.toVector())
                                    .normalize()
                                    .multiply(2.5); 
                            
                            knockback.setY(knockback.getY() + 0.5 + ThreadLocalRandom.current().nextDouble() * 0.5);
                            
                            livingEntity.setVelocity(knockback);
                        }
                    }
                }
            }
            
            world.playSound(location, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.8f, 0.9f);
            
            if (ThreadLocalRandom.current().nextDouble() < 0.7) {
                world.playSound(location, org.bukkit.Sound.BLOCK_STONE_BREAK, 1.0f, 0.5f);
            }
            
            Block mineBlock = getMineBlock();
            if (mineBlock != null && (mineBlock.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE || 
                                      mineBlock.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                mineBlock.setType(originalMaterial);
            }
        }
        
        if (itemEntity != null || stand != null) {
            remove();
        }
    }
    
    /**
     * Создает случайный кратер от взрыва
     * @param world мир, в котором создается кратер
     * @param center центр взрыва
     * @param radius базовый радиус взрыва
     * @param minDepth минимальная глубина кратера
     * @param maxDepth максимальная глубина кратера
     * @param radiusFactor множитель радиуса кратера (1.0 = полный радиус)
     */
    private void createRandomCrater(World world, Location center, double radius, 
                                  int minDepth, int maxDepth, double radiusFactor) {
        int craterRadius = (int) Math.ceil(radius * radiusFactor);
        
        if (craterRadius < 1) craterRadius = 1;
        
        if (craterRadius > 5) craterRadius = 5;
        
        int centerOffsetX = ThreadLocalRandom.current().nextInt(-1, 2);
        int centerOffsetZ = ThreadLocalRandom.current().nextInt(-1, 2);
        
        Location craterCenter = center.clone().add(centerOffsetX, 0, centerOffsetZ);
        
        int depth = ThreadLocalRandom.current().nextInt(minDepth, maxDepth + 1);
        
        Block centerBlock = craterCenter.getBlock();
        
        Material[] craterMaterials = {
            Material.AIR,          
            Material.DIRT,         
            Material.COARSE_DIRT,  
            Material.GRAVEL        
        };
        
        Material[] specialEffects = {
            Material.FIRE,         
            Material.COAL_BLOCK,   
            Material.BLACKSTONE,   
            Material.NETHERRACK    
        };
        
        double xScaleModifier = 0.8 + ThreadLocalRandom.current().nextDouble() * 0.4; 
        double zScaleModifier = 0.8 + ThreadLocalRandom.current().nextDouble() * 0.4; 
        
        for (int x = -craterRadius; x <= craterRadius; x++) {
            for (int z = -craterRadius; z <= craterRadius; z++) {
                double ellipseX = (double) x / (craterRadius * xScaleModifier);
                double ellipseZ = (double) z / (craterRadius * zScaleModifier);
                
                double ellipseValue = ellipseX * ellipseX + ellipseZ * ellipseZ;
                
                if (ellipseValue <= 1.0) {
                    if (ThreadLocalRandom.current().nextDouble() < 0.9 - 0.3 * ellipseValue) { 
                        int pointDepth = (int) Math.ceil(depth * (1.0 - ellipseValue * 0.5));
                        
                        for (int y = 0; y >= -pointDepth; y--) {
                            Block block = centerBlock.getRelative(x, y, z);
                            Material originalType = block.getType();
                            
                            if (originalType == Material.AIR || 
                                originalType == Material.WATER || 
                                originalType == Material.LAVA || 
                                originalType == Material.BEDROCK) {
                                continue;
                            }
                            
                            if (y == 0) {
                                if (ThreadLocalRandom.current().nextDouble() < 0.25 - 0.2 * ellipseValue) {
                                    if (ThreadLocalRandom.current().nextDouble() < 0.15) {
                                        Block blockBelow = block.getRelative(0, -1, 0);
                                        if (blockBelow.getType().isSolid()) {
                                            block.setType(Material.FIRE);
                                        } else {
                                            block.setType(Material.AIR);
                                        }
                                    } else {
                                        block.setType(specialEffects[ThreadLocalRandom.current().nextInt(1, specialEffects.length)]);
                                    }
                                } else {
                                    block.setType(Material.AIR);
                                }
                            } else {
                                if (ellipseValue < 0.3 && y == -pointDepth) {
                                    double materialRandomizer = ThreadLocalRandom.current().nextDouble();
                                    if (materialRandomizer < 0.5) {
                                        block.setType(Material.COARSE_DIRT);
                                    } else if (materialRandomizer < 0.7) {
                                        block.setType(Material.GRAVEL);
                                    } else if (materialRandomizer < 0.85) {
                                        block.setType(Material.COAL_BLOCK);
                                    } else {
                                        block.setType(Material.BLACKSTONE);
                                    }
                                } else if (ellipseValue > 0.7) {
                                    if (ThreadLocalRandom.current().nextDouble() < 0.4) {
                                        block.setType(craterMaterials[ThreadLocalRandom.current().nextInt(1, craterMaterials.length)]);
                                    }
                                } else {
                                    int materialIndex = ThreadLocalRandom.current().nextInt(craterMaterials.length);
                                    block.setType(craterMaterials[materialIndex]);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        int debrisCount = ThreadLocalRandom.current().nextInt(3, 7);
        for (int i = 0; i < debrisCount; i++) {
            double debrisDistance = craterRadius + ThreadLocalRandom.current().nextDouble() * 2;
            double angle = ThreadLocalRandom.current().nextDouble() * 2 * Math.PI;
            int debrisX = (int) Math.round(Math.cos(angle) * debrisDistance);
            int debrisZ = (int) Math.round(Math.sin(angle) * debrisDistance);
            
            Block debrisBlock = centerBlock.getRelative(debrisX, 0, debrisZ);
            
            while (debrisBlock.getType() == Material.AIR && debrisBlock.getY() > 0) {
                debrisBlock = debrisBlock.getRelative(0, -1, 0);
            }
            
            Block surfaceBlock = debrisBlock.getRelative(0, 1, 0);
            
            Material debrisMaterial;
            double debrisType = ThreadLocalRandom.current().nextDouble();
            
            if (debrisType < 0.4) {
                debrisMaterial = Material.COBBLESTONE;
            } else if (debrisType < 0.7) {
                debrisMaterial = Material.GRAVEL;
            } else if (debrisType < 0.85) {
                debrisMaterial = Material.COAL_BLOCK;
            } else if (debrisType < 0.95) {
                debrisMaterial = Material.BLACKSTONE;
            } else {
                debrisMaterial = Material.NETHERRACK;
                if (ThreadLocalRandom.current().nextDouble() < 0.3) {
                    Block fireBlock = surfaceBlock.getRelative(0, 1, 0);
                    fireBlock.setType(Material.FIRE);
                }
            }
            
            surfaceBlock.setType(debrisMaterial);
        }
    }
    
    /**
     * Создает эффект шрапнели (летящих частиц) при взрыве мины
     * 
     * @param world мир, в котором происходит взрыв
     * @param center позиция взрыва
     * @param radius радиус поражения
     * @param count количество осколков
     * @param velocity базовая скорость осколков
     */
    private void createShrapnelEffect(World world, Location center, double radius, int count, double velocity) {
        for (int i = 0; i < count; i++) {
            double phi = ThreadLocalRandom.current().nextDouble() * 2 * Math.PI; 
            double theta = ThreadLocalRandom.current().nextDouble() * Math.PI;   
            
            double particleVelocity = velocity * (0.7 + ThreadLocalRandom.current().nextDouble() * 0.6);
            
            double x = Math.sin(theta) * Math.cos(phi) * particleVelocity;
            double y = Math.cos(theta) * particleVelocity;
            double z = Math.sin(theta) * Math.sin(phi) * particleVelocity;
            
            y = Math.abs(y) * 0.5 + ThreadLocalRandom.current().nextDouble() * 0.5;
            
            double offsetX = ThreadLocalRandom.current().nextDouble() * 0.3 - 0.15;
            double offsetY = ThreadLocalRandom.current().nextDouble() * 0.3;
            double offsetZ = ThreadLocalRandom.current().nextDouble() * 0.3 - 0.15;
            
            Location particleLocation = center.clone().add(offsetX, offsetY, offsetZ);
            
            org.bukkit.Particle particleType;
            double particleChance = ThreadLocalRandom.current().nextDouble();
            
            if (particleChance < 0.3) {
                particleType = org.bukkit.Particle.ITEM;
                world.spawnParticle(particleType, particleLocation, 4, 0.1, 0.1, 0.1, 0.05, 
                        new org.bukkit.inventory.ItemStack(Material.IRON_NUGGET));
            } else if (particleChance < 0.6) {
                particleType = org.bukkit.Particle.ITEM;
                world.spawnParticle(particleType, particleLocation, 4, 0.1, 0.1, 0.1, 0.05,
                        new org.bukkit.inventory.ItemStack(Material.COBBLESTONE));
            } else if (particleChance < 0.85) {
                particleType = org.bukkit.Particle.ITEM;
                world.spawnParticle(particleType, particleLocation, 4, 0.1, 0.1, 0.1, 0.05,
                        new org.bukkit.inventory.ItemStack(Material.DIRT));
            } else {
                particleType = org.bukkit.Particle.FLAME;
                world.spawnParticle(particleType, particleLocation, 2, 0.05, 0.05, 0.05, 0.01);
                continue; 
            }
            
            for (double t = 0.0; t <= 1.0; t += 0.2) {
                double dx = x * t;
                double dy = y * t - 4.9 * t * t; 
                double dz = z * t;
                
                Location trailLoc = particleLocation.clone().add(dx, dy, dz);
                
                final double finalT = t;
                new org.bukkit.scheduler.BukkitRunnable() {
                    @Override
                    public void run() {
                        if (finalT < 0.3) {
                            world.spawnParticle(org.bukkit.Particle.SMOKE, trailLoc, 1, 0.02, 0.02, 0.02, 0.005);
                        }
                        
                        world.spawnParticle(org.bukkit.Particle.CRIT, trailLoc, 1, 0.0, 0.0, 0.0, 0.01);
                    }
                }.runTaskLater(me.wth.battleClass.BattleClass.getPlugin(me.wth.battleClass.BattleClass.class), 
                        (long)(t * 10)); 
            }
        }
    }
    
    /**
     * Обезвреживает мину
     * @param player игрок, обезвредивший мину
     * @return ItemStack мины, который можно выдать игроку
     */
    public ItemStack defuse(Player player) {
        if (exploded) {
            return null;
        }
        
        remove();
        
        
        return null; 
    }
} 