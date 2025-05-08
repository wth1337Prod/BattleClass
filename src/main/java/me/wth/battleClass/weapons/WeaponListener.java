package me.wth.battleClass.weapons;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.armor.Armor;
import me.wth.battleClass.armor.ArmorManager;
import me.wth.battleClass.armor.Helmet;
import me.wth.battleClass.armor.HelmetManager;
import me.wth.battleClass.medical.InjuryManager;
import me.wth.battleClass.weapons.Weapon;
import me.wth.battleClass.weapons.ammo.AmmoManager;
import me.wth.battleClass.weapons.ammo.AmmoType;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class WeaponListener implements Listener {
    private final BattleClass plugin;
    private final AmmoManager ammoManager;
    private final ArmorManager armorManager;
    private final HelmetManager helmetManager;
    private final InjuryManager injuryManager;
    private final Random random = new Random();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> reloadCooldowns = new HashMap<>();
    private final Map<UUID, String> reloadingWeapons = new HashMap<>();
    
    private final Map<UUID, Map<String, Integer>> weaponMagazines = new HashMap<>();
    
    private final Map<UUID, String> lastDamageWeapon = new HashMap<>();
    
    public WeaponListener(BattleClass plugin, AmmoManager ammoManager, ArmorManager armorManager, HelmetManager helmetManager) {
        this.plugin = plugin;
        this.ammoManager = ammoManager;
        this.armorManager = armorManager;
        this.helmetManager = helmetManager;
        this.injuryManager = plugin.getInjuryManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "weapon_id");
        
        if (!container.has(key, PersistentDataType.STRING)) {
            return;
        }
        
        String weaponId = container.get(key, PersistentDataType.STRING);
        Weapon weapon = plugin.getWeaponManager().getWeapon(weaponId);
        
        if (weapon == null) {
            return;
        }
        
        UUID playerUUID = player.getUniqueId();
        
        if (reloadingWeapons.containsKey(playerUUID) && reloadingWeapons.get(playerUUID).equals(weaponId)) {
            event.setCancelled(true);
            return;
        }
        
        AmmoType selectedAmmo = ammoManager.getSelectedAmmo(player, weaponId);
        
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            
            if (selectedAmmo == null) {
                player.sendMessage("§cУ вас нет патронов для этого оружия!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                return;
            }
            
            if (!selectedAmmo.getWeaponId().equals(weaponId)) {
                player.sendMessage("§cЭти патроны не подходят для данного оружия!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                return;
            }
            
            int ammoCount = ammoManager.getAmmoCount(player, selectedAmmo.getId());
            if (ammoCount <= 0) {
                player.sendMessage("§cУ вас закончились патроны!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                return;
            }
            
            Map<String, Integer> playerMagazines = weaponMagazines.computeIfAbsent(playerUUID, k -> new HashMap<>());
            
            int currentMagazineAmmo = playerMagazines.getOrDefault(weaponId, 0);
            
            if (currentMagazineAmmo <= 0) {
                player.sendMessage("§cМагазин пуст! Нажмите SHIFT + ЛКМ для перезарядки");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                return;
            }
            
            long currentTime = System.currentTimeMillis();
            
            if (cooldowns.containsKey(playerUUID)) {
                long lastFireTime = cooldowns.get(playerUUID);
                long cooldownTime = (long) (1000 / weapon.getFireRate());
                
                if (currentTime - lastFireTime < cooldownTime) {
                    return;
                }
            }
            
            cooldowns.put(playerUUID, currentTime);
            
            playerMagazines.put(weaponId, currentMagazineAmmo - 1);
            
            double damage = weapon.getDamage() * (1.0 + selectedAmmo.getDamageModifier());
            double accuracy = weapon.getAccuracy() * (1.0 + selectedAmmo.getAccuracyModifier());
            
            int baseRange = weapon.getRange();
            int maxRange = plugin.getConfig().getInt("settings.max-weapon-range", 150);
            int range = (int) (baseRange * (1.0 + selectedAmmo.getRangeModifier()));
            range = Math.min(range, maxRange); 
            
            applyRecoil(player, weapon, selectedAmmo);
            
            Sound fireSound;
            float volume = 2.0f;
            float pitch;
            
            if (weaponId.equals("xm7")) {
                fireSound = Sound.ENTITY_GENERIC_EXPLODE;
                pitch = 1.8f;
            } else if (weaponId.equals("ak12")) {
                fireSound = Sound.ENTITY_FIREWORK_ROCKET_BLAST;
                pitch = 1.5f;
            } else {
                fireSound = Sound.ENTITY_GENERIC_EXPLODE;
                pitch = 1.9f;
            }
            
            if (selectedAmmo.getId().contains("subsonic")) {
                fireSound = Sound.ENTITY_ARROW_SHOOT;
                volume = 0.5f;
                pitch = 0.6f;
            }
            
            player.getWorld().playSound(player.getLocation(), fireSound, volume, pitch);
            
            performRaycast(player, weapon, damage, accuracy, range, selectedAmmo);
            
            if (selectedAmmo.getId().contains("tracer")) {
                Location eyeLocation = player.getEyeLocation();
                Vector direction = eyeLocation.getDirection();
                
                for (double i = 1; i < Math.min(range, 50); i += 1.0) {
                    Location particleLocation = eyeLocation.clone().add(direction.clone().multiply(i));
                    player.getWorld().spawnParticle(Particle.DUST, particleLocation, 1, 0, 0, 0, 0, new org.bukkit.Particle.DustOptions(org.bukkit.Color.RED, 1.0f));
                }
            }
        }
        else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                event.setCancelled(true);
                
                if (reloadingWeapons.containsKey(playerUUID)) {
                    return;
                }
                
                if (selectedAmmo != null) {
                    Map<String, Integer> playerMagazines = weaponMagazines.computeIfAbsent(playerUUID, k -> new HashMap<>());
                    int currentMagazineAmmo = playerMagazines.getOrDefault(weaponId, 0);
                    
                    if (currentMagazineAmmo >= weapon.getMagazineSize()) {
                        player.sendMessage("§aМагазин полон!");
                        return;
                    }
                    
                    int ammoCount = ammoManager.getAmmoCount(player, selectedAmmo.getId());
                    
                    if (ammoCount <= 0) {
                        player.sendMessage("§cУ вас нет патронов для перезарядки!");
                        return;
                    }
                    
                    double reloadTime = weapon.getReloadTime();
                    
                    double reloadTimeModifier = 0.0;
                    try {
                        reloadTimeModifier = selectedAmmo.getReloadTimeModifier();
                    } catch (Exception e) {
                    }
                    
                    double finalReloadTime = reloadTime * (1.0 + reloadTimeModifier);
                    long reloadTimeMillis = (long) (finalReloadTime * 1000);
                    
                    player.sendMessage("§eПерезарядка... (" + String.format("%.1f", finalReloadTime) + " сек)");
                    reloadingWeapons.put(playerUUID, weaponId);
                    reloadCooldowns.put(playerUUID, System.currentTimeMillis() + reloadTimeMillis);
                    
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (reloadingWeapons.containsKey(playerUUID) && reloadingWeapons.get(playerUUID).equals(weaponId)) {
                            int neededAmmo = weapon.getMagazineSize() - currentMagazineAmmo;
                            int actualAmmo = Math.min(neededAmmo, ammoManager.getAmmoCount(player, selectedAmmo.getId()));
                            
                            ammoManager.useAmmo(player, selectedAmmo.getId(), actualAmmo);
                            
                            playerMagazines.put(weaponId, currentMagazineAmmo + actualAmmo);
                            
                            reloadingWeapons.remove(playerUUID);
                            reloadCooldowns.remove(playerUUID);
                            player.sendMessage("§aПерезарядка завершена! Магазин: " + 
                                playerMagazines.get(weaponId) + "/" + weapon.getMagazineSize());
                            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1.0f, 1.0f);
                        }
                    }, (long) (finalReloadTime * 20)); 
                } else {
                    player.sendMessage("§cУ вас нет патронов для этого оружия!");
                }
            }
        }
    }
    
    /**
     * Получает текущее количество патронов в магазине оружия
     * @param player игрок
     * @param weaponId идентификатор оружия
     * @return количество патронов в магазине
     */
    public int getCurrentMagazineAmmo(Player player, String weaponId) {
        UUID playerUUID = player.getUniqueId();
        Map<String, Integer> playerMagazines = weaponMagazines.get(playerUUID);
        if (playerMagazines == null) {
            return 0;
        }
        return playerMagazines.getOrDefault(weaponId, 0);
    }
    
    /**
     * Устанавливает количество патронов в магазине оружия
     * Используется для инициализации нового оружия
     * @param player игрок
     * @param weaponId идентификатор оружия
     * @param amount количество патронов
     */
    public void setMagazineAmmo(Player player, String weaponId, int amount) {
        UUID playerUUID = player.getUniqueId();
        Map<String, Integer> playerMagazines = weaponMagazines.computeIfAbsent(playerUUID, k -> new HashMap<>());
        playerMagazines.put(weaponId, amount);
    }
    
    private void performRaycast(Player player, Weapon weapon, double damage, double accuracy, int range, AmmoType ammoType) {
        try {
            World world = player.getWorld();
            Location eyeLocation = player.getEyeLocation();
            Vector direction = eyeLocation.getDirection();
            
            if (accuracy < 1.0) {
                double inaccuracy = 0.1 * (1.0 - accuracy);
                direction.add(new Vector(
                    (Math.random() - 0.5) * inaccuracy,
                    (Math.random() - 0.5) * inaccuracy,
                    (Math.random() - 0.5) * inaccuracy
                )).normalize();
            }
            
            RayTraceResult rayTrace = world.rayTrace(
                eyeLocation,
                direction,
                range,
                org.bukkit.FluidCollisionMode.NEVER,
                true,
                0.5,
                entity -> entity instanceof LivingEntity && entity != player
            );
            
            world.playEffect(eyeLocation, Effect.SMOKE, 0);
            
            if (rayTrace != null) {
                if (rayTrace.getHitBlock() != null) {
                    Location hitLocation = rayTrace.getHitPosition().toLocation(world);
                    world.spawnParticle(Particle.BLOCK, hitLocation, 10, 0.1, 0.1, 0.1, 0.1, rayTrace.getHitBlock().getBlockData());
                    world.playSound(hitLocation, Sound.BLOCK_STONE_HIT, 1.0f, 1.0f);
                }
                else if (rayTrace.getHitEntity() != null) {
                    Entity hitEntity = rayTrace.getHitEntity();
                    if (hitEntity instanceof LivingEntity) {
                        LivingEntity target = (LivingEntity) hitEntity;
                        Location hitLocation = target.getLocation();
                        
                        if (target instanceof Player) {
                            lastDamageWeapon.put(target.getUniqueId(), weapon.getId());
                            
                            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                                lastDamageWeapon.remove(target.getUniqueId());
                            }, 20 * 30); 
                        }
                        
                        if (target instanceof Player && !plugin.getConfig().getBoolean("settings.friendly-fire", true)) {
                            player.sendMessage("§7Вы попали в союзника, но friendly-fire отключен!");
                            world.spawnParticle(Particle.BLOCK, hitLocation, 20, 0.1, 0.1, 0.1, 0.1, org.bukkit.Material.REDSTONE_BLOCK.createBlockData());
                            world.playSound(hitLocation, Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
                            return;
                        }
                        
                        double finalDamage = damage;
                        if (ammoType != null && ammoType.getPenetrationModifier() > 0) {
                            finalDamage *= (1.0 + ammoType.getPenetrationModifier());
                        }
                        
                        finalDamage *= plugin.getConfig().getDouble("settings.damage-multiplier", 1.0);
                        
                        boolean isHeadshot = false;
                        if (target instanceof Player) {
                            isHeadshot = ThreadLocalRandom.current().nextDouble() < 0.3;
                        }
                        
                        if (target instanceof Player) {
                            Player targetPlayer = (Player) target;
                            
                            if (armorManager.hasArmor(targetPlayer)) {
                                Armor targetArmor = armorManager.getPlayerArmor(targetPlayer);
                                
                                if (targetArmor != null) {
                                    if (!isHeadshot) {
                                        double protectionLevel = targetArmor.getProtectionLevel();
                                        
                                        if (ammoType != null && ammoType.getId().contains("armor_piercing")) {
                                            double armorPiercingProtection = targetArmor.getArmorPiercingProtection();
                                            protectionLevel = protectionLevel * (1.0 - armorPiercingProtection);
                                            targetPlayer.sendMessage("§c§oБронебойные патроны снизили эффективность вашего бронежилета!");
                                        }
                                        
                                        finalDamage *= (1.0 - protectionLevel);
                                        
                                        targetPlayer.sendMessage("§7Ваш бронежилет " + targetArmor.getDisplayName() + 
                                                              " §7снизил урон на §f" + 
                                                              (int)(protectionLevel * 100) + "%");
                                    }
                                }
                            }
                            
                            if (isHeadshot && helmetManager.hasHelmet(targetPlayer)) {
                                Helmet targetHelmet = helmetManager.getPlayerHelmet(targetPlayer);
                                
                                if (targetHelmet != null) {
                                    double headProtectionLevel = targetHelmet.getHeadProtectionLevel();
                                    
                                    if (ammoType != null && ammoType.getId().contains("armor_piercing")) {
                                        double armorPiercingProtection = targetHelmet.getArmorPiercingProtection();
                                        headProtectionLevel = headProtectionLevel * (1.0 - armorPiercingProtection);
                                        targetPlayer.sendMessage("§c§oБронебойные патроны снизили эффективность вашего шлема!");
                                    }
                                    
                                    double damageReduction = headProtectionLevel;
                                    finalDamage *= (1.0 - damageReduction);
                                    
                                    targetPlayer.sendMessage("§cПопадание в голову! §7Ваш шлем " + targetHelmet.getDisplayName() + 
                                                          " §7снизил урон на §f" + 
                                                          (int)(damageReduction * 100) + "%");
                                } else {
                                    finalDamage *= 1.5;
                                    targetPlayer.sendMessage("§c§lКритическое попадание в голову! §c(+50% урона)");
                                    player.sendMessage("§a§lКритическое попадание в голову!");
                                }
                            } else if (isHeadshot) {
                                finalDamage *= 1.5;
                                targetPlayer.sendMessage("§c§lКритическое попадание в голову! §c(+50% урона)");
                                player.sendMessage("§a§lКритическое попадание в голову!");
                            }
                            
                            calculateAndApplyInjuries(targetPlayer, finalDamage);
                        }
                        
                        target.damage(finalDamage, player);
                        
                        world.spawnParticle(Particle.BLOCK, hitLocation, 20, 0.1, 0.1, 0.1, 0.1, org.bukkit.Material.REDSTONE_BLOCK.createBlockData());
                        world.playSound(hitLocation, Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
                    }
                }
            } else {
                Location endLocation = eyeLocation.clone().add(direction.multiply(range));
                world.spawnParticle(Particle.SMOKE, endLocation, 5, 0.1, 0.1, 0.1, 0.01);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка при выполнении трассировки выстрела: " + e.getMessage());
        }
    }
    
    /**
     * Рассчитывает и применяет травмы к игроку в зависимости от полученного урона
     * @param player игрок
     * @param damage величина урона
     */
    private void calculateAndApplyInjuries(Player player, double damage) {
        double bleedingChanceMultiplier = plugin.getConfig().getDouble("injuries.bleeding-chance-multiplier", 0.1);
        double fractureChanceMultiplier = plugin.getConfig().getDouble("injuries.fracture-chance-multiplier", 0.05);
        
        double bleedingChance = damage * bleedingChanceMultiplier;
        double fractureChance = damage * fractureChanceMultiplier;
        
        bleedingChance = Math.min(bleedingChance, 0.6); 
        fractureChance = Math.min(fractureChance, 0.4); 
        
        if (random.nextDouble() < bleedingChance) {
            injuryManager.applyBleeding(player);
        }
        
        if (random.nextDouble() < fractureChance) {
            injuryManager.applyInjury(player);
        }
    }
    
    /**
     * Применяет эффект отдачи к игроку при стрельбе
     * @param player игрок, который стреляет
     * @param weapon оружие, из которого произведен выстрел
     * @param ammoType тип используемых патронов
     */
    private void applyRecoil(Player player, Weapon weapon, AmmoType ammoType) {
        try {
            double baseRecoil = weapon.getRecoil();
            
            double recoilMultiplier = plugin.getConfig().getDouble("settings.recoil-multiplier", 1.0);
            
            double attachmentRecoilModifier = 0.0;
            
            double ammoRecoilModifier = 0.0;
            if (ammoType != null) {
                ammoRecoilModifier = ammoType.getRecoilModifier();
            }
            
            double finalRecoil = baseRecoil * recoilMultiplier * (1.0 + attachmentRecoilModifier + ammoRecoilModifier);
            
            Vector playerDirection = player.getLocation().getDirection();
            Vector recoilVector = playerDirection.clone().multiply(-finalRecoil * 0.2); 
            
            double upwardRecoil = finalRecoil * 0.05 * Math.random(); 
            double horizontalRecoil = finalRecoil * 0.02 * (Math.random() - 0.5); 
            
            recoilVector.setY(recoilVector.getY() + upwardRecoil);
            recoilVector.setX(recoilVector.getX() + horizontalRecoil);
            
            player.setVelocity(player.getVelocity().add(recoilVector));
            
            float pitch = player.getLocation().getPitch();
            float yaw = player.getLocation().getYaw();
            
            pitch -= finalRecoil * 5.0; 
            pitch = Math.max(-90, pitch); 
            
            yaw += (Math.random() - 0.5) * finalRecoil * 3.0;
            
            Location newLoc = player.getLocation().clone();
            newLoc.setPitch(pitch);
            newLoc.setYaw(yaw);
            player.teleport(newLoc);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка при применении отдачи: " + e.getMessage());
        }
    }
    
    /**
     * Проверяет, находится ли оружие в процессе перезарядки
     * @param playerUUID UUID игрока
     * @param weaponId идентификатор оружия
     * @return true, если оружие перезаряжается
     */
    public boolean isWeaponReloading(UUID playerUUID, String weaponId) {
        return reloadingWeapons.containsKey(playerUUID) && 
               reloadingWeapons.get(playerUUID).equals(weaponId);
    }
    
    /**
     * Проверяет, инициализирован ли магазин оружия
     * @param playerUUID UUID игрока
     * @param weaponId идентификатор оружия
     * @return true, если магазин инициализирован
     */
    public boolean hasWeaponMagazine(UUID playerUUID, String weaponId) {
        Map<String, Integer> playerMagazines = weaponMagazines.get(playerUUID);
        return playerMagazines != null && playerMagazines.containsKey(weaponId);
    }
    
    /**
     * Получает ID последнего оружия, нанесшего урон игроку
     * @param playerUUID UUID игрока
     * @return ID оружия или null, если нет информации
     */
    public String getLastDamageWeapon(UUID playerUUID) {
        return lastDamageWeapon.get(playerUUID);
    }
} 