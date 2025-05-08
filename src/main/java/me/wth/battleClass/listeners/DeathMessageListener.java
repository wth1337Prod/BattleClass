package me.wth.battleClass.listeners;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.weapons.Weapon;
import me.wth.battleClass.weapons.WeaponListener;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Класс для обработки сообщений о смерти от оружия
 */
public class DeathMessageListener implements Listener {
    private final BattleClass plugin;
    private final WeaponListener weaponListener;

    public DeathMessageListener(BattleClass plugin, WeaponListener weaponListener) {
        this.plugin = plugin;
        this.weaponListener = weaponListener;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Обработчик события смерти игрока
     * Регистрируем с MONITOR приоритетом, чтобы перехватить событие после всех остальных обработчиков
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathEarly(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }
    
    /**
     * Обработчик события смерти игрока для установки кастомного сообщения
     * Регистрируем с MONITOR приоритетом, чтобы перехватить событие после всех остальных обработчиков
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        
        EntityDamageEvent lastDamage = victim.getLastDamageCause();
        if (lastDamage == null) {
            sendDeathMessage(ChatColor.RED + victim.getName() + ChatColor.GRAY + " умер");
            return;
        }
        
        if (lastDamage instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) lastDamage;
            Entity damager = damageByEntityEvent.getDamager();
            
            if (damager instanceof Player) {
                Player killer = (Player) damager;
                
                String weaponId = getWeaponIdFromItem(killer.getInventory().getItemInMainHand());
                if (weaponId != null) {
                    sendWeaponDeathMessage(victim, killer, weaponId);
                    return;
                }
                
                weaponId = weaponListener.getLastDamageWeapon(victim.getUniqueId());
                if (weaponId != null) {
                    sendWeaponDeathMessage(victim, killer, weaponId);
                    return;
                }
                
                sendDeathMessage(ChatColor.RED + victim.getName() + ChatColor.GRAY + " был убит " + 
                                 ChatColor.RED + killer.getName());
                return;
            }
            
            String defaultMessage = ChatColor.RED + victim.getName() + ChatColor.GRAY + 
                                    " был убит " + ChatColor.WHITE + damager.getName();
            sendDeathMessage(defaultMessage);
        } else {
            String causeMessage = getCauseOfDeathMessage(victim, lastDamage.getCause());
            sendDeathMessage(causeMessage);
        }
    }
    
    /**
     * Получает ID оружия из предмета
     */
    private String getWeaponIdFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "weapon_id");
        
        if (!container.has(key, PersistentDataType.STRING)) {
            return null;
        }
        
        return container.get(key, PersistentDataType.STRING);
    }
    
    /**
     * Отправляет сообщение о смерти от оружия
     */
    private void sendWeaponDeathMessage(Player victim, Player killer, String weaponId) {
        Weapon weapon = plugin.getWeaponManager().getWeapon(weaponId);
        if (weapon == null) {
            sendDeathMessage(ChatColor.RED + victim.getName() + ChatColor.GRAY + " был убит " + 
                            ChatColor.RED + killer.getName());
            return;
        }
        
        String weaponName = weapon.getDisplayName();
        String deathMessage = ChatColor.RED + victim.getName() + ChatColor.GRAY + " был застрелен " + 
                             ChatColor.RED + killer.getName() + ChatColor.GRAY + " из " + 
                             ChatColor.WHITE + weaponName;
        
        sendDeathMessage(deathMessage);
    }
    
    /**
     * Отправляет сообщение о смерти всем игрокам и в консоль
     */
    private void sendDeathMessage(String message) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
        
        plugin.getServer().getConsoleSender().sendMessage(message);
    }
    
    /**
     * Получает сообщение о причине смерти на основе стандартного типа причины
     */
    private String getCauseOfDeathMessage(Player victim, EntityDamageEvent.DamageCause cause) {
        String baseMessage = ChatColor.RED + victim.getName() + ChatColor.GRAY;
        
        switch (cause) {
            case FALL:
                return baseMessage + " разбился насмерть";
            case FIRE:
            case FIRE_TICK:
                return baseMessage + " сгорел";
            case LAVA:
                return baseMessage + " решил искупаться в лаве";
            case DROWNING:
                return baseMessage + " утонул";
            case ENTITY_EXPLOSION:
            case BLOCK_EXPLOSION:
                return baseMessage + " был взорван";
            case VOID:
                return baseMessage + " выпал из мира";
            case POISON:
            case MAGIC:
                return baseMessage + " погиб от магии";
            case WITHER:
                return baseMessage + " иссох";
            case FALLING_BLOCK:
                return baseMessage + " был раздавлен";
            case THORNS:
                return baseMessage + " укололся до смерти";
            case DRAGON_BREATH:
                return baseMessage + " был испепелен драконьим дыханием";
            case STARVATION:
                return baseMessage + " умер от голода";
            case LIGHTNING:
                return baseMessage + " был поражен молнией";
            default:
                return baseMessage + " умер";
        }
    }
} 