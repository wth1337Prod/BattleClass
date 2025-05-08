package me.wth.battleClass.rations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public abstract class Ration {
    protected String id;
    protected String displayName;
    protected List<String> lore;
    protected int foodValue;
    protected double healthValue;
    protected double staminaValue;
    protected int useDuration; 
    protected Material material;
    protected List<PotionEffect> effects;

    public Ration(String id, String displayName, int foodValue, double healthValue, double staminaValue, int useDuration, Material material) {
        this.id = id;
        this.displayName = displayName;
        this.foodValue = foodValue;
        this.healthValue = healthValue;
        this.staminaValue = staminaValue;
        this.useDuration = useDuration;
        this.material = material;
        this.lore = new ArrayList<>();
        this.effects = new ArrayList<>();
        
        this.lore.add(ChatColor.GRAY + "Насыщение: " + ChatColor.GOLD + foodValue);
        this.lore.add(ChatColor.GRAY + "Восстановление здоровья: " + ChatColor.RED + healthValue + " ♥");
        this.lore.add(ChatColor.GRAY + "Восстановление выносливости: " + ChatColor.GREEN + staminaValue);
        this.lore.add(ChatColor.GRAY + "Время употребления: " + ChatColor.AQUA + (useDuration / 20.0) + "с");
        this.lore.add("");
        this.lore.add(ChatColor.GRAY + "ПКМ - использовать");
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getFoodValue() {
        return foodValue;
    }

    public double getHealthValue() {
        return healthValue;
    }

    public double getStaminaValue() {
        return staminaValue;
    }

    public int getUseDuration() {
        return useDuration;
    }
    
    public List<PotionEffect> getEffects() {
        return effects;
    }
    
    public void addEffect(PotionEffect effect) {
        effects.add(effect);
    }

    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        
        meta.setDisplayName(ChatColor.RESET + displayName);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    public void addLoreText(String text) {
        int index = lore.size() - 2;
        if (index < 0) index = 0;
        lore.add(index, text);
    }
} 