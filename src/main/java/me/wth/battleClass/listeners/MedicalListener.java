package me.wth.battleClass.listeners;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.medical.MedicalItem;
import me.wth.battleClass.medical.MedicalManager;
import me.wth.battleClass.medical.InjuryManager;
import me.wth.battleClass.medical.items.Adrenaline;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 * Обработчик событий для медицинских предметов
 */
public class MedicalListener implements Listener {
    private final BattleClass plugin;
    private final MedicalManager medicalManager;
    private final InjuryManager injuryManager;
    private final Random random = new Random();
    
    /**
     * Конструктор слушателя медицинских предметов
     * 
     * @param plugin экземпляр главного класса плагина
     * @param medicalManager менеджер медицинских предметов
     * @param injuryManager менеджер травм
     */
    public MedicalListener(BattleClass plugin, MedicalManager medicalManager, InjuryManager injuryManager) {
        this.plugin = plugin;
        this.medicalManager = medicalManager;
        this.injuryManager = injuryManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Обработчик получения урона игроком
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        double damage = event.getFinalDamage();
        
        if (damage >= 3.0) {
            double injuryChance = damage * 0.05; 
            if (random.nextDouble() < injuryChance) {
                injuryManager.applyInjury(player);
            }
        }
        
        if (damage >= 2.0) {
            double bleedingChance = damage * 0.1; 
            if (random.nextDouble() < bleedingChance) {
                injuryManager.applyBleeding(player);
            }
        }
    }
    
    /**
     * Обработчик использования предметов
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || 
            (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        
        if (!medicalManager.isMedicalItem(item)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (medicalManager.isPlayerUsingMedicalItem(player, null)) {
            player.sendMessage("§cВы уже используете медицинский предмет!");
            return;
        }
        
        String medicalId = medicalManager.getMedicalItemId(item);
        if (medicalId == null) {
            return;
        }
        
        MedicalItem medicalItem = medicalManager.getMedicalItem(medicalId);
        if (medicalItem == null) {
            return;
        }
        
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double currentHealth = player.getHealth();
        boolean needsHealing = currentHealth < maxHealth;
        
        boolean needsBleedingCure = injuryManager.isBleeding(player) && medicalItem.stopsBleeding();
        
        boolean needsInjuryCure = injuryManager.hasInjury(player) && medicalItem.healsInjuries();
        
        boolean canUse = needsHealing || needsBleedingCure || needsInjuryCure || (medicalItem instanceof Adrenaline);
        
        if (!canUse) {
            player.sendMessage("§cВам не требуется лечение данным предметом!");
            return;
        }
        
        double useTime = medicalItem.getUseTime();
        int useTimeTicks = (int) (useTime * 20); 
        
        player.sendMessage("§eИспользование " + medicalItem.getDisplayName() + "... §f(" + useTime + " сек)");
        
        player.playSound(player.getLocation(), Sound.BLOCK_WOOL_PLACE, 1.0f, 1.0f);
        
        int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }
            
            if (!medicalManager.isPlayerUsingMedicalItem(player, medicalId)) {
                return;
            }
            
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().removeItem(item);
            }
            
            
            if (needsHealing) {
                double healAmount = medicalItem.getHealAmount();
                double newHealth = Math.min(currentHealth + healAmount, maxHealth);
                player.setHealth(newHealth);
                player.sendMessage("§aВы восстановили §f" + healAmount + " HP§a здоровья!");
            }
            
            if (needsBleedingCure) {
                injuryManager.stopBleeding(player);
                player.sendMessage("§aКровотечение остановлено!");
            }
            
            if (needsInjuryCure) {
                injuryManager.healInjury(player);
                player.sendMessage("§aПерелом вылечен!");
            }
            
            if (medicalItem instanceof Adrenaline) {
                Adrenaline adrenaline = (Adrenaline) medicalItem;
                int duration = adrenaline.getEffectDuration() * 20; 
                
                if (adrenaline.getSpeedLevel() > 0) {
                    player.addPotionEffect(new PotionEffect(
                        PotionEffectType.SPEED,
                        duration,
                        adrenaline.getSpeedLevel() - 1, 
                        false,
                        true,
                        true
                    ));
                }
                
                if (adrenaline.getStrengthLevel() > 0) {
                    player.addPotionEffect(new PotionEffect(
                        PotionEffectType.INSTANT_DAMAGE,
                        duration,
                        adrenaline.getStrengthLevel() - 1, 
                        false,
                        true,
                        true
                    ));
                }
                
                player.sendMessage("§aАдреналин активирован! Вы чувствуете прилив сил.");
            }
            
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            
            medicalManager.removePlayerUsingMedicalItem(player, medicalId);
        }, useTimeTicks);
        
        medicalManager.setPlayerUsingMedicalItem(player, medicalId, taskId);
    }
    
    /**
     * Обработчик смены предмета в руке
     */
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        if (medicalManager.isPlayerUsingMedicalItem(player, null)) {
            for (String medicalId : medicalManager.getMedicalItems().keySet()) {
                if (medicalManager.isPlayerUsingMedicalItem(player, medicalId)) {
                    int taskId = medicalManager.getTaskId(player, medicalId);
                    if (taskId != -1) {
                        plugin.getServer().getScheduler().cancelTask(taskId);
                    }
                    
                    medicalManager.removePlayerUsingMedicalItem(player, medicalId);
                    
                    player.sendMessage("§cИспользование медицинского предмета прервано!");
                    
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                }
            }
        }
    }
    
    /**
     * Обработчик движения игрока
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        
        if (event.getFrom().getX() == event.getTo().getX() &&
            event.getFrom().getY() == event.getTo().getY() &&
            event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        if (medicalManager.isPlayerUsingMedicalItem(player, null)) {
            for (String medicalId : medicalManager.getMedicalItems().keySet()) {
                if (medicalManager.isPlayerUsingMedicalItem(player, medicalId)) {
                    int taskId = medicalManager.getTaskId(player, medicalId);
                    if (taskId != -1) {
                        plugin.getServer().getScheduler().cancelTask(taskId);
                    }
                    
                    medicalManager.removePlayerUsingMedicalItem(player, medicalId);
                    
                    player.sendMessage("§cИспользование медицинского предмета прервано!");
                    
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                }
            }
        }
    }
    
    /**
     * Обработчик выхода игрока
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (medicalManager.isPlayerUsingMedicalItem(player, null)) {
            for (String medicalId : medicalManager.getMedicalItems().keySet()) {
                if (medicalManager.isPlayerUsingMedicalItem(player, medicalId)) {
                    int taskId = medicalManager.getTaskId(player, medicalId);
                    if (taskId != -1) {
                        plugin.getServer().getScheduler().cancelTask(taskId);
                    }
                    
                    medicalManager.removePlayerUsingMedicalItem(player, medicalId);
                }
            }
        }
    }
} 