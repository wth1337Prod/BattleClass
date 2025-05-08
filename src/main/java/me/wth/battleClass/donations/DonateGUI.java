package me.wth.battleClass.donations;

import me.wth.battleClass.BattleClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Класс для отображения GUI-меню доната
 */
public class DonateGUI implements InventoryHolder {
    private final BattleClass plugin;
    private Inventory inventory;
    
    private final Map<UUID, String> openMenuType = new HashMap<>();
    
    private final Map<UUID, Integer> animationTasks = new HashMap<>();
    
    private final String[] usRanks = {
        "private", "corporal", "sergeant", "lieutenant", "captain", 
        "major", "colonel", "general"
    };
    
    private final String[] ruRanks = {
        "ryadovoy", "efreitor", "serzhant", "leytenant", "kapitan", 
        "mayor", "polkovnik", "general_armii"
    };
    
    /**
     * Конструктор GUI для донатов
     * 
     * @param plugin экземпляр основного класса плагина
     */
    public DonateGUI(BattleClass plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * Открывает главное меню выбора фракции для доната
     * 
     * @param player игрок, для которого открывается меню
     */
    public void openMainDonateMenu(Player player) {
        inventory = Bukkit.createInventory(this, 54, ChatColor.GOLD + "BattleClass - Донат");
        
        openMenuType.put(player.getUniqueId(), "main");
        
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.GOLD + "Информация о донате");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Выберите фракцию, которой");
            lore.add(ChatColor.GRAY + "вы хотите помочь.");
            lore.add("");
            lore.add(ChatColor.GRAY + "Все собранные средства идут");
            lore.add(ChatColor.GRAY + "на развитие сервера.");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Сайт: " + ChatColor.GREEN + "battleclass.ru");
            infoMeta.setLore(lore);
            infoItem.setItemMeta(infoMeta);
        }
        
        ItemStack usItem = new ItemStack(Material.BLUE_BANNER);
        ItemMeta usMeta = usItem.getItemMeta();
        if (usMeta != null) {
            usMeta.setDisplayName(ChatColor.BLUE + "США");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Выберите для получения");
            lore.add(ChatColor.GRAY + "привилегий в армии США");
            lore.add("");
            lore.add(ChatColor.YELLOW + "• Синяя форма");
            lore.add(ChatColor.YELLOW + "• Доступ к уникальному оружию");
            lore.add(ChatColor.YELLOW + "• Военная техника");
            lore.add("");
            lore.add(ChatColor.AQUA + "Нажмите, чтобы выбрать");
            usMeta.setLore(lore);
            usMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            usItem.setItemMeta(usMeta);
        }
        
        ItemStack ruItem = new ItemStack(Material.RED_BANNER);
        ItemMeta ruMeta = ruItem.getItemMeta();
        if (ruMeta != null) {
            ruMeta.setDisplayName(ChatColor.RED + "Россия");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Выберите для получения");
            lore.add(ChatColor.GRAY + "привилегий в армии России");
            lore.add("");
            lore.add(ChatColor.YELLOW + "• Красная форма");
            lore.add(ChatColor.YELLOW + "• Доступ к уникальному оружию");
            lore.add(ChatColor.YELLOW + "• Военная техника");
            lore.add("");
            lore.add(ChatColor.AQUA + "Нажмите, чтобы выбрать");
            ruMeta.setLore(lore);
            ruMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            ruItem.setItemMeta(ruMeta);
        }
        
        inventory.setItem(4, infoItem);
        inventory.setItem(20, usItem);
        inventory.setItem(24, ruItem);
        
        fillEmptySlots(inventory, Material.GRAY_STAINED_GLASS_PANE);
        
        startMenuAnimation(player);
        
