package me.wth.battleClass.mortars;

import me.wth.battleClass.BattleClass;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Обработчик событий для системы минометов
 */
public class MortarListener implements Listener {
    private final BattleClass plugin;
    private final MortarManager mortarManager;
    
    private final Map<Location, Mortar> deployedMortars = new HashMap<>();
    
    private final Map<UUID, Location> configuringPlayers = new HashMap<>();
    
    private final Map<Location, ArmorStand> mortarModels = new HashMap<>();
    
    /**
     * Конструктор слушателя минометов
     * 
     * @param plugin экземпляр основного плагина
     * @param mortarManager менеджер минометов
     */
    public MortarListener(BattleClass plugin, MortarManager mortarManager) {
        this.plugin = plugin;
        this.mortarManager = mortarManager;
    }
    
    /**
     * Получает экземпляр плагина
     * 
     * @return экземпляр плагина
     */
    public BattleClass getPlugin() {
        return plugin;
    }
    
    /**
     * Обрабатывает нажатия правой кнопкой мыши для установки и использования минометов
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        Mortar mortar = mortarManager.getMortarFromItemStack(itemInHand);
        
        if (mortar != null) {
            event.setCancelled(true);
            
            if (!mortarManager.canPlayerUseMortar(player, mortar)) {
                player.sendMessage(ChatColor.RED + "Вы не можете использовать миномет другой фракции!");
                return;
            }
            
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) {
                Block clickedBlock = event.getClickedBlock();
                if (clickedBlock != null) {
                    Location mortarLocation = clickedBlock.getLocation().add(0, 1, 0);
                    Block mortarBlock = mortarLocation.getBlock();
                    
                    if (mortarBlock.getType() == Material.AIR) {
                        createVisualMortar(mortarLocation, mortar, player.getLocation().getYaw());
                        
                        deployedMortars.put(mortarLocation, mortar);
                        
                        mortarBlock.setMetadata("mortar", new FixedMetadataValue(plugin, mortar.getId()));
                        
                        player.getWorld().playSound(mortarLocation, Sound.BLOCK_METAL_PLACE, 1.0f, 0.8f);
                        
                        player.sendMessage(ChatColor.GREEN + "Миномет " + mortar.getDisplayName() + 
                                           ChatColor.GREEN + " успешно установлен.");
                        
                        if (itemInHand.getAmount() > 1) {
                            itemInHand.setAmount(itemInHand.getAmount() - 1);
                        } else {
                            player.getInventory().setItemInMainHand(null);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Недостаточно места для установки миномета!");
                    }
                }
            } 
            else if (event.getAction() == Action.RIGHT_CLICK_AIR && player.isSneaking()) {
                player.sendMessage(ChatColor.YELLOW + "Для настройки миномета сначала установите его на землю.");
            }
        }
        
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            
            if (clickedBlock != null && clickedBlock.hasMetadata("mortar")) {
                Location mortarLocation = clickedBlock.getLocation();
                
                if (deployedMortars.containsKey(mortarLocation)) {
                    Mortar deployedMortar = deployedMortars.get(mortarLocation);
                    
                    if (!mortarManager.canPlayerUseMortar(player, deployedMortar)) {
                        player.sendMessage(ChatColor.RED + "Вы не можете использовать миномет другой фракции!");
                        return;
                    }
                    
                    if (player.isSneaking()) {
                        configuringPlayers.put(player.getUniqueId(), mortarLocation);
                        
                        player.sendMessage(ChatColor.GREEN + "Настройка миномета " + 
                                           deployedMortar.getDisplayName() + ChatColor.GREEN + ":");
                        player.sendMessage(ChatColor.YELLOW + "Введите в чат: <угол> <сила>");
                        player.sendMessage(ChatColor.GRAY + "Например: 45 75");
                        player.sendMessage(ChatColor.GRAY + "Угол от 30° до 85°, сила от 1% до 100%");
                    }
                    else {
                        adjustMortarAngle(mortarLocation, 45);
                        
                        deployedMortar.fire(player, 45, 75);
                    }
                    
                    event.setCancelled(true);
                }
            }
        }
    }
    
    /**
     * Создает визуальное представление миномета
     * 
     * @param location местоположение миномета
     * @param mortar объект миномета
     * @param yaw направление установки 
     */
    private void createVisualMortar(Location location, Mortar mortar, float yaw) {
        String mortarType = null;
        if (mortar instanceof Mortar2B14Podnos) {
            mortarType = "2b14";
        } else if (mortar instanceof MortarM224) {
            mortarType = "m224";
        }
        
        Block baseBlock = location.getBlock();
        baseBlock.setType(mortarType != null && mortarType.equals("2b14") ? Material.ANVIL : Material.IRON_BLOCK);
        
        location.getBlock().getRelative(BlockFace.UP).setType(Material.GRAY_BANNER);
        
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(
            location.clone().add(0.5, 0, 0.5), 
            EntityType.ARMOR_STAND
        );
        
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setCustomName(mortar.getDisplayName());
        stand.setCustomNameVisible(true);
        
        Material headMaterial = mortarType != null && mortarType.equals("2b14") 
            ? Material.OBSERVER 
            : Material.HOPPER;
        
        ItemStack headItem = new ItemStack(headMaterial);
        stand.getEquipment().setHelmet(headItem);
        
        adjustMortarAngle(location, 30);
        
        mortarModels.put(location, stand);
    }
    
