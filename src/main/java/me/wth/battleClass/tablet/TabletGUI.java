package me.wth.battleClass.tablet;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.medical.InjuryManager;
import me.wth.battleClass.ranks.Rank;
import me.wth.battleClass.mines.Mine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Класс для создания и управления GUI военного планшета
 */
public class TabletGUI implements InventoryHolder {
    private final BattleClass plugin;
    private Inventory inventory; 
    
    /**
     * Конструктор для TabletGUI
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public TabletGUI(BattleClass plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Возвращает инвентарь, связанный с этим держателем
     * @return текущий инвентарь
     */
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * Открывает главное меню планшета для игрока
     * 
     * @param player игрок, для которого открывается меню
     * @param tabletType тип планшета (us/ru)
     */
    public void openMainMenu(Player player, String tabletType) {
        String title = tabletType.equals("us") ? 
                ChatColor.BLUE + "Планшет США" : 
                ChatColor.RED + "Планшет РФ";
        
        inventory = Bukkit.createInventory(this, 54, title);
        
        inventory.setItem(4, createPlayerInfoItem(player));
        
        inventory.setItem(11, createHealthStatusItem(player));
        
        inventory.setItem(13, createInjuriesItem(player));
        
        inventory.setItem(15, createAmmoItem(player, tabletType));
        
        inventory.setItem(20, createWeaponsItem(player));
        
        inventory.setItem(22, createArmorItem(player));
        
        inventory.setItem(24, createMedicalItem(player));
        
        inventory.setItem(31, createMapItem());
        
        inventory.setItem(38, createRadioItem());
        
        inventory.setItem(40, createMissionItem());
        
        inventory.setItem(42, createNotesItem());
        
        inventory.setItem(49, createExitItem());
        
        ItemStack filler = createFillerItem(tabletType);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
        
        player.openInventory(inventory);
    }
    
