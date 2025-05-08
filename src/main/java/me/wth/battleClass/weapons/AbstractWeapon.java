package me.wth.battleClass.weapons;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWeapon implements Weapon {
    protected final BattleClass plugin;
    protected final String id;
    protected final String displayName;
    protected final List<String> description;
    protected final Material material;
    protected final double damage;
    protected final double fireRate;
    protected final int range;
    protected final int magazineSize;
    protected final double reloadTime;
    protected final double accuracy;
    protected final double recoil;
    protected final List<WeaponAttachment> availableAttachments;
    
    public AbstractWeapon(BattleClass plugin, String id, String displayName, List<String> description, 
                          Material material, double damage, double fireRate, int range, 
                          int magazineSize, double reloadTime, double accuracy, double recoil) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.damage = damage;
        this.fireRate = fireRate;
        this.range = range;
        this.magazineSize = magazineSize;
        this.reloadTime = reloadTime;
        this.accuracy = accuracy;
        this.recoil = recoil;
        this.availableAttachments = new ArrayList<>();
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public List<String> getDescription() {
        return description;
    }
    
    @Override
    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6" + displayName);
            
            List<String> lore = new ArrayList<>();
            lore.addAll(description);
            lore.add("");
            lore.add("§7Урон: §f" + damage);
            lore.add("§7Скорострельность: §f" + fireRate + " выстр/сек");
            lore.add("§7Дальность: §f" + range + " блоков");
            lore.add("§7Магазин: §f" + magazineSize + " патронов");
            lore.add("§7Время перезарядки: §f" + reloadTime + " сек");
            lore.add("§7Точность: §f" + Math.round(accuracy * 100) + "%");
            lore.add("§7Отдача: §f" + Math.round(recoil * 100) + "%");
            
            meta.setLore(lore);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            
            NamespacedKey key = new NamespacedKey(plugin, "weapon_id");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, id);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    @Override
    public double getDamage() {
        return damage;
    }
    
    @Override
    public double getFireRate() {
        return fireRate;
    }
    
    @Override
    public int getRange() {
        return range;
    }
    
    @Override
    public int getMagazineSize() {
        return magazineSize;
    }
    
    @Override
    public double getReloadTime() {
        return reloadTime;
    }
    
    @Override
    public double getAccuracy() {
        return accuracy;
    }
    
    @Override
    public double getRecoil() {
        return recoil;
    }
    
    @Override
    public List<WeaponAttachment> getAvailableAttachments() {
        return availableAttachments;
    }
    
    /**
     * Добавить аксессуар в список доступных
     * @param attachment аксессуар для добавления
     */
    protected void addAvailableAttachment(WeaponAttachment attachment) {
        availableAttachments.add(attachment);
    }
} 