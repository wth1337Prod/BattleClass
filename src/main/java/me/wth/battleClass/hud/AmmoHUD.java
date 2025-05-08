package me.wth.battleClass.hud;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.weapons.Weapon;
import me.wth.battleClass.weapons.WeaponListener;
import me.wth.battleClass.weapons.ammo.AmmoManager;
import me.wth.battleClass.weapons.ammo.AmmoType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Класс для отображения информации о патронах в HUD игрока
 */
public class AmmoHUD implements Listener {
    private final BattleClass plugin;
    private final AmmoManager ammoManager;
    private final WeaponListener weaponListener;
    private final Map<UUID, BossBar> ammoBars = new HashMap<>();
    private final Map<UUID, BossBar> ammoTypeBars = new HashMap<>();
    private final Map<UUID, BossBar> weaponNameBars = new HashMap<>();
    private final Map<UUID, BukkitTask> hudTasks = new HashMap<>();
    
    private static final BarColor AMMO_BAR_COLOR = BarColor.RED;
    private static final BarStyle AMMO_BAR_STYLE = BarStyle.SEGMENTED_10;
    
    public AmmoHUD(BattleClass plugin, WeaponListener weaponListener, AmmoManager ammoManager) {
        this.plugin = plugin;
        this.ammoManager = ammoManager;
        this.weaponListener = weaponListener;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        startHUDUpdateTask();
    }
    
    private void startHUDUpdateTask() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateAmmoHUD(player);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateAmmoHUD(player);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        hideHUD(player);
    }
    
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        updateAmmoHUD(player);
    }
    
    private void updateAmmoHUD(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        if (itemInHand == null || !itemInHand.hasItemMeta()) {
            hideHUD(player);
            return;
        }
        
        ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null) {
            hideHUD(player);
            return;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "weapon_id");
        
        if (!container.has(key, PersistentDataType.STRING)) {
            hideHUD(player);
            return;
        }
        
        String weaponId = container.get(key, PersistentDataType.STRING);
        Weapon weapon = plugin.getWeaponManager().getWeapon(weaponId);
        
        if (weapon == null) {
            hideHUD(player);
            return;
        }
        
        AmmoType selectedAmmo = ammoManager.getSelectedAmmo(player, weaponId);
        int totalAmmoCount = 0;
        
        if (selectedAmmo != null) {
            totalAmmoCount = ammoManager.getAmmoCount(player, selectedAmmo.getId());
        }
        
        int magazineAmmo = plugin.getListeners().getCurrentMagazineAmmo(player, weaponId);
        
        if (magazineAmmo <= 0 && totalAmmoCount > 0) {
            boolean isReloading = plugin.getListeners().isWeaponReloading(player.getUniqueId(), weaponId);
            if (!isReloading) {
                player.sendActionBar("§cМагазин пуст! Нажмите SHIFT + ЛКМ для перезарядки");
            }
        }
        
        if (magazineAmmo <= 0 && !plugin.getListeners().hasWeaponMagazine(player.getUniqueId(), weaponId)) {
            if (selectedAmmo != null && totalAmmoCount > 0) {
                int maxAmmo = Math.min(weapon.getMagazineSize(), totalAmmoCount);
                plugin.getListeners().setMagazineAmmo(player, weaponId, maxAmmo);
                ammoManager.useAmmo(player, selectedAmmo.getId(), maxAmmo);
                magazineAmmo = maxAmmo;
            }
        }
        
        UUID playerUUID = player.getUniqueId();
        
        BossBar weaponNameBar = weaponNameBars.get(playerUUID);
        if (weaponNameBar == null) {
            weaponNameBar = Bukkit.createBossBar(
                ChatColor.GOLD + weapon.getDisplayName(),
                BarColor.WHITE,
                BarStyle.SOLID
            );
            weaponNameBars.put(playerUUID, weaponNameBar);
        } else {
            weaponNameBar.setTitle(ChatColor.GOLD + weapon.getDisplayName());
        }
        weaponNameBar.addPlayer(player);
        weaponNameBar.setProgress(1.0);
        
        BossBar ammoTypeBar = ammoTypeBars.get(playerUUID);
        String ammoTypeTitle = selectedAmmo != null ? 
            ChatColor.YELLOW + "Патроны: " + ChatColor.WHITE + selectedAmmo.getDisplayName() : 
            ChatColor.RED + "Нет патронов";
            
        if (ammoTypeBar == null) {
            ammoTypeBar = Bukkit.createBossBar(
                ammoTypeTitle,
                selectedAmmo != null ? BarColor.YELLOW : BarColor.RED,
                BarStyle.SOLID
            );
            ammoTypeBars.put(playerUUID, ammoTypeBar);
        } else {
            ammoTypeBar.setTitle(ammoTypeTitle);
            ammoTypeBar.setColor(selectedAmmo != null ? BarColor.YELLOW : BarColor.RED);
        }
        ammoTypeBar.addPlayer(player);
        ammoTypeBar.setProgress(1.0);
        
        BossBar ammoBar = ammoBars.get(playerUUID);
        String ammoText = ChatColor.RED + "Магазин: " + ChatColor.WHITE + magazineAmmo + "/" + weapon.getMagazineSize() + 
                          ChatColor.GRAY + " (Запас: " + totalAmmoCount + ")";
        
        if (ammoBar == null) {
            ammoBar = Bukkit.createBossBar(
                ammoText,
                AMMO_BAR_COLOR,
                AMMO_BAR_STYLE
            );
            ammoBars.put(playerUUID, ammoBar);
        } else {
            ammoBar.setTitle(ammoText);
        }
        
        ammoBar.addPlayer(player);
        
        double progress = (double) magazineAmmo / weapon.getMagazineSize();
        ammoBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        
        if (progress < 0.3) {
            ammoBar.setColor(BarColor.RED);
        } else if (progress < 0.7) {
            ammoBar.setColor(BarColor.YELLOW);
        } else {
            ammoBar.setColor(BarColor.GREEN);
        }
    }
    
    private void hideHUD(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        BossBar ammoBar = ammoBars.get(playerUUID);
        if (ammoBar != null) {
            ammoBar.removePlayer(player);
        }
        
        BossBar ammoTypeBar = ammoTypeBars.get(playerUUID);
        if (ammoTypeBar != null) {
            ammoTypeBar.removePlayer(player);
        }
        
        BossBar weaponNameBar = weaponNameBars.get(playerUUID);
        if (weaponNameBar != null) {
            weaponNameBar.removePlayer(player);
        }
    }
    
    public void cleanup() {
        for (BossBar bar : ammoBars.values()) {
            bar.removeAll();
        }
        
        for (BossBar bar : ammoTypeBars.values()) {
            bar.removeAll();
        }
        
        for (BossBar bar : weaponNameBars.values()) {
            bar.removeAll();
        }
        
        ammoBars.clear();
        ammoTypeBars.clear();
        weaponNameBars.clear();
    }
} 