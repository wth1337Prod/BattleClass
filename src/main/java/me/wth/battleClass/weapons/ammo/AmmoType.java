package me.wth.battleClass.weapons.ammo;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AmmoType {
    private final String id;
    private final String displayName;
    private final List<String> description;
    private final Material material;
    private final double damageModifier;
    private final double penetrationModifier;
    private final double accuracyModifier;
    private final double rangeModifier;
    private final double reloadTimeModifier;
    private final double recoilModifier;
    private final String weaponId;
    
    public AmmoType(String id, String displayName, List<String> description, Material material, 
                    String weaponId, double damageModifier, double penetrationModifier, 
                    double accuracyModifier, double rangeModifier) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.weaponId = weaponId;
        this.damageModifier = damageModifier;
        this.penetrationModifier = penetrationModifier;
        this.accuracyModifier = accuracyModifier;
        this.rangeModifier = rangeModifier;
        this.reloadTimeModifier = 0.0; 
        this.recoilModifier = 0.0;     
    }
    
    public AmmoType(String id, String displayName, List<String> description, Material material, 
                    String weaponId, double damageModifier, double penetrationModifier, 
                    double accuracyModifier, double rangeModifier, double reloadTimeModifier) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.weaponId = weaponId;
        this.damageModifier = damageModifier;
        this.penetrationModifier = penetrationModifier;
        this.accuracyModifier = accuracyModifier;
        this.rangeModifier = rangeModifier;
        this.reloadTimeModifier = reloadTimeModifier;
        this.recoilModifier = 0.0;     
    }
    
    public AmmoType(String id, String displayName, List<String> description, Material material, 
                    String weaponId, double damageModifier, double penetrationModifier, 
                    double accuracyModifier, double rangeModifier, double reloadTimeModifier,
                    double recoilModifier) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.weaponId = weaponId;
        this.damageModifier = damageModifier;
        this.penetrationModifier = penetrationModifier;
        this.accuracyModifier = accuracyModifier;
        this.rangeModifier = rangeModifier;
        this.reloadTimeModifier = reloadTimeModifier;
        this.recoilModifier = recoilModifier;
    }
    
    public ItemStack createItemStack(int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6" + displayName);
            
            List<String> lore = new ArrayList<>(description);
            lore.add("");
            lore.add("§7Модификатор урона: §f" + (damageModifier > 0 ? "+" : "") + (int)(damageModifier * 100) + "%");
            lore.add("§7Пробивная способность: §f" + (penetrationModifier > 0 ? "+" : "") + (int)(penetrationModifier * 100) + "%");
            lore.add("§7Модификатор точности: §f" + (accuracyModifier > 0 ? "+" : "") + (int)(accuracyModifier * 100) + "%");
            lore.add("§7Модификатор дальности: §f" + (rangeModifier > 0 ? "+" : "") + (int)(rangeModifier * 100) + "%");
            
            if (reloadTimeModifier != 0.0) {
                lore.add("§7Время перезарядки: §f" + (reloadTimeModifier > 0 ? "+" : "") + (int)(reloadTimeModifier * 100) + "%");
            }
            
            if (recoilModifier != 0.0) {
                lore.add("§7Модификатор отдачи: §f" + (recoilModifier > 0 ? "+" : "") + (int)(recoilModifier * 100) + "%");
            }
            
            lore.add("");
            lore.add("§7Для оружия: §f" + weaponId);
            
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public List<String> getDescription() {
        return description;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public String getWeaponId() {
        return weaponId;
    }
    
    public double getDamageModifier() {
        return damageModifier;
    }
    
    public double getPenetrationModifier() {
        return penetrationModifier;
    }
    
    public double getAccuracyModifier() {
        return accuracyModifier;
    }
    
    public double getRangeModifier() {
        return rangeModifier;
    }
    
    public double getReloadTimeModifier() {
        return reloadTimeModifier;
    }
    
    public double getRecoilModifier() {
        return recoilModifier;
    }
} 