        player.openInventory(inventory);
    }
    
    /**
     * Запускает анимацию меню
     * 
     * @param player игрок, для которого запускается анимация
     */
    private void startMenuAnimation(Player player) {
        stopMenuAnimation(player);
        
        int taskId = new BukkitRunnable() {
            private int tick = 0;
            private final Material[] usColors = {
                Material.BLUE_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.CYAN_STAINED_GLASS_PANE
            };
            
            private final Material[] ruColors = {
                Material.RED_STAINED_GLASS_PANE,
                Material.PINK_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE
            };
            
            @Override
            public void run() {
                if (player == null || !player.isOnline() || !openMenuType.containsKey(player.getUniqueId())) {
                    cancel();
                    animationTasks.remove(player.getUniqueId());
                    return;
                }
                
                String menuType = openMenuType.get(player.getUniqueId());
                tick = (tick + 1) % 15;
                
                if (menuType.equals("main")) {
                    animateMainMenu(tick);
                } else if (menuType.equals("us")) {
                    animateFactionMenu(tick, true);
                } else if (menuType.equals("ru")) {
                    animateFactionMenu(tick, false);
                }
            }
            
            private void animateMainMenu(int tick) {
                if (inventory == null) return;
                
                if (tick % 3 == 0) {
                    for (int i = 0; i < 9; i++) {
                        updateBorderItem(i, usColors[tick % 3]);
                        updateBorderItem(9 * i, usColors[(tick + 1) % 3]);
                        updateBorderItem(8 + 9 * i, ruColors[tick % 3]);
                        updateBorderItem(45 + i, ruColors[(tick + 2) % 3]);
                    }
                }
            }
            
            private void animateFactionMenu(int tick, boolean isUS) {
                if (inventory == null) return;
                
                Material[] colors = isUS ? usColors : ruColors;
                
                if (tick % 3 == 0) {
                    for (int i = 0; i < 9; i++) {
                        updateBorderItem(i, colors[tick % 3]);
                        updateBorderItem(9 * i, colors[(tick + 1) % 3]);
                        updateBorderItem(8 + 9 * i, colors[tick % 3]);
                        updateBorderItem(45 + i, colors[(tick + 2) % 3]);
                    }
                }
            }
            
            private void updateBorderItem(int slot, Material material) {
                if (inventory.getItem(slot) != null && 
                    inventory.getItem(slot).getType().toString().contains("STAINED_GLASS_PANE")) {
                    ItemStack item = new ItemStack(material);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(" ");
                        item.setItemMeta(meta);
                    }
                    inventory.setItem(slot, item);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L).getTaskId();
        
        animationTasks.put(player.getUniqueId(), taskId);
    }
    
    /**
     * Останавливает анимацию меню
     * 
     * @param player игрок
     */
    public void stopMenuAnimation(Player player) {
        UUID playerId = player.getUniqueId();
        if (animationTasks.containsKey(playerId)) {
            Bukkit.getScheduler().cancelTask(animationTasks.get(playerId));
            animationTasks.remove(playerId);
        }
    }
    
    /**
     * Открывает меню доната для выбранной фракции
     * 
     * @param player игрок
     * @param faction фракция ("us" или "ru")
     */
    public void openFactionDonateMenu(Player player, String faction) {
        boolean isUS = faction.equals("us");
        String title = isUS ? 
                ChatColor.BLUE + "Донат - Армия США" : 
                ChatColor.RED + "Донат - Армия России";
        
        inventory = Bukkit.createInventory(this, 54, title);
        
        openMenuType.put(player.getUniqueId(), faction);
        
        String[] rankList = isUS ? usRanks : ruRanks;
        
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName((isUS ? ChatColor.BLUE : ChatColor.RED) + "Информация о донатах");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Выберите желаемый ранг");
            lore.add(ChatColor.GRAY + "для просмотра привилегий.");
            lore.add("");
            lore.add(ChatColor.GRAY + "ЛКМ - просмотр информации");
            lore.add(ChatColor.GRAY + "ПКМ - просмотр набора предметов");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Сайт: " + ChatColor.GREEN + "battleclass.ru");
            infoMeta.setLore(lore);
            infoItem.setItemMeta(infoMeta);
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Назад");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Вернуться к выбору фракции");
            backMeta.setLore(lore);
            backItem.setItemMeta(backMeta);
        }
        
        int slot = 19;
        for (String rankId : rankList) {
            ItemStack rankItem = createRankItem(rankId, faction);
            inventory.setItem(slot, rankItem);
            
            slot += (slot % 9 == 7) ? 3 : 1;
        }
        
        inventory.setItem(4, infoItem);
        inventory.setItem(49, backItem);
        
        Material fillMaterial = isUS ? 
                Material.BLUE_STAINED_GLASS_PANE : 
                Material.RED_STAINED_GLASS_PANE;
        fillEmptySlots(inventory, fillMaterial);
        
        startMenuAnimation(player);
        
        player.openInventory(inventory);
    }
    
    /**
     * Создает предмет для ранга в меню доната
     * 
     * @param rankId идентификатор ранга
     * @param faction фракция
     * @return предмет для меню
     */
    private ItemStack createRankItem(String rankId, String faction) {
        boolean isUS = faction.equals("us");
        
        Material material;
        String displayName;
        List<String> privileges = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        String kitInfo;
        
        switch (rankId) {
            case "private":
                material = Material.IRON_INGOT;
                displayName = "Рядовой";
                privileges.add("• Базовый набор снаряжения");
                privileges.add("• Доступ к общей казарме");
                commands.add("/battleclass weapon sigsauerm17");
                commands.add("/battleclass ammo sigsauerm17_ammo 64");
                kitInfo = "Стартовый комплект военного США: \nПистолет SigSauerM17, патроны для пистолета";
                break;
            case "corporal":
                material = Material.IRON_SWORD;
                displayName = "Капрал";
                privileges.add("• Улучшенное оружие");
                privileges.add("• Доступ к тренировочной площадке");
                privileges.add("• +1 набор первой помощи");
                commands.add("/battleclass weapon sigsauerm17");
                commands.add("/battleclass ammo sigsauerm17_ammo 128");
                commands.add("/battleclass grenade m67 2");
                commands.add("/battleclass medical bandage 5");
                kitInfo = "Улучшенный комплект: \nПистолет SigSauerM17, гранаты M67, бинты, американский бронежилет";
                break;
            case "sergeant":
                material = Material.GOLDEN_SWORD;
                displayName = "Сержант";
                privileges.add("• Командование отделением");
                privileges.add("• Доступ к транспорту");
                privileges.add("• Расширенный арсенал");
                commands.add("/battleclass weapon xm7");
                commands.add("/battleclass ammo xm7_ammo 128");
                commands.add("/battleclass weapon sigsauerm17");
                commands.add("/battleclass ammo sigsauerm17_ammo 64");
                commands.add("/battleclass armor americanvestiotv 100");
                kitInfo = "Комплект сержанта: \nВинтовка XM7, пистолет SigSauerM17, американский бронежилет";
                break;
            case "lieutenant":
                material = Material.DIAMOND_SWORD;
                displayName = "Лейтенант";
                privileges.add("• Планшет командира");
                privileges.add("• Личные апартаменты");
                privileges.add("• Расширенный набор медикаментов");
                commands.add("/battleclass weapon xm7");
                commands.add("/battleclass ammo xm7_ammo 256");
                commands.add("/battleclass grenade m67 3");
                commands.add("/battleclass medical medkit 2");
                commands.add("/battleclass tablet us");
                kitInfo = "Офицерский комплект: \nВинтовка XM7, медкомплект, аптечка, планшет, гранаты M67";
                break;
            case "captain":
                material = Material.GOLDEN_HELMET;
                displayName = "Капитан";
                privileges.add("• Доступ к вертолетам");
                privileges.add("• Координация атак");
                privileges.add("• Тактическое преимущество");
                commands.add("/battleclass weapon xm7");
                commands.add("/battleclass ammo xm7_ammo 512");
                commands.add("/battleclass weapon sigsauerm17");
                commands.add("/battleclass ammo sigsauerm17_ammo 128");
                commands.add("/battleclass tablet us");
                commands.add("/battleclass armor americanvestiotv 200");
                kitInfo = "Комплект капитана: \nПолный офицерский набор XM7, SigSauerM17, американский бронежилет, планшет";
                break;
            case "major":
                material = Material.DIAMOND_HELMET;
                displayName = "Майор";
                privileges.add("• Вызов поддержки");
                privileges.add("• Расширенный арсенал оружия");
                privileges.add("• Личный транспорт");
                commands.add("/battleclass weapon xm7");
                commands.add("/battleclass ammo xm7_ammo 512");
                commands.add("/battleclass attachments scope1 1");
                commands.add("/battleclass mine claymore 3");
                commands.add("/battleclass tablet us");
                kitInfo = "Комплект майора: \nXM7 с прицелом, мины, личное транспортное средство, планшет высшего офицера";
                break;
            case "colonel":
                material = Material.NETHERITE_HELMET;
                displayName = "Полковник";
                privileges.add("• Командование базой");
                privileges.add("• Тактические удары");
                privileges.add("• Расширенное снаряжение");
                commands.add("/battleclass weapon xm7");
                commands.add("/battleclass ammo xm7_ammo 1024");
                commands.add("/battleclass attachments scope1 1");
                commands.add("/battleclass attachments grip1 1");
                commands.add("/battleclass helmet us_helmet 150");
                commands.add("/battleclass armor americanvestiotv 300");
                kitInfo = "Комплект полковника: \nСпециальное снаряжение, XM7 с полным набором модификаций, американский шлем высшей защиты";
                break;
            case "general":
                material = Material.NETHER_STAR;
                displayName = "Генерал";
                privileges.add("• Полный контроль армии");
                privileges.add("• Неограниченный арсенал");
                privileges.add("• Стратегические возможности");
                commands.add("/battleclass weapon xm7");
                commands.add("/battleclass ammo xm7_ammo 2048");
                commands.add("/battleclass attachments scope1 1");
                commands.add("/battleclass attachments grip1 1");
                commands.add("/battleclass attachments laser1 1");
                commands.add("/battleclass helmet us_helmet 300");
                commands.add("/battleclass armor americanvestiotv 400");
                commands.add("/battleclass boots us_boots 300");
                commands.add("/battleclass pants us_pants 300");
                kitInfo = "Генеральский комплект: \nПолное элитное снаряжение армии США, XM7 со всеми модификациями, неограниченный доступ ко всем военным объектам";
                break;
                
            case "ryadovoy":
                material = Material.IRON_INGOT;
                displayName = "Рядовой";
                privileges.add("• Базовый набор снаряжения");
                privileges.add("• Доступ к общей казарме");
                commands.add("/battleclass weapon udav");
                commands.add("/battleclass ammo udav_ammo 64");
                kitInfo = "Стартовый комплект военного РФ: \nПистолет Удав, патроны для пистолета";
                break;
            case "efreitor":
                material = Material.IRON_SWORD;
                displayName = "Ефрейтор";
                privileges.add("• Улучшенное оружие");
                privileges.add("• Доступ к тренировочной площадке");
                privileges.add("• +1 набор первой помощи");
                commands.add("/battleclass weapon udav");
                commands.add("/battleclass ammo udav_ammo 128");
                commands.add("/battleclass grenade rgd 2");
                commands.add("/battleclass medical bandage 5");
                kitInfo = "Улучшенный комплект: \nПистолет Удав, гранаты РГД, бинты, российский бронежилет";
                break;
            case "serzhant":
                material = Material.GOLDEN_SWORD;
                displayName = "Сержант";
                privileges.add("• Командование отделением");
                privileges.add("• Доступ к транспорту");
                privileges.add("• Расширенный арсенал");
                commands.add("/battleclass weapon ak12");
                commands.add("/battleclass ammo ak12_ammo 128");
                commands.add("/battleclass weapon udav");
                commands.add("/battleclass ammo udav_ammo 64");
                commands.add("/battleclass armor russianvest6b45 100");
                kitInfo = "Комплект сержанта: \nАК-12, пистолет Удав, российский бронежилет 6Б45";
                break;
            case "leytenant":
                material = Material.DIAMOND_SWORD;
                displayName = "Лейтенант";
                privileges.add("• Планшет командира");
                privileges.add("• Личные апартаменты");
                privileges.add("• Расширенный набор медикаментов");
                commands.add("/battleclass weapon ak12");
                commands.add("/battleclass ammo ak12_ammo 256");
                commands.add("/battleclass grenade rgd 3");
                commands.add("/battleclass medical medkit 2");
                commands.add("/battleclass tablet ru");
                kitInfo = "Офицерский комплект: \nАК-12, медкомплект, аптечка, планшет, гранаты РГД";
                break;
            case "kapitan":
                material = Material.GOLDEN_HELMET;
                displayName = "Капитан";
                privileges.add("• Доступ к вертолетам");
                privileges.add("• Координация атак");
                privileges.add("• Тактическое преимущество");
                commands.add("/battleclass weapon ak12");
                commands.add("/battleclass ammo ak12_ammo 512");
                commands.add("/battleclass weapon udav");
                commands.add("/battleclass ammo udav_ammo 128");
                commands.add("/battleclass tablet ru");
                commands.add("/battleclass armor russianvest6b45 200");
                kitInfo = "Комплект капитана: \nПолный офицерский набор АК-12, Удав, российский бронежилет, планшет";
                break;
            case "mayor":
                material = Material.DIAMOND_HELMET;
                displayName = "Майор";
                privileges.add("• Вызов поддержки");
                privileges.add("• Расширенный арсенал оружия");
                privileges.add("• Личный транспорт");
                commands.add("/battleclass weapon ak12");
                commands.add("/battleclass ammo ak12_ammo 512");
                commands.add("/battleclass attachments scope1 1");
                commands.add("/battleclass mine pmn 3");
                commands.add("/battleclass tablet ru");
                kitInfo = "Комплект майора: \nАК-12 с прицелом, мины, личное транспортное средство, планшет высшего офицера";
                break;
            case "polkovnik":
                material = Material.NETHERITE_HELMET;
                displayName = "Полковник";
                privileges.add("• Командование базой");
                privileges.add("• Тактические удары");
                privileges.add("• Расширенное снаряжение");
                commands.add("/battleclass weapon ak12");
                commands.add("/battleclass ammo ak12_ammo 1024");
                commands.add("/battleclass attachments scope1 1");
                commands.add("/battleclass attachments grip1 1");
                commands.add("/battleclass helmet ru_helmet 150");
                commands.add("/battleclass armor russianvest6b45 300");
                kitInfo = "Комплект полковника: \nСпециальное снаряжение, АК-12 с полным набором модификаций, российский шлем высшей защиты";
                break;
            case "general_armii":
                material = Material.NETHER_STAR;
                displayName = "Генерал армии";
                privileges.add("• Полный контроль армии");
                privileges.add("• Неограниченный арсенал");
                privileges.add("• Стратегический удар");
                commands.add("/kit general_armii");
                commands.add("/army control");
                commands.add("/strike strategic");
                kitInfo = "Комплект генерала армии: \nэлитное снаряжение, шифровальное устройство, доступ ко всем видам техники и оружия";
                break;
            default:
                material = Material.PAPER;
                displayName = "Неизвестный ранг";
                privileges.add("• Информация недоступна");
                commands.add("Нет доступных команд");
                kitInfo = "Информация о наборе недоступна";
                break;
        }
        
        ChatColor rankColor = isUS ? ChatColor.BLUE : ChatColor.RED;
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(rankColor + displayName);
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Привилегии:");
            
            for (String privilege : privileges) {
                lore.add(ChatColor.YELLOW + privilege);
            }
            
            lore.add("");
            lore.add(ChatColor.GRAY + "Доступные команды:");
            
            for (String cmd : commands) {
                lore.add(ChatColor.GREEN + cmd);
            }
            
            lore.add("");
            lore.add(ChatColor.AQUA + "ЛКМ - подробная информация");
            lore.add(ChatColor.AQUA + "ПКМ - просмотр набора предметов");
            
            lore.add(ChatColor.BLACK + "kit_info:" + kitInfo);
            
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Открывает подробную информацию о наборе предметов для ранга
     * 
     * @param player игрок
     * @param clickedItem предмет, по которому кликнули
     */
    public void openKitInfo(Player player, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getItemMeta() == null) return;
        
        ItemMeta meta = clickedItem.getItemMeta();
        List<String> lore = meta.getLore();
        
        if (lore == null || lore.isEmpty()) return;
        
        String kitInfo = "";
        for (String line : lore) {
            if (line.startsWith(ChatColor.BLACK + "kit_info:")) {
                kitInfo = line.substring((ChatColor.BLACK + "kit_info:").length());
                break;
            }
        }
        
        if (kitInfo.isEmpty()) return;
        
        String faction = openMenuType.getOrDefault(player.getUniqueId(), "main");
        if (faction.equals("main")) return;
        
        boolean isUS = faction.equals("us");
        ChatColor mainColor = isUS ? ChatColor.BLUE : ChatColor.RED;
        
        String title = mainColor + "Набор: " + meta.getDisplayName().substring(2);
        inventory = Bukkit.createInventory(this, 27, title);
        
        ItemStack infoItem = new ItemStack(Material.CHEST);
        ItemMeta infoMeta = infoItem.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(mainColor + "Содержимое набора");
            
            List<String> infoLore = new ArrayList<>();
            infoLore.add(ChatColor.GRAY + "В этот набор входит:");
            infoLore.add("");
            
            for (String line : kitInfo.split("\\n")) {
                infoLore.add(ChatColor.YELLOW + line);
            }
            
            infoLore.add("");
            infoLore.add(ChatColor.GREEN + "Для покупки перейдите на сайт:");
            infoLore.add(ChatColor.AQUA + "battleclass.ru");
            
            infoMeta.setLore(infoLore);
            infoItem.setItemMeta(infoMeta);
        }
        
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Назад");
            List<String> backLore = new ArrayList<>();
            backLore.add(ChatColor.GRAY + "Вернуться к выбору рангов");
            backMeta.setLore(backLore);
            backItem.setItemMeta(backMeta);
        }
        
        inventory.setItem(13, infoItem);
        inventory.setItem(22, backItem);
        
        Material fillMaterial = isUS ? 
                Material.BLUE_STAINED_GLASS_PANE : 
                Material.RED_STAINED_GLASS_PANE;
        fillEmptySlots(inventory, fillMaterial);
        
        player.openInventory(inventory);
    }
    
    /**
     * Заполняет пустые слоты в инвентаре декоративными панелями
     * 
     * @param inv инвентарь для заполнения
     * @param material материал для заполнения
     */
    private void fillEmptySlots(Inventory inv, Material material) {
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }
        
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, filler);
            }
        }
    }
    
    /**
     * Удаляет запись об открытом меню при закрытии инвентаря
     * 
     * @param player игрок
     */
    public void handleInventoryClose(Player player) {
        stopMenuAnimation(player);
        openMenuType.remove(player.getUniqueId());
    }
    
    /**
     * Обрабатывает клик по элементу в меню доната
     * 
     * @param player игрок, который кликнул
     * @param clickedItem предмет, по которому кликнули
     * @param isRightClick была ли нажата правая кнопка мыши
     * @return true, если клик был обработан
     */
    public boolean handleInventoryClick(Player player, ItemStack clickedItem, boolean isRightClick) {
        if (clickedItem == null || clickedItem.getItemMeta() == null) {
            return false;
        }
        
        String menuType = openMenuType.getOrDefault(player.getUniqueId(), "main");
        String itemName = clickedItem.getItemMeta().getDisplayName();
        
        if (menuType.equals("main")) {
            if (itemName.equals(ChatColor.BLUE + "США")) {
                openFactionDonateMenu(player, "us");
                return true;
            } else if (itemName.equals(ChatColor.RED + "Россия")) {
                openFactionDonateMenu(player, "ru");
                return true;
            }
        } else if (menuType.equals("us") || menuType.equals("ru")) {
            if (itemName.equals(ChatColor.YELLOW + "Назад")) {
                openMainDonateMenu(player);
                return true;
            } else if (isRightClick && clickedItem.getItemMeta().hasLore()) {
                openKitInfo(player, clickedItem);
                return true;
            }
        } else {
            if (itemName.equals(ChatColor.YELLOW + "Назад")) {
                openFactionDonateMenu(player, menuType);
                return true;
            }
        }
        
        return false;
    }
} 