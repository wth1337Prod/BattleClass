package me.wth.battleClass.grenades;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class Grenade {
    protected String id;
    protected String displayName;
    protected List<String> lore;
    protected double damage;
    protected double blastRadius;
    protected int explosionDelay; 
    protected Material material;

    public Grenade(String id, String displayName, double damage, double blastRadius, int explosionDelay, Material material) {
        this.id = id;
        this.displayName = displayName;
        this.damage = damage;
        this.blastRadius = blastRadius;
        this.explosionDelay = explosionDelay;
        this.material = material;
        this.lore = new ArrayList<>();
        
        this.lore.add(ChatColor.GRAY + "Урон: " + ChatColor.RED + damage);
        this.lore.add(ChatColor.GRAY + "Радиус взрыва: " + ChatColor.YELLOW + blastRadius + "м");
        this.lore.add(ChatColor.GRAY + "Задержка: " + ChatColor.AQUA + (explosionDelay / 20.0) + "с");
        this.lore.add("");
        this.lore.add(ChatColor.GRAY + "ПКМ - кинуть гранату");
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDamage() {
        return damage;
    }

    public double getBlastRadius() {
        return blastRadius;
    }

    public int getExplosionDelay() {
        return explosionDelay;
    }

    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        
        meta.setDisplayName(ChatColor.RESET + displayName);
        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    public void addLoreText(String text) {
        int index = lore.size() - 2;
        if (index < 0) index = 0;
        lore.add(index, text);
    }
} 