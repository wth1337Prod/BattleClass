package me.wth.battleClass.medical;

import me.wth.battleClass.BattleClass;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Менеджер для управления травмами и их эффектами
 */
public class InjuryManager {
    private final BattleClass plugin;
    
    private final Set<UUID> playersWithInjuries = new HashSet<>();
    
    private final Map<UUID, Long> playersWithBleeding = new HashMap<>();
    
    private final long bleedingDamageInterval = 3000; 
    
    private final double bleedingDamage = 1.0;
    
    /**
     * Конструктор менеджера травм
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public InjuryManager(BattleClass plugin) {
        this.plugin = plugin;
        startBleedingDamageTask();
    }
    
    /**
     * Запускает задачу для нанесения периодического урона от кровотечения
     */
    private void startBleedingDamageTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            
            for (Map.Entry<UUID, Long> entry : new HashMap<>(playersWithBleeding).entrySet()) {
                UUID playerId = entry.getKey();
                Long lastDamageTime = entry.getValue();
                
                if (currentTime - lastDamageTime >= bleedingDamageInterval) {
                    Player player = plugin.getServer().getPlayer(playerId);
                    
                    if (player != null && player.isOnline() && !player.isDead() && player.getHealth() > 0) {
                        player.damage(bleedingDamage);
                        
                        player.sendMessage("§c§oВы теряете кровь... (§f-" + bleedingDamage + " HP§c§o)");
                        
                        playersWithBleeding.put(playerId, currentTime);
                    } else {
                        playersWithBleeding.remove(playerId);
                        
                        if (player != null && player.isOnline()) {
                            plugin.getLogger().info("Удалено кровотечение у игрока " + player.getName() + 
                                    " (isDead: " + player.isDead() + ", health: " + player.getHealth() + ")");
                        }
                    }
                }
            }
        }, 20L, 20L); 
    }
    
    /**
     * Применяет перелом к игроку
     * 
     * @param player игрок, к которому применяется эффект
     */
    public void applyInjury(Player player) {
        if (player == null || player.isDead()) {
            return;
        }
        
        UUID playerId = player.getUniqueId();
        
        if (!playersWithInjuries.contains(playerId)) {
            playersWithInjuries.add(playerId);
            
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOWNESS,
                Integer.MAX_VALUE, 
                1, 
                false, 
                true, 
                true 
            ));
            
            player.sendMessage("§c§oВы получили перелом! Ваша скорость передвижения снижена.");
        }
    }
    
    /**
     * Лечит перелом у игрока
     * 
     * @param player игрок, у которого лечится перелом
     */
    public void healInjury(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (playersWithInjuries.contains(playerId)) {
            playersWithInjuries.remove(playerId);
            
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            
            player.sendMessage("§a§oВаш перелом вылечен! Скорость передвижения восстановлена.");
        }
    }
    
    /**
     * Проверяет, есть ли у игрока перелом
     * 
     * @param player игрок для проверки
     * @return true, если у игрока есть перелом
     */
    public boolean hasInjury(Player player) {
        return playersWithInjuries.contains(player.getUniqueId());
    }
    
    /**
     * Применяет кровотечение к игроку
     * 
     * @param player игрок, к которому применяется эффект
     */
    public void applyBleeding(Player player) {
        if (player == null || player.isDead()) {
            return;
        }
        
        UUID playerId = player.getUniqueId();
        
        if (!playersWithBleeding.containsKey(playerId)) {
            playersWithBleeding.put(playerId, System.currentTimeMillis());
            
            player.sendMessage("§c§oУ вас началось кровотечение! Используйте бинты или аптечку, чтобы остановить его.");
        }
    }
    
    /**
     * Останавливает кровотечение у игрока
     * 
     * @param player игрок, у которого останавливается кровотечение
     */
    public void stopBleeding(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (playersWithBleeding.containsKey(playerId)) {
            playersWithBleeding.remove(playerId);
        }
    }
    
    /**
     * Проверяет, есть ли у игрока кровотечение
     * 
     * @param player игрок для проверки
     * @return true, если у игрока есть кровотечение
     */
    public boolean isBleeding(Player player) {
        return playersWithBleeding.containsKey(player.getUniqueId());
    }
    
    /**
     * Очищает все травмы у игрока (переломы и кровотечения)
     * Используется при смерти и возрождении игрока
     * 
     * @param player игрок, у которого нужно очистить все травмы
     */
    public void clearAllInjuries(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (playersWithInjuries.contains(playerId)) {
            playersWithInjuries.remove(playerId);
            player.removePotionEffect(PotionEffectType.SLOWNESS);
        }
        
        if (playersWithBleeding.containsKey(playerId)) {
            playersWithBleeding.remove(playerId);
        }
    }
    
    /**
     * Возвращает количество активных травм у игрока
     * 
     * @param player игрок для проверки
     * @return количество активных травм
     */
    public int getActiveInjuryCount(Player player) {
        int count = 0;
        UUID playerId = player.getUniqueId();
        
        if (playersWithInjuries.contains(playerId)) {
            count++;
        }
        
        if (playersWithBleeding.containsKey(playerId)) {
            count++;
        }
        
        if (player.hasPotionEffect(PotionEffectType.NAUSEA)) {
            count++;
        }
        
        return count;
    }
} 