    /**
     * Регулирует угол наклона миномета визуально 
     * 
     * @param location местоположение миномета
     * @param angle угол наклона в градусах
     */
    private void adjustMortarAngle(Location location, double angle) {
        ArmorStand stand = mortarModels.get(location);
        if (stand != null) {
            double radians = Math.toRadians(angle);
            
            stand.setHeadPose(new EulerAngle(Math.PI/2 - radians, 0, 0));
        }
    }
    
    /**
     * Обрабатывает попадание снаряда миномета
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().hasMetadata("mortar_projectile")) {
            Location hitLocation = event.getEntity().getLocation();
            
            String mortarId = event.getEntity().getMetadata("mortar_projectile").get(0).asString();
            Mortar mortar = mortarManager.getMortarById(mortarId);
            
            if (mortar != null) {
                hitLocation.getWorld().createExplosion(
                    hitLocation,
                    (float)mortar.getExplosionRadius(),
                    false, 
                    true   
                );
                
                event.getEntity().remove();
            }
        }
    }
    
    /**
     * Проверяет, находится ли игрок в режиме настройки миномета
     * 
     * @param player игрок для проверки
     * @return true, если игрок настраивает миномет
     */
    public boolean isConfiguringMortar(Player player) {
        return configuringPlayers.containsKey(player.getUniqueId());
    }
    
    /**
     * Обрабатывает ввод параметров для стрельбы из миномета
     * 
     * @param player игрок, вводящий параметры
     * @param message сообщение с параметрами
     * @return true, если сообщение было обработано как команда для миномета
     */
    public boolean handleMortarConfiguration(Player player, String message) {
        if (!isConfiguringMortar(player)) {
            return false;
        }
        
        Location mortarLocation = configuringPlayers.get(player.getUniqueId());
        Mortar mortar = deployedMortars.get(mortarLocation);
        
        configuringPlayers.remove(player.getUniqueId());
        
        String[] params = message.split(" ");
        if (params.length != 2) {
            player.sendMessage(ChatColor.RED + "Неверный формат. Используйте: <угол> <сила>");
            return true;
        }
        
        try {
            double angle = Double.parseDouble(params[0]);
            int power = Integer.parseInt(params[1]);
            
            if (angle < 30 || angle > 85) {
                player.sendMessage(ChatColor.RED + "Угол должен быть от 30° до 85°.");
                return true;
            }
            
            if (power < 1 || power > 100) {
                player.sendMessage(ChatColor.RED + "Сила должна быть от 1% до 100%.");
                return true;
            }
            
            adjustMortarAngle(mortarLocation, angle);
            
            mortar.fire(player, angle, power);
            
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Неверный формат чисел. Используйте: <угол> <сила>");
        }
        
        return true;
    }
    
    /**
     * Удаляет миномет с указанного местоположения
     * 
     * @param location местоположение миномета
     * @return true, если миномет был успешно удален
     */
    public boolean removeMortar(Location location) {
        if (deployedMortars.containsKey(location)) {
            location.getBlock().setType(Material.AIR);
            location.getBlock().removeMetadata("mortar", plugin);
            
            ArmorStand stand = mortarModels.get(location);
            if (stand != null) {
                stand.remove();
                mortarModels.remove(location);
            }
            
            deployedMortars.remove(location);
            
            return true;
        }
        
        return false;
    }
} 