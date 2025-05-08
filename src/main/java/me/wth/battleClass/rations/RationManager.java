package me.wth.battleClass.rations;

import me.wth.battleClass.BattleClass;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RationManager {
    private final BattleClass plugin;
    private final Map<String, Ration> rations;
    private final Map<UUID, RationUseTask> usingRations;
    
    public RationManager(BattleClass plugin) {
        this.plugin = plugin;
        this.rations = new HashMap<>();
        this.usingRations = new HashMap<>();
        
        registerRations();
    }
    
    private void registerRations() {
        MRERation mreRation = new MRERation();
        rations.put(mreRation.getId(), mreRation);
        
        IRPRation irpRation = new IRPRation();
        rations.put(irpRation.getId(), irpRation);
    }
    
    public Map<String, Ration> getRations() {
        return rations;
    }
    
    public Ration getRation(String id) {
        return rations.get(id);
    }
    
    public void giveRationToPlayer(Player player, String rationId, int amount) {
        Ration ration = rations.get(rationId);
        
        if (ration == null) {
            player.sendMessage("§cСухой паек " + rationId + " не найден!");
            return;
        }
        
        if (amount < 1) amount = 1;
        if (amount > 64) amount = 64;
        
        ItemStack rationItem = ration.createItemStack();
        rationItem.setAmount(amount);
        
        player.getInventory().addItem(rationItem);
        player.sendMessage("§aВы получили §f" + ration.getDisplayName() + " §ax" + amount);
    }
    
    public boolean isRation(ItemStack item) {
        if (item == null) {
            return false;
        }
        
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        
        for (Ration ration : rations.values()) {
            if (displayName.equals(ration.getDisplayName())) {
                return true;
            }
        }
        
        return false;
    }
    
    public Ration getRationFromItem(ItemStack item) {
        if (!isRation(item)) {
            return null;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        
        for (Ration ration : rations.values()) {
            if (displayName.equals(ration.getDisplayName())) {
                return ration;
            }
        }
        
        return null;
    }
    
    public void useRation(Player player, Ration ration) {
        if (usingRations.containsKey(player.getUniqueId())) {
            player.sendMessage("§cВы уже употребляете сухой паек!");
            return;
        }
        
        RationUseTask task = new RationUseTask(player, ration);
        task.runTaskTimer(plugin, 0L, 4L); 
        
        usingRations.put(player.getUniqueId(), task);
        
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getAmount() > 1) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
        
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.7f, 1.0f);
    }
    
    public void cancelUse(Player player) {
        RationUseTask task = usingRations.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
            player.sendMessage("§cУпотребление сухого пайка отменено");
        }
    }
    
    private class RationUseTask extends BukkitRunnable {
        private final Player player;
        private final Ration ration;
        private int progress = 0;
        private final int maxProgress;
        
        public RationUseTask(Player player, Ration ration) {
            this.player = player;
            this.ration = ration;
            this.maxProgress = ration.getUseDuration();
        }
        
        @Override
        public void run() {
            progress += 4; 
            
            if (!player.isOnline() || player.isDead()) {
                cancel();
                usingRations.remove(player.getUniqueId());
                return;
            }
            
            if (progress % 8 == 0) { 
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.6f, 1.0f);
            }
            
            if (progress >= maxProgress) {
                completeUse();
                cancel();
                usingRations.remove(player.getUniqueId());
                return;
            }
        }
        
        private void completeUse() {
            int newFoodLevel = Math.min(player.getFoodLevel() + ration.getFoodValue(), 20);
            player.setFoodLevel(newFoodLevel);
            
            double newHealth = Math.min(player.getHealth() + ration.getHealthValue(), player.getMaxHealth());
            player.setHealth(newHealth);
            
            for (PotionEffect effect : ration.getEffects()) {
                player.addPotionEffect(effect);
            }
            
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 0.8f, 1.0f);
            
            player.sendMessage("§aВы употребили " + ration.getDisplayName() + "§a и восстановили силы!");
        }
    }
    
    /**
     * Получает список идентификаторов всех зарегистрированных сухих пайков
     * 
     * @return список идентификаторов сухих пайков
     */
    public List<String> getRationIds() {
        return new ArrayList<>(rations.keySet());
    }
} 