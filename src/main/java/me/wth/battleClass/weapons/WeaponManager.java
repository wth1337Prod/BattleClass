package me.wth.battleClass.weapons;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.weapons.pistols.SigSauerM17;
import me.wth.battleClass.weapons.pistols.Udav;
import me.wth.battleClass.weapons.rifles.AK12;
import me.wth.battleClass.weapons.rifles.XM7;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponManager {
    private final BattleClass plugin;
    private final Map<String, Weapon> weapons = new HashMap<>();
    
    public WeaponManager(BattleClass plugin) {
        this.plugin = plugin;
        registerWeapons();
    }
    
    private void registerWeapons() {
        registerWeapon(new XM7(plugin));
        registerWeapon(new AK12(plugin));
        
        registerWeapon(new Udav(plugin));
        registerWeapon(new SigSauerM17(plugin));
    }
    
    private void registerWeapon(Weapon weapon) {
        weapons.put(weapon.getId(), weapon);
    }
    
    public void giveWeaponToPlayer(Player player, String weaponId) {
        Weapon weapon = weapons.get(weaponId);
        
        if (weapon != null) {
            ItemStack weaponItem = weapon.createItemStack();
            player.getInventory().addItem(weaponItem);
            player.sendMessage("§aВы получили " + weapon.getDisplayName());
        } else {
            player.sendMessage("§cОружие " + weaponId + " не найдено!");
        }
    }
    
    public Weapon getWeapon(String weaponId) {
        return weapons.get(weaponId);
    }
    
    public Map<String, Weapon> getWeapons() {
        return weapons;
    }
    
    /**
     * Получает список идентификаторов всех зарегистрированных оружий
     * 
     * @return список идентификаторов оружий
     */
    public List<String> getWeaponIds() {
        return new ArrayList<>(weapons.keySet());
    }
    
    /**
     * Получает список всех аксессуаров для всех оружий
     * 
     * @return список всех доступных аксессуаров
     */
    public List<WeaponAttachment> getAttachments() {
        List<WeaponAttachment> allAttachments = new ArrayList<>();
        
        for (Weapon weapon : weapons.values()) {
            for (WeaponAttachment attachment : weapon.getAvailableAttachments()) {
                if (!allAttachments.contains(attachment)) {
                    allAttachments.add(attachment);
                }
            }
        }
        
        return allAttachments;
    }
    
    /**
     * Получает список идентификаторов всех доступных аксессуаров для оружия
     * 
     * @return список идентификаторов аксессуаров
     */
    public List<String> getAttachmentIds() {
        List<String> attachmentIds = new ArrayList<>();
        
        for (Weapon weapon : weapons.values()) {
            for (WeaponAttachment attachment : weapon.getAvailableAttachments()) {
                if (!attachmentIds.contains(attachment.getId())) {
                    attachmentIds.add(attachment.getId());
                }
            }
        }
        
        return attachmentIds;
    }
} 