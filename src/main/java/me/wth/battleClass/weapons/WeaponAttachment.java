package me.wth.battleClass.weapons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WeaponAttachment {
    private final String id;
    private final String displayName;
    private final List<String> description;
    private final Material material;
    private final AttachmentType type;
    private final AttachmentStats stats;
    
    public WeaponAttachment(String id, String displayName, List<String> description, 
                            Material material, AttachmentType type, AttachmentStats stats) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.type = type;
        this.stats = stats;
    }
    
    /**
     * Создает ItemStack для этого аксессуара
     * @return ItemStack для аксессуара
     */
    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6" + displayName);
            meta.setLore(description);
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
    
    public AttachmentType getType() {
        return type;
    }
    
    public AttachmentStats getStats() {
        return stats;
    }
    
    /**
     * Типы аксессуаров для оружия
     */
    public enum AttachmentType {
        SCOPE,
        BARREL,
        GRIP,
        MAGAZINE,
        STOCK,
        UNDERBARREL
    }
    
    /**
     * Статистики, которые изменяет аксессуар
     */
    public static class AttachmentStats {
        private final double damageModifier;
        private final double fireRateModifier;
        private final double accuracyModifier;
        private final double recoilModifier;
        private final int rangeModifier;
        private final int magazineSizeModifier;
        private final double reloadTimeModifier;
        
        public AttachmentStats(double damageModifier, double fireRateModifier, double accuracyModifier,
                              double recoilModifier, int rangeModifier, int magazineSizeModifier, 
                              double reloadTimeModifier) {
            this.damageModifier = damageModifier;
            this.fireRateModifier = fireRateModifier;
            this.accuracyModifier = accuracyModifier;
            this.recoilModifier = recoilModifier;
            this.rangeModifier = rangeModifier;
            this.magazineSizeModifier = magazineSizeModifier;
            this.reloadTimeModifier = reloadTimeModifier;
        }
        
        
        public double getDamageModifier() {
            return damageModifier;
        }
        
        public double getFireRateModifier() {
            return fireRateModifier;
        }
        
        public double getAccuracyModifier() {
            return accuracyModifier;
        }
        
        public double getRecoilModifier() {
            return recoilModifier;
        }
        
        public int getRangeModifier() {
            return rangeModifier;
        }
        
        public int getMagazineSizeModifier() {
            return magazineSizeModifier;
        }
        
        public double getReloadTimeModifier() {
            return reloadTimeModifier;
        }
    }
} 