    /**
     * Создает информационный предмет о игроке
     */
    private ItemStack createPlayerInfoItem(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName(ChatColor.GOLD + "Боец: " + player.getName());
            
            List<String> lore = new ArrayList<>();
            
            String factionName = "Нет данных";
            String rankName = "Не определен";
            ChatColor rankColor = ChatColor.GRAY;
            
            if (plugin.getRankManager() != null) {
                String faction = plugin.getRankManager().getPlayerFaction(player);
                Rank rank = plugin.getRankManager().getPlayerRank(player);
                
                if (faction != null) {
                    factionName = faction.equals("us") ? "США" : "РФ";
                }
                
                if (rank != null) {
                    rankName = rank.getDisplayName();
                    rankColor = rank.getColor();
                }
            }
            
            lore.add(ChatColor.GRAY + "Ранг: " + rankColor + rankName);
            lore.add(ChatColor.GRAY + "Фракция: " + ChatColor.YELLOW + factionName);
            lore.add(ChatColor.GRAY + "Позывной: " + ChatColor.YELLOW + player.getName());
            lore.add("");
            lore.add(ChatColor.GRAY + "Координаты: " + ChatColor.WHITE + 
                    "X: " + player.getLocation().getBlockX() + 
                    " Y: " + player.getLocation().getBlockY() + 
                    " Z: " + player.getLocation().getBlockZ());
            lore.add("");
            lore.add(ChatColor.GRAY + "Мир: " + ChatColor.WHITE + player.getWorld().getName());
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет со статусом здоровья
     */
    private ItemStack createHealthStatusItem(Player player) {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Статус здоровья");
            
            List<String> lore = new ArrayList<>();
            double health = player.getHealth();
            double maxHealth = player.getMaxHealth();
            int healthPercent = (int) ((health / maxHealth) * 100);
            
            lore.add(ChatColor.GRAY + "Здоровье: " + getColorByPercent(healthPercent) + healthPercent + "%");
            lore.add(ChatColor.GRAY + "(" + health + "/" + maxHealth + ")");
            lore.add("");
            
            StringBuilder healthBar = new StringBuilder(ChatColor.GRAY + "[");
            int bars = 20;
            int filledBars = (int) ((health / maxHealth) * bars);
            
            for (int i = 0; i < bars; i++) {
                if (i < filledBars) {
                    healthBar.append(getColorByPercent(healthPercent)).append("|");
                } else {
                    healthBar.append(ChatColor.DARK_GRAY).append("|");
                }
            }
            
            healthBar.append(ChatColor.GRAY).append("]");
            lore.add(healthBar.toString());
            lore.add("");
            
            int foodLevel = player.getFoodLevel();
            int foodPercent = (int) ((foodLevel / 20.0) * 100);
            lore.add(ChatColor.GRAY + "Голод: " + getColorByPercent(foodPercent) + foodPercent + "%");
            
            float saturation = player.getSaturation();
            int saturationPercent = (int) ((saturation / 20.0) * 100);
            lore.add(ChatColor.GRAY + "Насыщение: " + getColorByPercent(saturationPercent) + saturationPercent + "%");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет с информацией о травмах
     */
    private ItemStack createInjuriesItem(Player player) {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_RED + "Текущие травмы");
            
            List<String> lore = new ArrayList<>();
            InjuryManager injuryManager = plugin.getInjuryManager();
            
            int injuryCount = injuryManager.getActiveInjuryCount(player);
            
            if (injuryCount > 0) {
                lore.add(ChatColor.GRAY + "Активные травмы: " + ChatColor.RED + injuryCount);
                lore.add("");
                
                lore.add(ChatColor.RED + "• Кровотечение");
                lore.add(ChatColor.RED + "• Перелом");
                lore.add(ChatColor.RED + "• Сотрясение");
            } else {
                lore.add(ChatColor.GREEN + "Травмы отсутствуют");
            }
            
            lore.add("");
            lore.add(ChatColor.YELLOW + "Нажмите для подробной информации");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет с информацией о боеприпасах в зависимости от типа планшета
     */
    private ItemStack createAmmoItem(Player player, String tabletType) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Боеприпасы");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Информация о доступных");
            lore.add(ChatColor.GRAY + "боеприпасах:");
            lore.add("");
            
            int ammoXM7Standard = 0;
            int ammoXM7AP = 0;
            int ammoXM7Tracer = 0;
            int ammoAK12Standard = 0;
            int ammoAK12AP = 0;
            int ammoAK12Tracer = 0;
            int ammoAK12Subsonic = 0;
            int ammoUdavStandard = 0;
            int ammoUdavAP = 0;
            int ammoUdavExpansive = 0;
            int ammoSigM17Standard = 0;
            int ammoSigM17AP = 0;
            int ammoSigM17JHP = 0;
            int ammoSigM17Subsonic = 0;
            
            for (ItemStack invItem : player.getInventory().getContents()) {
                if (invItem != null && invItem.hasItemMeta() && invItem.getItemMeta().hasDisplayName()) {
                    String displayName = invItem.getItemMeta().getDisplayName();
                    
                    if (displayName.contains("6.8×51") && displayName.contains("стандартный")) {
                        ammoXM7Standard += invItem.getAmount();
                    } else if (displayName.contains("6.8×51") && displayName.contains("бронебойный")) {
                        ammoXM7AP += invItem.getAmount();
                    } else if (displayName.contains("6.8×51") && displayName.contains("трассирующий")) {
                        ammoXM7Tracer += invItem.getAmount();
                    } else if (displayName.contains("5.45×39") && displayName.contains("стандартный")) {
                        ammoAK12Standard += invItem.getAmount();
                    } else if (displayName.contains("5.45×39") && displayName.contains("бронебойный")) {
                        ammoAK12AP += invItem.getAmount();
                    } else if (displayName.contains("5.45×39") && displayName.contains("трассирующий")) {
                        ammoAK12Tracer += invItem.getAmount();
                    } else if (displayName.contains("5.45×39") && displayName.contains("дозвуковой")) {
                        ammoAK12Subsonic += invItem.getAmount();
                    }
                    else if (displayName.contains("9×21") && displayName.contains("стандартный")) {
                        ammoUdavStandard += invItem.getAmount();
                    } else if (displayName.contains("9×21") && displayName.contains("бронебойный")) {
                        ammoUdavAP += invItem.getAmount();
                    } else if (displayName.contains("9×21") && displayName.contains("экспансивный")) {
                        ammoUdavExpansive += invItem.getAmount();
                    }
                    else if (displayName.contains("9×19") && displayName.contains("стандартный")) {
                        ammoSigM17Standard += invItem.getAmount();
                    } else if (displayName.contains("9×19") && displayName.contains("бронебойный")) {
                        ammoSigM17AP += invItem.getAmount();
                    } else if (displayName.contains("9×19") && displayName.contains("JHP")) {
                        ammoSigM17JHP += invItem.getAmount();
                    } else if (displayName.contains("9×19") && displayName.contains("дозвуковой")) {
                        ammoSigM17Subsonic += invItem.getAmount();
                    }
                }
            }
            
            if (tabletType.equals("us")) {
                int totalXM7Ammo = ammoXM7Standard + ammoXM7AP + ammoXM7Tracer;
                if (totalXM7Ammo > 0) {
                    lore.add(ChatColor.YELLOW + "• 6.8×51 мм: " + ChatColor.WHITE + totalXM7Ammo + " шт.");
                    if (ammoXM7Standard > 0) {
                        lore.add(ChatColor.GRAY + "   Стандартные: " + ChatColor.WHITE + ammoXM7Standard + " шт.");
                    }
                    if (ammoXM7AP > 0) {
                        lore.add(ChatColor.GRAY + "   Бронебойные: " + ChatColor.WHITE + ammoXM7AP + " шт.");
                    }
                    if (ammoXM7Tracer > 0) {
                        lore.add(ChatColor.GRAY + "   Трассирующие: " + ChatColor.WHITE + ammoXM7Tracer + " шт.");
                    }
                } else {
                    lore.add(ChatColor.YELLOW + "• 6.8×51 мм: " + ChatColor.RED + "0 шт.");
                }
                
                int totalSigM17Ammo = ammoSigM17Standard + ammoSigM17AP + ammoSigM17JHP + ammoSigM17Subsonic;
                if (totalSigM17Ammo > 0) {
                    lore.add(ChatColor.YELLOW + "• 9×19 мм: " + ChatColor.WHITE + totalSigM17Ammo + " шт.");
                    if (ammoSigM17Standard > 0) {
                        lore.add(ChatColor.GRAY + "   Стандартные: " + ChatColor.WHITE + ammoSigM17Standard + " шт.");
                    }
                    if (ammoSigM17AP > 0) {
                        lore.add(ChatColor.GRAY + "   Бронебойные M1152: " + ChatColor.WHITE + ammoSigM17AP + " шт.");
                    }
                    if (ammoSigM17JHP > 0) {
                        lore.add(ChatColor.GRAY + "   Экспансивные JHP: " + ChatColor.WHITE + ammoSigM17JHP + " шт.");
                    }
                    if (ammoSigM17Subsonic > 0) {
                        lore.add(ChatColor.GRAY + "   Дозвуковые: " + ChatColor.WHITE + ammoSigM17Subsonic + " шт.");
                    }
                } else {
                    lore.add(ChatColor.YELLOW + "• 9×19 мм: " + ChatColor.RED + "0 шт.");
                }
            } else {
                int totalAK12Ammo = ammoAK12Standard + ammoAK12AP + ammoAK12Tracer + ammoAK12Subsonic;
                if (totalAK12Ammo > 0) {
                    lore.add(ChatColor.YELLOW + "• 5.45×39 мм: " + ChatColor.WHITE + totalAK12Ammo + " шт.");
                    if (ammoAK12Standard > 0) {
                        lore.add(ChatColor.GRAY + "   Стандартные: " + ChatColor.WHITE + ammoAK12Standard + " шт.");
                    }
                    if (ammoAK12AP > 0) {
                        lore.add(ChatColor.GRAY + "   Бронебойные БС: " + ChatColor.WHITE + ammoAK12AP + " шт.");
                    }
                    if (ammoAK12Tracer > 0) {
                        lore.add(ChatColor.GRAY + "   Трассирующие Т: " + ChatColor.WHITE + ammoAK12Tracer + " шт.");
                    }
                    if (ammoAK12Subsonic > 0) {
                        lore.add(ChatColor.GRAY + "   Дозвуковые УС: " + ChatColor.WHITE + ammoAK12Subsonic + " шт.");
                    }
                } else {
                    lore.add(ChatColor.YELLOW + "• 5.45×39 мм: " + ChatColor.RED + "0 шт.");
                }
                
                int totalUdavAmmo = ammoUdavStandard + ammoUdavAP + ammoUdavExpansive;
                if (totalUdavAmmo > 0) {
                    lore.add(ChatColor.YELLOW + "• 9×21 мм: " + ChatColor.WHITE + totalUdavAmmo + " шт.");
                    if (ammoUdavStandard > 0) {
                        lore.add(ChatColor.GRAY + "   Стандартные: " + ChatColor.WHITE + ammoUdavStandard + " шт.");
                    }
                    if (ammoUdavAP > 0) {
                        lore.add(ChatColor.GRAY + "   Бронебойные СП-12: " + ChatColor.WHITE + ammoUdavAP + " шт.");
                    }
                    if (ammoUdavExpansive > 0) {
                        lore.add(ChatColor.GRAY + "   Экспансивные: " + ChatColor.WHITE + ammoUdavExpansive + " шт.");
                    }
                } else {
                    lore.add(ChatColor.YELLOW + "• 9×21 мм: " + ChatColor.RED + "0 шт.");
                }
            }
            
            lore.add("");
            lore.add(ChatColor.YELLOW + "Нажмите для подробной информации");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет с информацией об оружии
     */
    private ItemStack createWeaponsItem(Player player) {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_GRAY + "Вооружение");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Информация о вашем");
            lore.add(ChatColor.GRAY + "текущем вооружении:");
            lore.add("");
            
            boolean hasAK12 = false;
            boolean hasXM7 = false;
            boolean hasUdav = false;
            boolean hasSigM17 = false;
            
            for (ItemStack invItem : player.getInventory().getContents()) {
                if (invItem != null && invItem.hasItemMeta() && invItem.getItemMeta().hasDisplayName()) {
                    String displayName = invItem.getItemMeta().getDisplayName();
                    
                    if (displayName.contains("AK-12") || displayName.contains("АК-12")) {
                        hasAK12 = true;
                    } else if (displayName.contains("XM7") || displayName.contains("ХМ7")) {
                        hasXM7 = true;
                    } else if (displayName.contains("Удав")) {
                        hasUdav = true;
                    } else if (displayName.contains("Sig Sauer M17") || displayName.contains("M17")) {
                        hasSigM17 = true;
                    }
                }
            }
            
            if (hasAK12) {
                lore.add(ChatColor.AQUA + "• Основное: " + ChatColor.WHITE + "АК-12");
            } else if (hasXM7) {
                lore.add(ChatColor.AQUA + "• Основное: " + ChatColor.WHITE + "XM7");
            } else {
                lore.add(ChatColor.AQUA + "• Основное: " + ChatColor.RED + "Отсутствует");
            }
            
            if (hasUdav) {
                lore.add(ChatColor.AQUA + "• Вторичное: " + ChatColor.WHITE + "Пистолет Удав");
            } else if (hasSigM17) {
                lore.add(ChatColor.AQUA + "• Вторичное: " + ChatColor.WHITE + "Sig Sauer M17");
            } else {
                lore.add(ChatColor.AQUA + "• Вторичное: " + ChatColor.RED + "Отсутствует");
            }
            
            lore.add(ChatColor.AQUA + "• Холодное: " + ChatColor.RED + "Отсутствует");
            
            lore.add("");
            lore.add(ChatColor.YELLOW + "Нажмите для подробной информации");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет с информацией о броне
     */
    private ItemStack createArmorItem(Player player) {
        ItemStack item = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_AQUA + "Экипировка");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Информация о вашей");
            lore.add(ChatColor.GRAY + "текущей экипировке:");
            lore.add("");
            
            ItemStack helmet = player.getInventory().getHelmet();
            ItemStack chestplate = player.getInventory().getChestplate();
            ItemStack leggings = player.getInventory().getLeggings();
            ItemStack boots = player.getInventory().getBoots();
            
            if (helmet != null && helmet.getType() != Material.AIR) {
                String helmetName = helmet.hasItemMeta() && helmet.getItemMeta().hasDisplayName() ? 
                        helmet.getItemMeta().getDisplayName() : "Шлем";
                lore.add(ChatColor.BLUE + "• Шлем: " + ChatColor.WHITE + helmetName);
            } else {
                lore.add(ChatColor.BLUE + "• Шлем: " + ChatColor.RED + "Отсутствует");
            }
            
            if (chestplate != null && chestplate.getType() != Material.AIR) {
                String chestplateName = chestplate.hasItemMeta() && chestplate.getItemMeta().hasDisplayName() ? 
                        chestplate.getItemMeta().getDisplayName() : "Бронежилет";
                lore.add(ChatColor.BLUE + "• Бронежилет: " + ChatColor.WHITE + chestplateName);
            } else {
                lore.add(ChatColor.BLUE + "• Бронежилет: " + ChatColor.RED + "Отсутствует");
            }
            
            if (leggings != null && leggings.getType() != Material.AIR) {
                String leggingsName = leggings.hasItemMeta() && leggings.getItemMeta().hasDisplayName() ? 
                        leggings.getItemMeta().getDisplayName() : "Штаны";
                lore.add(ChatColor.BLUE + "• Штаны: " + ChatColor.WHITE + leggingsName);
            } else {
                lore.add(ChatColor.BLUE + "• Штаны: " + ChatColor.RED + "Отсутствуют");
            }
            
            if (boots != null && boots.getType() != Material.AIR) {
                String bootsName = boots.hasItemMeta() && boots.getItemMeta().hasDisplayName() ? 
                        boots.getItemMeta().getDisplayName() : "Ботинки";
                lore.add(ChatColor.BLUE + "• Ботинки: " + ChatColor.WHITE + bootsName);
            } else {
                lore.add(ChatColor.BLUE + "• Ботинки: " + ChatColor.RED + "Отсутствуют");
            }
            
            lore.add("");
            lore.add(ChatColor.YELLOW + "Нажмите для подробной информации");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет с информацией о медикаментах
     */
    private ItemStack createMedicalItem(Player player) {
        ItemStack item = new ItemStack(Material.GHAST_TEAR);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Медикаменты");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Информация о доступных");
            lore.add(ChatColor.GRAY + "медицинских средствах:");
            lore.add("");
            
            int bandages = 0;
            int medkits = 0;
            int morphine = 0;
            int splints = 0;
            
            for (ItemStack invItem : player.getInventory().getContents()) {
                if (invItem != null && invItem.hasItemMeta() && invItem.getItemMeta().hasDisplayName()) {
                    String displayName = invItem.getItemMeta().getDisplayName();
                    
                    if (displayName.contains("Бинт") || displayName.contains("бинт")) {
                        bandages += invItem.getAmount();
                    } else if (displayName.contains("Аптечк") || displayName.contains("аптечк")) {
                        medkits += invItem.getAmount();
                    } else if (displayName.contains("Морфин") || displayName.contains("морфин")) {
                        morphine += invItem.getAmount();
                    } else if (displayName.contains("Шин") || displayName.contains("шин")) {
                        splints += invItem.getAmount();
                    }
                }
            }
            
            if (bandages > 0) {
                lore.add(ChatColor.LIGHT_PURPLE + "• Бинты: " + ChatColor.WHITE + bandages + " шт.");
            } else {
                lore.add(ChatColor.LIGHT_PURPLE + "• Бинты: " + ChatColor.RED + "Нет");
            }
            
            if (medkits > 0) {
                lore.add(ChatColor.LIGHT_PURPLE + "• Аптечка: " + ChatColor.WHITE + medkits + " шт.");
            } else {
                lore.add(ChatColor.LIGHT_PURPLE + "• Аптечка: " + ChatColor.RED + "Нет");
            }
            
            if (morphine > 0) {
                lore.add(ChatColor.LIGHT_PURPLE + "• Морфин: " + ChatColor.WHITE + morphine + " шт.");
            } else {
                lore.add(ChatColor.LIGHT_PURPLE + "• Морфин: " + ChatColor.RED + "Нет");
            }
            
            if (splints > 0) {
                lore.add(ChatColor.LIGHT_PURPLE + "• Шина: " + ChatColor.WHITE + splints + " шт.");
            } else {
                lore.add(ChatColor.LIGHT_PURPLE + "• Шина: " + ChatColor.RED + "Нет");
            }
            
            lore.add("");
            lore.add(ChatColor.YELLOW + "Нажмите для подробной информации");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет с картой/GPS
     */
    private ItemStack createMapItem() {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_GREEN + "Тактическая карта");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Интерактивная карта");
            lore.add(ChatColor.GRAY + "местности с GPS-навигацией");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Нажмите для открытия карты");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет с радио связью
     */
    private ItemStack createRadioItem() {
        ItemStack item = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_PURPLE + "Радиосвязь");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Связь с командным центром");
            lore.add(ChatColor.GRAY + "и другими бойцами");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Нажмите для доступа к радиоканалам");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет с информацией о задании
     */
    private ItemStack createMissionItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Задание");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Информация о текущей");
            lore.add(ChatColor.GRAY + "боевой задаче");
            lore.add("");
            lore.add(ChatColor.WHITE + "Операция: " + ChatColor.YELLOW + "Быстрый удар");
            lore.add(ChatColor.WHITE + "Статус: " + ChatColor.GREEN + "В процессе");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Нажмите для подробной информации");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет с тактическими заметками
     */
    private ItemStack createNotesItem() {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Тактические заметки");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Личные заметки и");
            lore.add(ChatColor.GRAY + "тактическая информация");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Нажмите для просмотра и редактирования");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает предмет кнопки выхода
     */
    private ItemStack createExitItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Закрыть планшет");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Нажмите, чтобы закрыть");
            lore.add(ChatColor.GRAY + "тактический планшет");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Создает декоративный элемент-заполнитель для пустых слотов
     */
    private ItemStack createFillerItem(String tabletType) {
        Material material = tabletType.equals("us") ? 
                Material.BLUE_STAINED_GLASS_PANE : 
                Material.RED_STAINED_GLASS_PANE;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(" ");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Возвращает цвет в зависимости от процента (для отображения здоровья и т.д.)
     */
    private ChatColor getColorByPercent(int percent) {
        if (percent <= 20) {
            return ChatColor.DARK_RED;
        } else if (percent <= 40) {
            return ChatColor.RED;
        } else if (percent <= 60) {
            return ChatColor.GOLD;
        } else if (percent <= 80) {
            return ChatColor.YELLOW;
        } else {
            return ChatColor.GREEN;
        }
    }
    
    /**
     * Открывает подробную информацию о травмах игрока
     * 
     * @param player игрок
     * @param tabletType тип планшета
     */
    public void openInjuriesDetails(Player player, String tabletType) {
        String title = tabletType.equals("us") ? 
                ChatColor.BLUE + "Травмы - Планшет США" : 
                ChatColor.RED + "Травмы - Планшет РФ";
        
        inventory = Bukkit.createInventory(this, 36, title);
        
        InjuryManager injuryManager = plugin.getInjuryManager();
        
        ItemStack fractureItem = new ItemStack(injuryManager.hasInjury(player) ? 
                Material.BONE : Material.BONE_MEAL);
        ItemMeta fractureMeta = fractureItem.getItemMeta();
        
        if (fractureMeta != null) {
            fractureMeta.setDisplayName(injuryManager.hasInjury(player) ? 
                    ChatColor.RED + "Перелом" : 
                    ChatColor.GREEN + "Нет перелома");
            
            List<String> lore = new ArrayList<>();
            if (injuryManager.hasInjury(player)) {
                lore.add(ChatColor.GRAY + "У вас имеется перелом,");
                lore.add(ChatColor.GRAY + "что замедляет передвижение.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Рекомендация: Используйте шину");
                lore.add(ChatColor.YELLOW + "или аптечку для лечения.");
            } else {
                lore.add(ChatColor.GRAY + "У вас нет перелома.");
                lore.add(ChatColor.GRAY + "Передвижение не затруднено.");
            }
            
            fractureMeta.setLore(lore);
            fractureItem.setItemMeta(fractureMeta);
        }
        
        ItemStack bleedingItem = new ItemStack(injuryManager.isBleeding(player) ? 
                Material.REDSTONE : Material.GREEN_DYE);
        ItemMeta bleedingMeta = bleedingItem.getItemMeta();
        
        if (bleedingMeta != null) {
            bleedingMeta.setDisplayName(injuryManager.isBleeding(player) ? 
                    ChatColor.DARK_RED + "Кровотечение" : 
                    ChatColor.GREEN + "Нет кровотечения");
            
            List<String> lore = new ArrayList<>();
            if (injuryManager.isBleeding(player)) {
                lore.add(ChatColor.GRAY + "У вас кровотечение,");
                lore.add(ChatColor.GRAY + "наносящее периодический урон.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Рекомендация: Используйте бинты");
                lore.add(ChatColor.YELLOW + "или аптечку для остановки.");
                lore.add("");
                lore.add(ChatColor.RED + "⚠ Требуется немедленное лечение!");
            } else {
                lore.add(ChatColor.GRAY + "У вас нет кровотечения.");
            }
            
            bleedingMeta.setLore(lore);
            bleedingItem.setItemMeta(bleedingMeta);
        }
        
        boolean hasConcussion = player.hasPotionEffect(PotionEffectType.NAUSEA);
        ItemStack concussionItem = new ItemStack(hasConcussion ? 
                Material.FERMENTED_SPIDER_EYE : Material.SPIDER_EYE);
        ItemMeta concussionMeta = concussionItem.getItemMeta();
        
        if (concussionMeta != null) {
            concussionMeta.setDisplayName(hasConcussion ? 
                    ChatColor.DARK_PURPLE + "Сотрясение" : 
                    ChatColor.GREEN + "Нет сотрясения");
            
            List<String> lore = new ArrayList<>();
            if (hasConcussion) {
                lore.add(ChatColor.GRAY + "У вас сотрясение,");
                lore.add(ChatColor.GRAY + "вызывающее головокружение.");
                lore.add("");
                lore.add(ChatColor.YELLOW + "Рекомендация: Отдохните и");
                lore.add(ChatColor.YELLOW + "используйте аптечку.");
            } else {
                lore.add(ChatColor.GRAY + "У вас нет сотрясения.");
            }
            
            concussionMeta.setLore(lore);
            concussionItem.setItemMeta(concussionMeta);
        }
        
        ItemStack firstAidItem = new ItemStack(Material.GHAST_TEAR);
        ItemMeta firstAidMeta = firstAidItem.getItemMeta();
        
        if (firstAidMeta != null) {
            firstAidMeta.setDisplayName(ChatColor.GREEN + "Доступные медикаменты");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Информация о доступных");
            lore.add(ChatColor.GRAY + "медицинских средствах:");
            lore.add("");
            
            int bandages = 0;
            int medkits = 0;
            int morphine = 0;
            int splints = 0;
            
            for (ItemStack invItem : player.getInventory().getContents()) {
                if (invItem != null && invItem.hasItemMeta() && invItem.getItemMeta().hasDisplayName()) {
                    String displayName = invItem.getItemMeta().getDisplayName();
                    
                    if (displayName.contains("Бинт") || displayName.contains("бинт")) {
                        bandages += invItem.getAmount();
                    } else if (displayName.contains("Аптечк") || displayName.contains("аптечк")) {
                        medkits += invItem.getAmount();
                    } else if (displayName.contains("Морфин") || displayName.contains("морфин")) {
                        morphine += invItem.getAmount();
                    } else if (displayName.contains("Шин") || displayName.contains("шин")) {
                        splints += invItem.getAmount();
                    }
                }
            }
            
            if (bandages > 0) {
                lore.add(ChatColor.LIGHT_PURPLE + "• Бинты: " + ChatColor.WHITE + bandages + " шт.");
            } else {
                lore.add(ChatColor.LIGHT_PURPLE + "• Бинты: " + ChatColor.RED + "Нет");
            }
            
            if (medkits > 0) {
                lore.add(ChatColor.LIGHT_PURPLE + "• Аптечка: " + ChatColor.WHITE + medkits + " шт.");
            } else {
                lore.add(ChatColor.LIGHT_PURPLE + "• Аптечка: " + ChatColor.RED + "Нет");
            }
            
            if (morphine > 0) {
                lore.add(ChatColor.LIGHT_PURPLE + "• Морфин: " + ChatColor.WHITE + morphine + " шт.");
            } else {
                lore.add(ChatColor.LIGHT_PURPLE + "• Морфин: " + ChatColor.RED + "Нет");
            }
            
            if (splints > 0) {
                lore.add(ChatColor.LIGHT_PURPLE + "• Шина: " + ChatColor.WHITE + splints + " шт.");
            } else {
                lore.add(ChatColor.LIGHT_PURPLE + "• Шина: " + ChatColor.RED + "Нет");
            }
            
            firstAidMeta.setLore(lore);
            firstAidItem.setItemMeta(firstAidMeta);
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Назад в главное меню");
            backMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Вернуться в главное меню планшета"));
            backItem.setItemMeta(backMeta);
        }
        
        inventory.setItem(11, fractureItem);   
        inventory.setItem(13, bleedingItem);   
        inventory.setItem(15, concussionItem); 
        inventory.setItem(22, firstAidItem);   
        inventory.setItem(31, backItem);       
        
        ItemStack filler = createFillerItem(tabletType);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
        
        player.openInventory(inventory);
    }
    
    /**
     * Открывает подробную информацию об оружии игрока
     * 
     * @param player игрок
     * @param tabletType тип планшета
     */
    public void openWeaponsDetails(Player player, String tabletType) {
        String title = tabletType.equals("us") ? 
                ChatColor.BLUE + "Вооружение - Планшет США" : 
                ChatColor.RED + "Вооружение - Планшет РФ";
        
        inventory = Bukkit.createInventory(this, 36, title);
        
        boolean hasAK12 = false;
        boolean hasXM7 = false;
        boolean hasUdav = false;
        boolean hasSigM17 = false;
        
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.hasItemMeta() && invItem.getItemMeta().hasDisplayName()) {
                String displayName = invItem.getItemMeta().getDisplayName();
                
                if (displayName.contains("AK-12") || displayName.contains("АК-12")) {
                    hasAK12 = true;
                } else if (displayName.contains("XM7") || displayName.contains("ХМ7")) {
                    hasXM7 = true;
                } else if (displayName.contains("Удав")) {
                    hasUdav = true;
                } else if (displayName.contains("Sig Sauer M17") || displayName.contains("M17")) {
                    hasSigM17 = true;
                }
            }
        }
        
        ItemStack primaryItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta primaryMeta = primaryItem.getItemMeta();
        
        if (primaryMeta != null) {
            primaryMeta.setDisplayName(ChatColor.AQUA + "Основное оружие");
            
            List<String> lore = new ArrayList<>();
            if (hasAK12) {
                lore.add(ChatColor.GRAY + "Текущее оружие: " + ChatColor.WHITE + "АК-12");
                lore.add(ChatColor.GRAY + "Патроны: " + ChatColor.WHITE + "5.45×39 мм");
                lore.add(ChatColor.GRAY + "Режим: " + ChatColor.WHITE + "Одиночный/Автоматический");
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• Урон: " + ChatColor.WHITE + "7.5");
                lore.add(ChatColor.YELLOW + "• Скорострельность: " + ChatColor.WHITE + "Высокая");
                lore.add(ChatColor.YELLOW + "• Точность: " + ChatColor.WHITE + "80%");
                lore.add(ChatColor.YELLOW + "• Дальность: " + ChatColor.WHITE + "100м");
            } else if (hasXM7) {
                lore.add(ChatColor.GRAY + "Текущее оружие: " + ChatColor.WHITE + "XM7");
                lore.add(ChatColor.GRAY + "Патроны: " + ChatColor.WHITE + "6.8×51 мм");
                lore.add(ChatColor.GRAY + "Режим: " + ChatColor.WHITE + "Одиночный");
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• Урон: " + ChatColor.WHITE + "7.0");
                lore.add(ChatColor.YELLOW + "• Скорострельность: " + ChatColor.WHITE + "Высокая");
                lore.add(ChatColor.YELLOW + "• Точность: " + ChatColor.WHITE + "85%");
                lore.add(ChatColor.YELLOW + "• Дальность: " + ChatColor.WHITE + "150м");
            } else {
                lore.add(ChatColor.RED + "У вас нет основного оружия.");
            }
            
            primaryMeta.setLore(lore);
            primaryItem.setItemMeta(primaryMeta);
        }
        
        ItemStack secondaryItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta secondaryMeta = secondaryItem.getItemMeta();
        
        if (secondaryMeta != null) {
            secondaryMeta.setDisplayName(ChatColor.AQUA + "Вторичное оружие");
            
            List<String> lore = new ArrayList<>();
            if (hasUdav) {
                lore.add(ChatColor.GRAY + "Текущее оружие: " + ChatColor.WHITE + "Пистолет Удав");
                lore.add(ChatColor.GRAY + "Патроны: " + ChatColor.WHITE + "9×21 мм");
                lore.add(ChatColor.GRAY + "Режим: " + ChatColor.WHITE + "Одиночный");
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• Урон: " + ChatColor.WHITE + "5.5");
                lore.add(ChatColor.YELLOW + "• Скорострельность: " + ChatColor.WHITE + "Средняя");
                lore.add(ChatColor.YELLOW + "• Точность: " + ChatColor.WHITE + "85%");
                lore.add(ChatColor.YELLOW + "• Дальность: " + ChatColor.WHITE + "50м");
                lore.add("");
                lore.add(ChatColor.GRAY + "Современный российский пистолет");
                lore.add(ChatColor.GRAY + "Принят на вооружение в 2019 году");
            } else if (hasSigM17) {
                lore.add(ChatColor.GRAY + "Текущее оружие: " + ChatColor.WHITE + "Sig Sauer M17");
                lore.add(ChatColor.GRAY + "Патроны: " + ChatColor.WHITE + "9×19 мм Parabellum");
                lore.add(ChatColor.GRAY + "Режим: " + ChatColor.WHITE + "Одиночный");
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• Урон: " + ChatColor.WHITE + "5.0");
                lore.add(ChatColor.YELLOW + "• Скорострельность: " + ChatColor.WHITE + "Высокая");
                lore.add(ChatColor.YELLOW + "• Точность: " + ChatColor.WHITE + "90%");
                lore.add(ChatColor.YELLOW + "• Дальность: " + ChatColor.WHITE + "45м");
                lore.add("");
                lore.add(ChatColor.GRAY + "Новый пистолет армии США");
                lore.add(ChatColor.GRAY + "Принят на вооружение в 2017 году");
            } else {
                lore.add(ChatColor.RED + "У вас нет вторичного оружия.");
            }
            
            secondaryMeta.setLore(lore);
            secondaryItem.setItemMeta(secondaryMeta);
        }
        
        ItemStack meleeItem = new ItemStack(Material.STONE_SWORD);
        ItemMeta meleeMeta = meleeItem.getItemMeta();
        
        if (meleeMeta != null) {
            meleeMeta.setDisplayName(ChatColor.AQUA + "Холодное оружие");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED + "У вас нет холодного оружия.");
            
            meleeMeta.setLore(lore);
            meleeItem.setItemMeta(meleeMeta);
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Назад в главное меню");
            backMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Вернуться в главное меню планшета"));
            backItem.setItemMeta(backMeta);
        }
        
        inventory.setItem(11, primaryItem);    
        inventory.setItem(13, secondaryItem);  
        inventory.setItem(15, meleeItem);      
        inventory.setItem(31, backItem);       
        
        ItemStack filler = createFillerItem(tabletType);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
        
        player.openInventory(inventory);
    }
    
    /**
     * Открывает подробную информацию об экипировке игрока
     * 
     * @param player игрок
     * @param tabletType тип планшета
     */
    public void openArmorDetails(Player player, String tabletType) {
        String title = tabletType.equals("us") ? 
                ChatColor.BLUE + "Экипировка - Планшет США" : 
                ChatColor.RED + "Экипировка - Планшет РФ";
        
        inventory = Bukkit.createInventory(this, 36, title);
        
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();
        
        ItemStack helmetItem = new ItemStack(Material.IRON_HELMET);
        ItemMeta helmetMeta = helmetItem.getItemMeta();
        
        if (helmetMeta != null) {
            helmetMeta.setDisplayName(ChatColor.BLUE + "Шлем");
            
            List<String> lore = new ArrayList<>();
            if (helmet != null && helmet.getType() != Material.AIR) {
                String helmetName = helmet.hasItemMeta() && helmet.getItemMeta().hasDisplayName() ? 
                        helmet.getItemMeta().getDisplayName() : "Шлем";
                
                lore.add(ChatColor.GRAY + "Текущий шлем: " + ChatColor.WHITE + helmetName);
                lore.add("");
                lore.add(ChatColor.GRAY + "Защита: " + ChatColor.WHITE + "3/5");
                lore.add(ChatColor.GRAY + "Вес: " + ChatColor.WHITE + "Средний");
                lore.add("");
                lore.add(ChatColor.GRAY + "Особенности:");
                lore.add(ChatColor.YELLOW + "• Защита от пуль малого калибра");
                lore.add(ChatColor.YELLOW + "• Защита от осколков");
            } else {
                lore.add(ChatColor.RED + "У вас нет шлема.");
            }
            
            helmetMeta.setLore(lore);
            helmetItem.setItemMeta(helmetMeta);
        }
        
        ItemStack chestplateItem = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta chestplateMeta = chestplateItem.getItemMeta();
        
        if (chestplateMeta != null) {
            chestplateMeta.setDisplayName(ChatColor.BLUE + "Бронежилет");
            
            List<String> lore = new ArrayList<>();
            if (chestplate != null && chestplate.getType() != Material.AIR) {
                String chestplateName = chestplate.hasItemMeta() && chestplate.getItemMeta().hasDisplayName() ? 
                        chestplate.getItemMeta().getDisplayName() : "Бронежилет";
                
                lore.add(ChatColor.GRAY + "Текущий бронежилет: " + ChatColor.WHITE + chestplateName);
                lore.add("");
                lore.add(ChatColor.GRAY + "Защита: " + ChatColor.WHITE + "4/5");
                lore.add(ChatColor.GRAY + "Вес: " + ChatColor.WHITE + "Тяжелый");
                lore.add("");
                lore.add(ChatColor.GRAY + "Особенности:");
                lore.add(ChatColor.YELLOW + "• Защита от пуль среднего калибра");
                lore.add(ChatColor.YELLOW + "• Защита от осколков");
                lore.add(ChatColor.YELLOW + "• Тактические подсумки");
            } else {
                lore.add(ChatColor.RED + "У вас нет бронежилета.");
            }
            
            chestplateMeta.setLore(lore);
            chestplateItem.setItemMeta(chestplateMeta);
        }
        
        ItemStack leggingsItem = new ItemStack(Material.IRON_LEGGINGS);
        ItemMeta leggingsMeta = leggingsItem.getItemMeta();
        
        if (leggingsMeta != null) {
            leggingsMeta.setDisplayName(ChatColor.BLUE + "Штаны");
            
            List<String> lore = new ArrayList<>();
            if (leggings != null && leggings.getType() != Material.AIR) {
                String leggingsName = leggings.hasItemMeta() && leggings.getItemMeta().hasDisplayName() ? 
                        leggings.getItemMeta().getDisplayName() : "Штаны";
                
                lore.add(ChatColor.GRAY + "Текущие штаны: " + ChatColor.WHITE + leggingsName);
                lore.add("");
                lore.add(ChatColor.GRAY + "Защита: " + ChatColor.WHITE + "2/5");
                lore.add(ChatColor.GRAY + "Вес: " + ChatColor.WHITE + "Легкий");
                lore.add("");
                lore.add(ChatColor.GRAY + "Особенности:");
                lore.add(ChatColor.YELLOW + "• Дополнительные карманы");
                lore.add(ChatColor.YELLOW + "• Усиленная ткань");
            } else {
                lore.add(ChatColor.RED + "У вас нет штанов.");
            }
            
            leggingsMeta.setLore(lore);
            leggingsItem.setItemMeta(leggingsMeta);
        }
        
        ItemStack bootsItem = new ItemStack(Material.IRON_BOOTS);
        ItemMeta bootsMeta = bootsItem.getItemMeta();
        
        if (bootsMeta != null) {
            bootsMeta.setDisplayName(ChatColor.BLUE + "Ботинки");
            
            List<String> lore = new ArrayList<>();
            if (boots != null && boots.getType() != Material.AIR) {
                String bootsName = boots.hasItemMeta() && boots.getItemMeta().hasDisplayName() ? 
                        boots.getItemMeta().getDisplayName() : "Ботинки";
                
                lore.add(ChatColor.GRAY + "Текущие ботинки: " + ChatColor.WHITE + bootsName);
                lore.add("");
                lore.add(ChatColor.GRAY + "Защита: " + ChatColor.WHITE + "2/5");
                lore.add(ChatColor.GRAY + "Вес: " + ChatColor.WHITE + "Средний");
                lore.add("");
                lore.add(ChatColor.GRAY + "Особенности:");
                lore.add(ChatColor.YELLOW + "• Защита от осколков");
                lore.add(ChatColor.YELLOW + "• Усиленная подошва");
            } else {
                lore.add(ChatColor.RED + "У вас нет ботинок.");
            }
            
            bootsMeta.setLore(lore);
            bootsItem.setItemMeta(bootsMeta);
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Назад в главное меню");
            backMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Вернуться в главное меню планшета"));
            backItem.setItemMeta(backMeta);
        }
        
        inventory.setItem(10, helmetItem);     
        inventory.setItem(12, chestplateItem); 
        inventory.setItem(14, leggingsItem);   
        inventory.setItem(16, bootsItem);      
        inventory.setItem(31, backItem);       
        
        ItemStack filler = createFillerItem(tabletType);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
        
        player.openInventory(inventory);
    }
    
    /**
     * Открывает подробную информацию о медикаментах игрока
     * 
     * @param player игрок
     * @param tabletType тип планшета
     */
    public void openMedicalDetails(Player player, String tabletType) {
        String title = tabletType.equals("us") ? 
                ChatColor.BLUE + "Медикаменты - Планшет США" : 
                ChatColor.RED + "Медикаменты - Планшет РФ";
        
        inventory = Bukkit.createInventory(this, 36, title);
        
        int bandages = 0;
        int medkits = 0;
        int morphine = 0;
        int splints = 0;
        
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.hasItemMeta() && invItem.getItemMeta().hasDisplayName()) {
                String displayName = invItem.getItemMeta().getDisplayName();
                
                if (displayName.contains("Бинт") || displayName.contains("бинт")) {
                    bandages += invItem.getAmount();
                } else if (displayName.contains("Аптечк") || displayName.contains("аптечк")) {
                    medkits += invItem.getAmount();
                } else if (displayName.contains("Морфин") || displayName.contains("морфин")) {
                    morphine += invItem.getAmount();
                } else if (displayName.contains("Шин") || displayName.contains("шин")) {
                    splints += invItem.getAmount();
                }
            }
        }
        
        ItemStack bandageItem = new ItemStack(Material.PAPER);
        ItemMeta bandageMeta = bandageItem.getItemMeta();
        
        if (bandageMeta != null) {
            bandageMeta.setDisplayName(ChatColor.GREEN + "Бинты");
            
            List<String> lore = new ArrayList<>();
            if (bandages > 0) {
                lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + bandages + " шт.");
            } else {
                lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "Нет");
            }
            lore.add("");
            lore.add(ChatColor.GRAY + "Эффекты:");
            lore.add(ChatColor.YELLOW + "• Останавливает кровотечение");
            lore.add(ChatColor.YELLOW + "• Восстанавливает 2 HP");
            
            bandageMeta.setLore(lore);
            bandageItem.setItemMeta(bandageMeta);
        }
        
        ItemStack medkitItem = new ItemStack(Material.APPLE);
        ItemMeta medkitMeta = medkitItem.getItemMeta();
        
        if (medkitMeta != null) {
            medkitMeta.setDisplayName(ChatColor.GREEN + "Аптечка");
            
            List<String> lore = new ArrayList<>();
            if (medkits > 0) {
                lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + medkits + " шт.");
            } else {
                lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "Нет");
            }
            lore.add("");
            lore.add(ChatColor.GRAY + "Эффекты:");
            lore.add(ChatColor.YELLOW + "• Останавливает кровотечение");
            lore.add(ChatColor.YELLOW + "• Лечит переломы");
            lore.add(ChatColor.YELLOW + "• Восстанавливает 8 HP");
            
            medkitMeta.setLore(lore);
            medkitItem.setItemMeta(medkitMeta);
        }
        
        ItemStack morphineItem = new ItemStack(Material.POTION);
        ItemMeta morphineMeta = morphineItem.getItemMeta();
        
        if (morphineMeta != null) {
            morphineMeta.setDisplayName(ChatColor.GREEN + "Морфин");
            
            List<String> lore = new ArrayList<>();
            if (morphine > 0) {
                lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + morphine + " шт.");
            } else {
                lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "Нет");
            }
            lore.add("");
            lore.add(ChatColor.GRAY + "Эффекты:");
            lore.add(ChatColor.YELLOW + "• Снимает болевой шок");
            lore.add(ChatColor.YELLOW + "• Временно улучшает выносливость");
            lore.add(ChatColor.RED + "• Временное привыкание при частом использовании");
            
            morphineMeta.setLore(lore);
            morphineItem.setItemMeta(morphineMeta);
        }
        
        ItemStack splintItem = new ItemStack(Material.STICK);
        ItemMeta splintMeta = splintItem.getItemMeta();
        
        if (splintMeta != null) {
            splintMeta.setDisplayName(ChatColor.GREEN + "Шина");
            
            List<String> lore = new ArrayList<>();
            if (splints > 0) {
                lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + splints + " шт.");
            } else {
                lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "Нет");
            }
            lore.add("");
            lore.add(ChatColor.GRAY + "Эффекты:");
            lore.add(ChatColor.YELLOW + "• Лечит переломы");
            
            splintMeta.setLore(lore);
            splintItem.setItemMeta(splintMeta);
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Назад в главное меню");
            backMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Вернуться в главное меню планшета"));
            backItem.setItemMeta(backMeta);
        }
        
        inventory.setItem(10, bandageItem);  
        inventory.setItem(12, medkitItem);   
        inventory.setItem(14, morphineItem); 
        inventory.setItem(16, splintItem);   
        inventory.setItem(31, backItem);     
        
        ItemStack filler = createFillerItem(tabletType);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
        
        player.openInventory(inventory);
    }
    
    /**
     * Открывает тактическую карту с учетом масштаба
     * 
     * @param player игрок
     * @param tabletType тип планшета
     */
    public void openMapDetails(Player player, String tabletType) {
        int zoomLevel = 1; 
        
        TabletListener tabletListener = plugin.getTabletListener();
        if (tabletListener != null) {
            zoomLevel = tabletListener.getMapZoomLevel(player.getUniqueId());
        }
        
        String title = tabletType.equals("us") ? 
                ChatColor.BLUE + "Тактическая карта - США" : 
                ChatColor.RED + "Тактическая карта - РФ";
        
        inventory = Bukkit.createInventory(this, 54, title);
        
        ItemStack playerLocationItem = new ItemStack(Material.COMPASS);
        ItemMeta playerLocationMeta = playerLocationItem.getItemMeta();
        
        if (playerLocationMeta != null) {
            playerLocationMeta.setDisplayName(ChatColor.YELLOW + "Ваше местоположение");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Текущие координаты:");
            lore.add(ChatColor.WHITE + "X: " + player.getLocation().getBlockX());
            lore.add(ChatColor.WHITE + "Y: " + player.getLocation().getBlockY());
            lore.add(ChatColor.WHITE + "Z: " + player.getLocation().getBlockZ());
            lore.add("");
            lore.add(ChatColor.GRAY + "Биом: " + ChatColor.WHITE + player.getLocation().getBlock().getBiome().name());
            lore.add(ChatColor.GRAY + "Высота: " + ChatColor.WHITE + player.getLocation().getBlockY() + " блоков");
            lore.add("");
            lore.add(ChatColor.GRAY + "Текущий масштаб: " + ChatColor.GOLD + "x" + zoomLevel);
            
            playerLocationMeta.setLore(lore);
            playerLocationItem.setItemMeta(playerLocationMeta);
        }
        
        Material[][] mapMaterials = generateMapMaterials(player, zoomLevel);
        
        int startSlot = 10; 
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 7; x++) {
                int slot = startSlot + y * 9 + x;
                Material blockMaterial = mapMaterials[y][x];
                
                ItemStack mapBlock = new ItemStack(blockMaterial);
                ItemMeta meta = mapBlock.getItemMeta();
                
                if (meta != null) {
                    if (y == 1 && x == 3) {
                        meta.setDisplayName(ChatColor.YELLOW + "Вы здесь");
                        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    } else {
                        meta.setDisplayName(ChatColor.GRAY + "Карта местности");
                    }
                    
                    int distanceMultiplier;
                    switch (zoomLevel) {
                        case 1:
                            distanceMultiplier = 20; 
                            break;
                        case 2:
                            distanceMultiplier = 10; 
                            break;
                        case 3:
                            distanceMultiplier = 5;  
                            break;
                        default:
                            distanceMultiplier = 20;
                    }
                    
                    int blockX = player.getLocation().getBlockX() + (x - 3) * distanceMultiplier;
                    int blockZ = player.getLocation().getBlockZ() + (y - 1) * distanceMultiplier;
                    
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Примерная область:");
                    lore.add(ChatColor.WHITE + "X: " + blockX + " Z: " + blockZ);
                    
                    if (zoomLevel == 3) {
                        lore.add("");
                        lore.add(ChatColor.GOLD + "Детальный просмотр (x3)");
                    } else if (zoomLevel == 2) {
                        lore.add("");
                        lore.add(ChatColor.GOLD + "Средний масштаб (x2)");
                    }
                    
                    meta.setLore(lore);
                    mapBlock.setItemMeta(meta);
                }
                
                inventory.setItem(slot, mapBlock);
            }
        }
        
        ItemStack zoomInItem = new ItemStack(Material.ENDER_EYE);
        ItemMeta zoomInMeta = zoomInItem.getItemMeta();
        
        if (zoomInMeta != null) {
            zoomInMeta.setDisplayName(ChatColor.GREEN + "Приблизить");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Увеличить масштаб карты");
            
            if (zoomLevel >= 3) {
                lore.add(ChatColor.RED + "Максимальное приближение");
            } else {
                lore.add(ChatColor.GRAY + "Текущий масштаб: " + ChatColor.GOLD + "x" + zoomLevel);
                lore.add(ChatColor.GRAY + "Нажмите для перехода на x" + (zoomLevel + 1));
            }
            
            zoomInMeta.setLore(lore);
            zoomInItem.setItemMeta(zoomInMeta);
        }
        
        ItemStack zoomOutItem = new ItemStack(Material.ENDER_PEARL);
        ItemMeta zoomOutMeta = zoomOutItem.getItemMeta();
        
        if (zoomOutMeta != null) {
            zoomOutMeta.setDisplayName(ChatColor.RED + "Отдалить");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Уменьшить масштаб карты");
            
            if (zoomLevel <= 1) {
                lore.add(ChatColor.RED + "Максимальное отдаление");
            } else {
                lore.add(ChatColor.GRAY + "Текущий масштаб: " + ChatColor.GOLD + "x" + zoomLevel);
                lore.add(ChatColor.GRAY + "Нажмите для перехода на x" + (zoomLevel - 1));
            }
            
            zoomOutMeta.setLore(lore);
            zoomOutItem.setItemMeta(zoomOutMeta);
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Назад в главное меню");
            backMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Вернуться в главное меню планшета"));
            backItem.setItemMeta(backMeta);
        }
        
        inventory.setItem(4, playerLocationItem); 
        inventory.setItem(47, zoomInItem);        
        inventory.setItem(51, zoomOutItem);       
        inventory.setItem(49, backItem);          
        
        ItemStack filler = createFillerItem(tabletType);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
        
        player.openInventory(inventory);
    }
    
    /**
     * Генерирует материалы для карты на основе положения игрока и масштаба
     * (упрощенная версия, в реальном плагине можно использовать данные о местности)
     * 
     * @param player игрок
     * @param zoomLevel уровень масштабирования
     * @return двумерный массив материалов для отображения на карте
     */
    private Material[][] generateMapMaterials(Player player, int zoomLevel) {
        Material[][] materials = new Material[3][7];
        
        String biomeName = player.getLocation().getBlock().getBiome().name().toLowerCase();
        
        Material primaryMaterial;
        Material secondaryMaterial;
        Material detailMaterial = Material.RED_CONCRETE; 
        
        if (biomeName.contains("forest")) {
            primaryMaterial = Material.GREEN_CONCRETE;
            secondaryMaterial = Material.LIME_CONCRETE;
            detailMaterial = Material.BROWN_CONCRETE; 
        } else if (biomeName.contains("desert")) {
            primaryMaterial = Material.YELLOW_CONCRETE;
            secondaryMaterial = Material.YELLOW_TERRACOTTA;
            detailMaterial = Material.ORANGE_CONCRETE; 
        } else if (biomeName.contains("mountain")) {
            primaryMaterial = Material.GRAY_CONCRETE;
            secondaryMaterial = Material.STONE;
            detailMaterial = Material.BLACK_CONCRETE; 
        } else if (biomeName.contains("ocean") || biomeName.contains("river")) {
            primaryMaterial = Material.BLUE_CONCRETE;
            secondaryMaterial = Material.LIGHT_BLUE_CONCRETE;
            detailMaterial = Material.CYAN_CONCRETE; 
        } else if (biomeName.contains("snow") || biomeName.contains("ice")) {
            primaryMaterial = Material.WHITE_CONCRETE;
            secondaryMaterial = Material.LIGHT_BLUE_CONCRETE;
            detailMaterial = Material.BLUE_ICE; 
        } else {
            primaryMaterial = Material.GREEN_CONCRETE;
            secondaryMaterial = Material.LIME_CONCRETE;
            detailMaterial = Material.BROWN_CONCRETE;
        }
        
        double secondaryVariationChance;
        double detailVariationChance;
        
        switch (zoomLevel) {
            case 1:
                secondaryVariationChance = 0.2;
                detailVariationChance = 0.05;
                break;
            case 2:
                secondaryVariationChance = 0.3;
                detailVariationChance = 0.1;
                break;
            case 3:
                secondaryVariationChance = 0.4;
                detailVariationChance = 0.2;
                break;
            default:
                secondaryVariationChance = 0.2;
                detailVariationChance = 0.05;
        }
        
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 7; x++) {
                if (y == 1 && x == 3) {
                    materials[y][x] = Material.RED_CONCRETE;
                } else {
                    double random = Math.random();
                    if (random < detailVariationChance) {
                        materials[y][x] = detailMaterial;
                    } else if (random < secondaryVariationChance) {
                        materials[y][x] = secondaryMaterial;
                    } else {
                        materials[y][x] = primaryMaterial;
                    }
                }
            }
        }
        
        return materials;
    }
    
    /**
     * Открывает тактические заметки
     * 
     * @param player игрок
     * @param tabletType тип планшета
     */
    public void openNotesDetails(Player player, String tabletType) {
        String title = tabletType.equals("us") ? 
                ChatColor.BLUE + "Тактические заметки - США" : 
                ChatColor.RED + "Тактические заметки - РФ";
        
        inventory = Bukkit.createInventory(this, 36, title);
        
        ItemStack note1Item = new ItemStack(Material.PAPER);
        ItemMeta note1Meta = note1Item.getItemMeta();
        
        if (note1Meta != null) {
            note1Meta.setDisplayName(ChatColor.YELLOW + "Тактика под прикрытием");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "При продвижении по местности,");
            lore.add(ChatColor.WHITE + "используйте укрытия и двигайтесь");
            lore.add(ChatColor.WHITE + "небольшими перебежками. Используйте");
            lore.add(ChatColor.WHITE + "камни и деревья для укрытия.");
            
            note1Meta.setLore(lore);
            note1Item.setItemMeta(note1Meta);
        }
        
        ItemStack note2Item = new ItemStack(Material.PAPER);
        ItemMeta note2Meta = note2Item.getItemMeta();
        
        if (note2Meta != null) {
            note2Meta.setDisplayName(ChatColor.YELLOW + "Патроны и боеприпасы");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "Трассирующие патроны помогают");
            lore.add(ChatColor.WHITE + "в темноте, но выдают вашу позицию.");
            lore.add(ChatColor.WHITE + "Бронебойные хороши против");
            lore.add(ChatColor.WHITE + "бронированных противников.");
            
            note2Meta.setLore(lore);
            note2Item.setItemMeta(note2Meta);
        }
        
        ItemStack note3Item = new ItemStack(Material.PAPER);
        ItemMeta note3Meta = note3Item.getItemMeta();
        
        if (note3Meta != null) {
            note3Meta.setDisplayName(ChatColor.YELLOW + "Медицинская помощь");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "Бинты быстро останавливают");
            lore.add(ChatColor.WHITE + "кровотечение, но не лечат");
            lore.add(ChatColor.WHITE + "переломы. Для этого нужна");
            lore.add(ChatColor.WHITE + "аптечка или шина.");
            
            note3Meta.setLore(lore);
            note3Item.setItemMeta(note3Meta);
        }
        
        ItemStack note4Item = new ItemStack(Material.PAPER);
        ItemMeta note4Meta = note4Item.getItemMeta();
        
        if (note4Meta != null) {
            note4Meta.setDisplayName(ChatColor.YELLOW + "Оружие ближнего боя");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "В ближнем бою используйте");
            lore.add(ChatColor.WHITE + "пистолет с высокой скорострельностью.");
            lore.add(ChatColor.WHITE + "Боевой нож поможет в критической");
            lore.add(ChatColor.WHITE + "ситуации или для скрытности.");
            
            note4Meta.setLore(lore);
            note4Item.setItemMeta(note4Meta);
        }
        
        ItemStack addNoteItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta addNoteMeta = addNoteItem.getItemMeta();
        
        if (addNoteMeta != null) {
            addNoteMeta.setDisplayName(ChatColor.GREEN + "Добавить заметку");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Создать новую тактическую заметку");
            
            addNoteMeta.setLore(lore);
            addNoteItem.setItemMeta(addNoteMeta);
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Назад в главное меню");
            backMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Вернуться в главное меню планшета"));
            backItem.setItemMeta(backMeta);
        }
        
        inventory.setItem(10, note1Item);    
        inventory.setItem(12, note2Item);    
        inventory.setItem(14, note3Item);    
        inventory.setItem(16, note4Item);    
        inventory.setItem(22, addNoteItem);  
        inventory.setItem(31, backItem);     
        
        ItemStack filler = createFillerItem(tabletType);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
        
        player.openInventory(inventory);
    }
    
    /**
     * Открывает подробную информацию о боеприпасах игрока
     * 
     * @param player игрок
     * @param tabletType тип планшета
     */
    public void openAmmoDetails(Player player, String tabletType) {
        String title = tabletType.equals("us") ? 
                ChatColor.BLUE + "Боеприпасы - Планшет США" : 
                ChatColor.RED + "Боеприпасы - Планшет РФ";
        
        inventory = Bukkit.createInventory(this, 45, title);
        
        int ammoXM7Standard = 0;
        int ammoXM7AP = 0;
        int ammoXM7Tracer = 0;
        int ammoAK12Standard = 0;
        int ammoAK12AP = 0;
        int ammoAK12Tracer = 0;
        int ammoAK12Subsonic = 0;
        int ammoUdavStandard = 0;
        int ammoUdavAP = 0;
        int ammoUdavExpansive = 0;
        int ammoSigM17Standard = 0;
        int ammoSigM17AP = 0;
        int ammoSigM17JHP = 0;
        int ammoSigM17Subsonic = 0;
        
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.hasItemMeta() && invItem.getItemMeta().hasDisplayName()) {
                String displayName = invItem.getItemMeta().getDisplayName();
                
                if (displayName.contains("6.8×51") && displayName.contains("стандартный")) {
                    ammoXM7Standard += invItem.getAmount();
                } else if (displayName.contains("6.8×51") && displayName.contains("бронебойный")) {
                    ammoXM7AP += invItem.getAmount();
                } else if (displayName.contains("6.8×51") && displayName.contains("трассирующий")) {
                    ammoXM7Tracer += invItem.getAmount();
                } else if (displayName.contains("5.45×39") && displayName.contains("стандартный")) {
                    ammoAK12Standard += invItem.getAmount();
                } else if (displayName.contains("5.45×39") && displayName.contains("бронебойный")) {
                    ammoAK12AP += invItem.getAmount();
                } else if (displayName.contains("5.45×39") && displayName.contains("трассирующий")) {
                    ammoAK12Tracer += invItem.getAmount();
                } else if (displayName.contains("5.45×39") && displayName.contains("дозвуковой")) {
                    ammoAK12Subsonic += invItem.getAmount();
                }
                else if (displayName.contains("9×21") && displayName.contains("стандартный")) {
                    ammoUdavStandard += invItem.getAmount();
                } else if (displayName.contains("9×21") && displayName.contains("бронебойный")) {
                    ammoUdavAP += invItem.getAmount();
                } else if (displayName.contains("9×21") && displayName.contains("экспансивный")) {
                    ammoUdavExpansive += invItem.getAmount();
                }
                else if (displayName.contains("9×19") && displayName.contains("стандартный")) {
                    ammoSigM17Standard += invItem.getAmount();
                } else if (displayName.contains("9×19") && displayName.contains("бронебойный")) {
                    ammoSigM17AP += invItem.getAmount();
                } else if (displayName.contains("9×19") && displayName.contains("JHP")) {
                    ammoSigM17JHP += invItem.getAmount();
                } else if (displayName.contains("9×19") && displayName.contains("дозвуковой")) {
                    ammoSigM17Subsonic += invItem.getAmount();
                }
            }
        }
        
        if (tabletType.equals("us")) {
            
            ItemStack ammoStandardItem = new ItemStack(Material.IRON_NUGGET);
            ItemMeta standardMeta = ammoStandardItem.getItemMeta();
            
            if (standardMeta != null) {
                standardMeta.setDisplayName(ChatColor.GOLD + "6.8×51 мм стандартный патрон");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Стандартный патрон для XM7");
                lore.add("");
                
                if (ammoXM7Standard > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoXM7Standard + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• Базовый урон");
                lore.add(ChatColor.YELLOW + "• Базовая пробиваемость");
                lore.add(ChatColor.YELLOW + "• Базовая точность");
                
                standardMeta.setLore(lore);
                ammoStandardItem.setItemMeta(standardMeta);
            }
            
            ItemStack ammoAPItem = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta apMeta = ammoAPItem.getItemMeta();
            
            if (apMeta != null) {
                apMeta.setDisplayName(ChatColor.GOLD + "6.8×51 мм бронебойный патрон");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Бронебойный патрон для XM7");
                lore.add("");
                
                if (ammoXM7AP > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoXM7AP + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• +10% к урону");
                lore.add(ChatColor.YELLOW + "• +30% к пробиваемости брони");
                lore.add(ChatColor.YELLOW + "• -5% к точности");
                lore.add(ChatColor.YELLOW + "• +15% к отдаче");
                
                apMeta.setLore(lore);
                ammoAPItem.setItemMeta(apMeta);
            }
            
            ItemStack ammoTracerItem = new ItemStack(Material.GLOWSTONE_DUST);
            ItemMeta tracerMeta = ammoTracerItem.getItemMeta();
            
            if (tracerMeta != null) {
                tracerMeta.setDisplayName(ChatColor.GOLD + "6.8×51 мм трассирующий патрон");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Трассирующий патрон для XM7");
                lore.add("");
                
                if (ammoXM7Tracer > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoXM7Tracer + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• -5% к урону");
                lore.add(ChatColor.YELLOW + "• +15% к точности");
                lore.add(ChatColor.YELLOW + "• +10% к дальности");
                lore.add(ChatColor.YELLOW + "• -10% к отдаче");
                
                tracerMeta.setLore(lore);
                ammoTracerItem.setItemMeta(tracerMeta);
            }
            
            ItemStack ammoM17StandardItem = new ItemStack(Material.IRON_NUGGET);
            ItemMeta m17StandardMeta = ammoM17StandardItem.getItemMeta();
            
            if (m17StandardMeta != null) {
                m17StandardMeta.setDisplayName(ChatColor.GOLD + "9×19 мм стандартный патрон");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Стандартный патрон для Sig Sauer M17");
                lore.add("");
                
                if (ammoSigM17Standard > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoSigM17Standard + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• Базовый урон");
                lore.add(ChatColor.YELLOW + "• Базовая пробиваемость");
                lore.add(ChatColor.YELLOW + "• Базовая точность");
                
                m17StandardMeta.setLore(lore);
                ammoM17StandardItem.setItemMeta(m17StandardMeta);
            }
            
            ItemStack ammoM17APItem = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta m17APMeta = ammoM17APItem.getItemMeta();
            
            if (m17APMeta != null) {
                m17APMeta.setDisplayName(ChatColor.GOLD + "9×19 мм бронебойный патрон M1152");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Бронебойный патрон для Sig Sauer M17");
                lore.add("");
                
                if (ammoSigM17AP > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoSigM17AP + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• +10% к урону");
                lore.add(ChatColor.YELLOW + "• +20% к пробиваемости брони");
                lore.add(ChatColor.YELLOW + "• -5% к точности");
                
                m17APMeta.setLore(lore);
                ammoM17APItem.setItemMeta(m17APMeta);
            }
            
            ItemStack ammoM17JHPItem = new ItemStack(Material.COPPER_INGOT);
            ItemMeta m17JHPMeta = ammoM17JHPItem.getItemMeta();
            
            if (m17JHPMeta != null) {
                m17JHPMeta.setDisplayName(ChatColor.GOLD + "9×19 мм JHP патрон M1153");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Экспансивный патрон для Sig Sauer M17");
                lore.add("");
                
                if (ammoSigM17JHP > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoSigM17JHP + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• +30% к урону по мягким целям");
                lore.add(ChatColor.YELLOW + "• -25% к пробиваемости брони");
                lore.add(ChatColor.YELLOW + "• +5% к точности");
                
                m17JHPMeta.setLore(lore);
                ammoM17JHPItem.setItemMeta(m17JHPMeta);
            }
            
            ItemStack backItem = new ItemStack(Material.ARROW);
            ItemMeta backMeta = backItem.getItemMeta();
            
            if (backMeta != null) {
                backMeta.setDisplayName(ChatColor.YELLOW + "Назад в главное меню");
                backMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Вернуться в главное меню планшета"));
                backItem.setItemMeta(backMeta);
            }
            
            inventory.setItem(10, ammoStandardItem); 
            inventory.setItem(11, ammoAPItem);       
            inventory.setItem(12, ammoTracerItem);   
            
            inventory.setItem(14, ammoM17StandardItem); 
            inventory.setItem(15, ammoM17APItem);       
            inventory.setItem(16, ammoM17JHPItem);      
            
            inventory.setItem(40, backItem);         
        } else {
            
            ItemStack ammoStandardItem = new ItemStack(Material.IRON_NUGGET);
            ItemMeta standardMeta = ammoStandardItem.getItemMeta();
            
            if (standardMeta != null) {
                standardMeta.setDisplayName(ChatColor.GOLD + "5.45×39 мм стандартный патрон");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Стандартный патрон для АК-12");
                lore.add("");
                
                if (ammoAK12Standard > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoAK12Standard + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• Базовый урон");
                lore.add(ChatColor.YELLOW + "• Базовая пробиваемость");
                lore.add(ChatColor.YELLOW + "• Базовая точность");
                
                standardMeta.setLore(lore);
                ammoStandardItem.setItemMeta(standardMeta);
            }
            
            ItemStack ammoAPItem = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta apMeta = ammoAPItem.getItemMeta();
            
            if (apMeta != null) {
                apMeta.setDisplayName(ChatColor.GOLD + "5.45×39 мм бронебойный патрон БС");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Бронебойный патрон для АК-12");
                lore.add("");
                
                if (ammoAK12AP > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoAK12AP + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• +15% к урону");
                lore.add(ChatColor.YELLOW + "• +25% к пробиваемости брони");
                lore.add(ChatColor.YELLOW + "• -10% к точности");
                lore.add(ChatColor.YELLOW + "• +20% к отдаче");
                
                apMeta.setLore(lore);
                ammoAPItem.setItemMeta(apMeta);
            }
            
            ItemStack ammoTracerItem = new ItemStack(Material.GLOWSTONE_DUST);
            ItemMeta tracerMeta = ammoTracerItem.getItemMeta();
            
            if (tracerMeta != null) {
                tracerMeta.setDisplayName(ChatColor.GOLD + "5.45×39 мм трассирующий патрон Т");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Трассирующий патрон для АК-12");
                lore.add("");
                
                if (ammoAK12Tracer > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoAK12Tracer + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• -5% к урону");
                lore.add(ChatColor.YELLOW + "• +10% к точности");
                lore.add(ChatColor.YELLOW + "• +5% к дальности");
                lore.add(ChatColor.YELLOW + "• -5% к отдаче");
                
                tracerMeta.setLore(lore);
                ammoTracerItem.setItemMeta(tracerMeta);
            }
            
            ItemStack ammoSubsonicItem = new ItemStack(Material.PRISMARINE_CRYSTALS);
            ItemMeta subsonicMeta = ammoSubsonicItem.getItemMeta();
            
            if (subsonicMeta != null) {
                subsonicMeta.setDisplayName(ChatColor.GOLD + "5.45×39 мм дозвуковой патрон УС");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Дозвуковой патрон для АК-12");
                lore.add(ChatColor.GRAY + "Идеален для использования с глушителем");
                lore.add("");
                
                if (ammoAK12Subsonic > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoAK12Subsonic + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• -20% к урону");
                lore.add(ChatColor.YELLOW + "• -10% к пробиваемости");
                lore.add(ChatColor.YELLOW + "• +5% к точности");
                lore.add(ChatColor.YELLOW + "• -15% к дальности");
                lore.add(ChatColor.YELLOW + "• -30% к отдаче");
                
                subsonicMeta.setLore(lore);
                ammoSubsonicItem.setItemMeta(subsonicMeta);
            }
            
            ItemStack ammoUdavStandardItem = new ItemStack(Material.IRON_NUGGET);
            ItemMeta udavStandardMeta = ammoUdavStandardItem.getItemMeta();
            
            if (udavStandardMeta != null) {
                udavStandardMeta.setDisplayName(ChatColor.GOLD + "9×21 мм стандартный патрон");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Стандартный патрон для пистолета Удав");
                lore.add("");
                
                if (ammoUdavStandard > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoUdavStandard + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• Базовый урон");
                lore.add(ChatColor.YELLOW + "• Базовая пробиваемость");
                lore.add(ChatColor.YELLOW + "• Базовая точность");
                
                udavStandardMeta.setLore(lore);
                ammoUdavStandardItem.setItemMeta(udavStandardMeta);
            }
            
            ItemStack ammoUdavAPItem = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta udavAPMeta = ammoUdavAPItem.getItemMeta();
            
            if (udavAPMeta != null) {
                udavAPMeta.setDisplayName(ChatColor.GOLD + "9×21 мм бронебойный патрон СП-12");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Бронебойный патрон для пистолета Удав");
                lore.add("");
                
                if (ammoUdavAP > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoUdavAP + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• +15% к урону");
                lore.add(ChatColor.YELLOW + "• +25% к пробиваемости брони");
                lore.add(ChatColor.YELLOW + "• -5% к точности");
                
                udavAPMeta.setLore(lore);
                ammoUdavAPItem.setItemMeta(udavAPMeta);
            }
            
            ItemStack ammoUdavExpansiveItem = new ItemStack(Material.COPPER_INGOT);
            ItemMeta udavExpansiveMeta = ammoUdavExpansiveItem.getItemMeta();
            
            if (udavExpansiveMeta != null) {
                udavExpansiveMeta.setDisplayName(ChatColor.GOLD + "9×21 мм экспансивный патрон");
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Экспансивный патрон для пистолета Удав");
                lore.add("");
                
                if (ammoUdavExpansive > 0) {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.WHITE + ammoUdavExpansive + " шт.");
                } else {
                    lore.add(ChatColor.GRAY + "Доступно: " + ChatColor.RED + "0 шт.");
                }
                
                lore.add("");
                lore.add(ChatColor.GRAY + "Характеристики:");
                lore.add(ChatColor.YELLOW + "• +25% к урону по мягким целям");
                lore.add(ChatColor.YELLOW + "• -20% к пробиваемости брони");
                lore.add(ChatColor.YELLOW + "• +5% к точности");
                
                udavExpansiveMeta.setLore(lore);
                ammoUdavExpansiveItem.setItemMeta(udavExpansiveMeta);
            }
            
            ItemStack backItem = new ItemStack(Material.ARROW);
            ItemMeta backMeta = backItem.getItemMeta();
            
            if (backMeta != null) {
                backMeta.setDisplayName(ChatColor.YELLOW + "Назад в главное меню");
                backMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Вернуться в главное меню планшета"));
                backItem.setItemMeta(backMeta);
            }
            
            inventory.setItem(10, ammoStandardItem);   
            inventory.setItem(11, ammoAPItem);         
            inventory.setItem(12, ammoTracerItem);     
            inventory.setItem(13, ammoSubsonicItem);   
            
            inventory.setItem(15, ammoUdavStandardItem);  
            inventory.setItem(16, ammoUdavAPItem);        
            inventory.setItem(17, ammoUdavExpansiveItem); 
            
            inventory.setItem(40, backItem);              
        }
        
        ItemStack filler = createFillerItem(tabletType);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
        
        player.openInventory(inventory);
    }
    
    /**
     * Открывает информацию о минах
     * @param player игрок, для которого открывается инвентарь
     */
    private void openMinesInfo(Player player) {
        inventory = Bukkit.createInventory(this, 54, "§6§lМины");
        
        Map<String, Mine> mines = plugin.getMineManager().getMines();
        
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("§e§lИнформация о минах");
        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7В этом разделе представлены");
        infoLore.add("§7мины, доступные в игре.");
        infoLore.add("");
        infoLore.add("§7Мины - это взрывные устройства");
        infoLore.add("§7для инженерных работ и защиты");
        infoLore.add("§7стратегических позиций.");
        infoMeta.setLore(infoLore);
        infoItem.setItemMeta(infoMeta);
        
        int slot = 10;
        for (Mine mine : mines.values()) {
            ItemStack mineItem = mine.createItemStack(plugin);
            
            ItemMeta meta = mineItem.getItemMeta();
            List<String> lore = meta.getLore();
            lore.add("");
            lore.add("§eНажмите для подробной информации");
            meta.setLore(lore);
            mineItem.setItemMeta(meta);
            
            inventory.setItem(slot, mineItem);
            slot += 2;
            
            if (slot > 25) break;
        }
        
        inventory.setItem(4, infoItem);
        
        ItemStack backButton = createBackButton();
        inventory.setItem(49, backButton);
        
        player.openInventory(inventory);
    }
    
    /**
     * Обрабатывает клики на элементы инвентаря мин
     * @param event событие клика
     */
    private void handleMinesInfoClick(InventoryClickEvent event) {
        event.setCancelled(true); 
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        if (event.getSlot() == 49) { 
            String tabletType = getTabletTypeForPlayer(player);
            openMainMenu(player, tabletType);
            return;
        }
        
        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
            String mineName = clickedItem.getItemMeta().getDisplayName();
            player.sendMessage("§aИнформация о мине: §f" + mineName);
            
            if (clickedItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "mine_id"), PersistentDataType.STRING)) {
                String mineId = clickedItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "mine_id"), PersistentDataType.STRING);
                Mine mine = plugin.getMineManager().getMine(mineId);
                
                if (mine != null) {
                    player.sendMessage("");
                    player.sendMessage("§e§lХарактеристики мины:");
                    player.sendMessage("§7Урон: §c" + mine.getDamage());
                    player.sendMessage("§7Радиус взрыва: §e" + mine.getBlastRadius() + " м");
                    player.sendMessage("§7Задержка активации: §b" + (mine.getTriggerDelay() / 20.0) + " сек");
                    player.sendMessage("§7Тип: §f" + (mine.isAntiPersonnel() ? "Противопехотная" : "Противотанковая"));
                    player.sendMessage("");
                    
                    if (mineId.equals("mpm3")) {
                        player.sendMessage("§c§lОсобенности МПМ-3 \"Лепесток\":");
                        player.sendMessage("§7• Пластиковый корпус затрудняет обнаружение");
                        player.sendMessage("§7• Быстрое срабатывание (0.25 секунды)");
                        player.sendMessage("§7• Компактный размер позволяет скрытно размещать");
                    }
                    else if (mineId.equals("m7_spider")) {
                        player.sendMessage("§b§lОсобенности XM-7 \"Spider\":");
                        player.sendMessage("§7• Электронная система активации");
                        player.sendMessage("§7• Самодеактивация через час после установки");
                        player.sendMessage("§7• Легко обнаруживается миноискателями");
                    }
                }
            }
            
            player.closeInventory();
        }
    }
    
    /**
     * Создает кнопку "Назад"
     * @return кнопка "Назад"
     */
    private ItemStack createBackButton() {
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Назад в главное меню");
        backMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Вернуться в главное меню планшета"));
        backButton.setItemMeta(backMeta);
        return backButton;
    }
    
    /**
     * Определяет тип планшета для игрока на основе его фракции
     * @param player игрок
     * @return тип планшета (us/ru)
     */
    private String getTabletTypeForPlayer(Player player) {
        if (plugin.getRankManager() != null) {
            String faction = plugin.getRankManager().getPlayerFaction(player);
            if (faction != null) {
                return faction;
            }
        }
        return "ru"; 
    }
} 