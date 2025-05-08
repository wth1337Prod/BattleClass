package me.wth.battleClass.commands;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.armor.Armor;
import me.wth.battleClass.armor.ArmorManager;
import me.wth.battleClass.armor.Boots;
import me.wth.battleClass.armor.BootsManager;
import me.wth.battleClass.armor.Helmet;
import me.wth.battleClass.armor.HelmetManager;
import me.wth.battleClass.armor.Pants;
import me.wth.battleClass.armor.PantsManager;
import me.wth.battleClass.grenades.Grenade;
import me.wth.battleClass.grenades.GrenadeManager;
import me.wth.battleClass.medical.MedicalItem;
import me.wth.battleClass.medical.MedicalManager;
import me.wth.battleClass.medical.InjuryManager;
import me.wth.battleClass.radio.Radio;
import me.wth.battleClass.radio.RadioManager;
import me.wth.battleClass.rations.Ration;
import me.wth.battleClass.rations.RationManager;
import me.wth.battleClass.tablet.TabletManager;
import me.wth.battleClass.weapons.Weapon;
import me.wth.battleClass.weapons.WeaponAttachment;
import me.wth.battleClass.weapons.WeaponManager;
import me.wth.battleClass.weapons.ammo.AmmoManager;
import me.wth.battleClass.weapons.ammo.AmmoType;
import me.wth.battleClass.mines.Mine;
import me.wth.battleClass.mines.MineManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class BattleClassCommand implements CommandExecutor, TabCompleter {
    private final BattleClass plugin;
    private final WeaponManager weaponManager;
    private final AmmoManager ammoManager;
    private final ArmorManager armorManager;
    private final HelmetManager helmetManager;
    private final BootsManager bootsManager;
    private final PantsManager pantsManager;
    private final MedicalManager medicalManager;
    private final GrenadeManager grenadeManager;
    private final RationManager rationManager;
    private final TabletManager tabletManager;
    private final MineManager mineManager;
    private final RadioManager radioManager;
    
    public BattleClassCommand(BattleClass plugin, WeaponManager weaponManager, AmmoManager ammoManager,
                              ArmorManager armorManager, HelmetManager helmetManager, MedicalManager medicalManager, 
                              TabletManager tabletManager, MineManager mineManager) {
        this.plugin = plugin;
        this.weaponManager = weaponManager;
        this.ammoManager = ammoManager;
        this.armorManager = armorManager;
        this.helmetManager = helmetManager;
        this.bootsManager = plugin.getBootsManager();
        this.pantsManager = plugin.getPantsManager();
        this.medicalManager = medicalManager;
        this.grenadeManager = plugin.getGrenadeManager();
        this.rationManager = plugin.getRationManager();
        this.tabletManager = tabletManager;
        this.mineManager = mineManager;
        this.radioManager = plugin.getRadioManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда может быть использована только игроком!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "weapon":
                handleWeaponCommand(player, args);
                break;
            case "attachments":
                handleAttachmentsCommand(player, args);
                break;
            case "ammo":
                handleAmmoCommand(player, args);
                break;
            case "select":
                handleSelectAmmoCommand(player, args);
                break;
            case "list":
                handleListCommand(player);
                break;
            case "armor":
                handleArmorCommand(player, args);
                break;
            case "helmet":
                handleHelmetCommand(player, args);
                break;
            case "boots":
                handleBootsCommand(player, args);
                break;
            case "pants":
                handlePantsCommand(player, args);
                break;
            case "medical":
                handleMedicalCommand(player, args);
                break;
            case "heal":
                handleHealCommand(player, args);
                break;
            case "injury":
                handleInjuryCommand(player, args);
                break;
            case "grenade":
                handleGrenadeCommand(player, args);
                break;
            case "ration":
                handleRationCommand(player, args);
                break;
            case "tablet":
                handleTabletCommand(player, args);
                break;
            case "mine":
                handleMineCommand(player, args);
                break;
            case "radio":
                handleRadioCommand(player, args);
                break;
            case "mortar":
                return handleMortarCommand(sender, args);
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void handleWeaponCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass weapon <id>");
            player.sendMessage("§cДоступные оружия: xm7, ak12");
            return;
        }
        
        String weaponId = args[1].toLowerCase();
        weaponManager.giveWeaponToPlayer(player, weaponId);
    }
    
    private void handleAttachmentsCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass attachments <weapon_id>");
            return;
        }
        
        String weaponId = args[1].toLowerCase();
        Weapon weapon = weaponManager.getWeapon(weaponId);
        
        if (weapon == null) {
            player.sendMessage("§cОружие " + weaponId + " не найдено!");
            return;
        }
        
        player.sendMessage("§6====== Аксессуары для " + weapon.getDisplayName() + " §6======");
        for (WeaponAttachment attachment : weapon.getAvailableAttachments()) {
            player.getInventory().addItem(attachment.createItemStack());
            player.sendMessage("§aВы получили §f" + attachment.getDisplayName());
        }
    }
    
    private void handleListCommand(Player player) {
        player.sendMessage("§6====== Доступное оружие §6======");
        
        Map<String, Weapon> weapons = weaponManager.getWeapons();
        for (Weapon weapon : weapons.values()) {
            player.sendMessage("§a" + weapon.getId() + " §f- " + weapon.getDisplayName());
        }
        
        player.sendMessage("§6====== Доступные бронежилеты §6======");
        
        Map<String, Armor> armors = armorManager.getArmors();
        for (Armor armor : armors.values()) {
            player.sendMessage("§a" + armor.getId() + " §f- " + armor.getDisplayName());
        }
        
        player.sendMessage("§6====== Доступные шлемы §6======");
        
        Map<String, Helmet> helmets = helmetManager.getHelmets();
        for (Helmet helmet : helmets.values()) {
            player.sendMessage("§a" + helmet.getId() + " §f- " + helmet.getDisplayName());
        }
        
        player.sendMessage("§6====== Доступные ботинки §6======");
        
        Map<String, Boots> boots = bootsManager.getAllBoots();
        for (Boots boot : boots.values()) {
            player.sendMessage("§a" + boot.getId() + " §f- " + boot.getDisplayName());
        }
        
        player.sendMessage("§6====== Доступные штаны §6======");
        
        Map<String, Pants> pants = pantsManager.getAllPants();
        for (Pants pant : pants.values()) {
            player.sendMessage("§a" + pant.getId() + " §f- " + pant.getDisplayName());
        }
        
        player.sendMessage("§6====== Доступные медицинские предметы §6======");
        
        Map<String, MedicalItem> medicalItems = medicalManager.getMedicalItems();
        for (MedicalItem medicalItem : medicalItems.values()) {
            player.sendMessage("§a" + medicalItem.getId() + " §f- " + medicalItem.getDisplayName());
        }
        
        player.sendMessage("§6====== Доступные гранаты §6======");
        
        Map<String, Grenade> grenades = grenadeManager.getGrenades();
        for (Grenade grenade : grenades.values()) {
            player.sendMessage("§a" + grenade.getId() + " §f- " + grenade.getDisplayName());
        }
        
        player.sendMessage("§6====== Доступные сухие пайки §6======");
        
        Map<String, Ration> rations = rationManager.getRations();
        for (Ration ration : rations.values()) {
            player.sendMessage("§a" + ration.getId() + " §f- " + ration.getDisplayName());
        }
        
        player.sendMessage("§6====== Доступные мины §6======");
        
        Map<String, Mine> mines = mineManager.getMines();
        for (Mine mine : mines.values()) {
            player.sendMessage("§a" + mine.getId() + " §f- " + mine.getDisplayName());
        }
    }
    
    private void handleAmmoCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§cИспользование: /battleclass ammo <id> [количество]");
            player.sendMessage("§cПримеры: /battleclass ammo xm7_standard 60");
            player.sendMessage("§cДоступные типы патронов:");
            player.sendMessage("§7XM7: §fxm7_standard, xm7_armor_piercing, xm7_tracer");
            player.sendMessage("§7АК-12: §fak12_standard, ak12_armor_piercing, ak12_tracer, ak12_subsonic");
            return;
        }
        
        String ammoId = args[1].toLowerCase();
        int amount = 30; 
        
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                
                if (amount <= 0 || amount > 1000) {
                    player.sendMessage("§cКоличество должно быть от 1 до 1000!");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cНекорректное количество патронов!");
                return;
            }
        }
        
        ammoManager.giveAmmoToPlayer(player, ammoId, amount);
    }
    
    private void handleSelectAmmoCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§cИспользование: /battleclass select <weapon_id> <ammo_id>");
            player.sendMessage("§cПримеры: /battleclass select xm7 xm7_armor_piercing");
            return;
        }
        
        String weaponId = args[1].toLowerCase();
        String ammoId = args[2].toLowerCase();
        
        Weapon weapon = weaponManager.getWeapon(weaponId);
        
        if (weapon == null) {
            player.sendMessage("§cОружие " + weaponId + " не найдено!");
            return;
        }
        
        AmmoType ammoType = ammoManager.getAmmoType(weaponId, ammoId);
        
        if (ammoType == null) {
            player.sendMessage("§cТип патронов " + ammoId + " не существует для оружия " + weaponId + "!");
            return;
        }
        
        int ammoCount = ammoManager.getAmmoCount(player, ammoId);
        
        if (ammoCount <= 0) {
            player.sendMessage("§cУ вас нет патронов типа " + ammoId + ". Сначала получите их с помощью команды /battleclass ammo " + ammoId);
            return;
        }
        
        ammoManager.selectAmmo(player, weaponId, ammoId);
    }
    
    private void handleArmorCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass armor <id>");
            player.sendMessage("§cДоступные бронежилеты: 6b45, iotv_gen4");
            return;
        }
        
        String armorId = args[1].toLowerCase();
        armorManager.giveArmorToPlayer(player, armorId);
    }
    
    private void handleHelmetCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass helmet <id>");
            player.sendMessage("§cДоступные шлемы: 6b47, ech");
            return;
        }
        
        String helmetId = args[1].toLowerCase();
        helmetManager.giveHelmetToPlayer(player, helmetId);
    }
    
    private void handleBootsCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass boots <id>");
            player.sendMessage("§cДоступные ботинки: american_boots_combat, russian_boots_ratnik");
            return;
        }
        
        String bootsId = args[1].toLowerCase();
        bootsManager.giveBootsToPlayer(player, bootsId);
    }
    
    private void handlePantsCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass pants <id>");
            player.sendMessage("§cДоступные штаны: camouflage_pants_forest");
            return;
        }
        
        String pantsId = args[1].toLowerCase();
        pantsManager.givePantsToPlayer(player, pantsId);
    }
    
    private void handleMedicalCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass medical <id> [количество]");
            player.sendMessage("§cДоступные медицинские предметы: bandage, medkit, painkiller, adrenaline");
            return;
        }
        
        String medicalId = args[1].toLowerCase();
        int amount = 1; 
        
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                
                if (amount <= 0 || amount > 64) {
                    player.sendMessage("§cКоличество должно быть от 1 до 64!");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cНекорректное количество предметов!");
                return;
            }
        }
        
        medicalManager.giveMedicalItemToPlayer(player, medicalId, amount);
    }
    
    private void handleHealCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass heal <player>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        
        if (target == null) {
            player.sendMessage("§cИгрок " + args[1] + " не найден!");
            return;
        }
        
        target.setHealth(target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
        
        plugin.getInjuryManager().stopBleeding(target);
        
        plugin.getInjuryManager().healInjury(target);
        
        target.removePotionEffect(org.bukkit.potion.PotionEffectType.POISON);
        target.removePotionEffect(org.bukkit.potion.PotionEffectType.WITHER);
        target.removePotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS);
        target.removePotionEffect(org.bukkit.potion.PotionEffectType.WEAKNESS);
        
        player.sendMessage("§aИгрок " + target.getName() + " полностью вылечен!");
        target.sendMessage("§aВы были полностью вылечены игроком " + player.getName() + "!");
    }
    
    private void handleInjuryCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§cИспользование: /battleclass injury <add|remove> <player> [bleeding|fracture]");
            return;
        }
        
        String action = args[1].toLowerCase();
        Player target = Bukkit.getPlayer(args[2]);
        
        if (target == null) {
            player.sendMessage("§cИгрок " + args[2] + " не найден!");
            return;
        }
        
        InjuryManager injuryManager = plugin.getInjuryManager();
        
        String injuryType = args.length >= 4 ? args[3].toLowerCase() : "all";
        
        switch (action) {
            case "add":
                if (injuryType.equals("bleeding") || injuryType.equals("all")) {
                    injuryManager.applyBleeding(target);
                    player.sendMessage("§aКровотечение добавлено игроку " + target.getName());
                }
                
                if (injuryType.equals("fracture") || injuryType.equals("all")) {
                    injuryManager.applyInjury(target);
                    player.sendMessage("§aПерелом добавлен игроку " + target.getName());
                }
                break;
                
            case "remove":
                if (injuryType.equals("bleeding") || injuryType.equals("all")) {
                    injuryManager.stopBleeding(target);
                    player.sendMessage("§aКровотечение удалено у игрока " + target.getName());
                }
                
                if (injuryType.equals("fracture") || injuryType.equals("all")) {
                    injuryManager.healInjury(target);
                    player.sendMessage("§aПерелом вылечен у игрока " + target.getName());
                }
                break;
                
            default:
                player.sendMessage("§cНеизвестное действие! Используйте add или remove");
                break;
        }
    }
    
    /**
     * Обрабатывает команду получения гранат
     * 
     * @param player игрок, который выполнил команду
     * @param args аргументы команды
     */
    private void handleGrenadeCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass grenade <id> [количество]");
            player.sendMessage("§cДоступные гранаты: m67, rgd5");
            return;
        }
        
        String grenadeId = args[1].toLowerCase();
        int amount = 1; 
        
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                
                if (amount <= 0 || amount > 64) {
                    player.sendMessage("§cКоличество должно быть от 1 до 64!");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cНекорректное количество гранат!");
                return;
            }
        }
        
        grenadeManager.giveGrenadeToPlayer(player, grenadeId, amount);
    }
    
    /**
     * Обрабатывает команду получения сухих пайков
     * 
     * @param player игрок, который выполнил команду
     * @param args аргументы команды
     */
    private void handleRationCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass ration <id> [количество]");
            player.sendMessage("§cДоступные сухие пайки: mre, irp");
            return;
        }
        
        String rationId = args[1].toLowerCase();
        int amount = 1; 
        
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                
                if (amount <= 0 || amount > 64) {
                    player.sendMessage("§cКоличество должно быть от 1 до 64!");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cНекорректное количество предметов!");
                return;
            }
        }
        
        rationManager.giveRationToPlayer(player, rationId, amount);
    }
    
    /**
     * Обрабатывает команду получения военного планшета
     * 
     * @param player игрок, который выполнил команду
     * @param args аргументы команды
     */
    private void handleTabletCommand(Player player, String[] args) {
        if (!player.hasPermission("battleclass.tablet")) {
            player.sendMessage("§cУ вас нет прав на использование этой команды!");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass tablet <us|ru> [игрок]");
            player.sendMessage("§cДоступные планшеты: us (США), ru (Россия)");
            return;
        }
        
        Player targetPlayer = player;
        if (args.length >= 3) {
            String targetName = args[2];
            targetPlayer = Bukkit.getPlayer(targetName);
            
            if (targetPlayer == null) {
                player.sendMessage("§cИгрок " + targetName + " не найден или не в сети!");
                return;
            }
        }
        
        if (targetPlayer != player) {
            if (!player.hasPermission("battleclass.admin")) {
                if (!plugin.getRankManager().canUseTablet(targetPlayer)) {
                    player.sendMessage("§cИгрок " + targetPlayer.getName() + " не имеет достаточный ранг для использования планшета!");
                    
                    if (plugin.getRankManager().getPlayerRank(targetPlayer) != null) {
                        player.sendMessage("§cТекущий ранг игрока: " + 
                                plugin.getRankManager().getPlayerRank(targetPlayer).getFormattedName() + 
                                "§c (необходим командирский ранг)");
                    } else {
                        player.sendMessage("§cУ игрока нет военного ранга. Сначала присвойте ему ранг командира через /rank set");
                    }
                    
                    return;
                }
            }
        } else {
            if (!player.hasPermission("battleclass.admin") && !plugin.getRankManager().canUseTablet(player)) {
                player.sendMessage("§cВы не имеете достаточный ранг для использования планшета!");
                
                if (plugin.getRankManager().getPlayerRank(player) != null) {
                    player.sendMessage("§cВаш текущий ранг: " + 
                            plugin.getRankManager().getPlayerRank(player).getFormattedName() + 
                            "§c (необходим командирский ранг)");
                } else {
                    player.sendMessage("§cУ вас нет военного ранга. Обратитесь к администратору для получения ранга командира.");
                }
                
                return;
            }
        }
        
        String tabletType = args[1].toLowerCase();
        ItemStack tablet = null;
        
        switch (tabletType) {
            case "us":
                tablet = tabletManager.createUSTablet();
                player.sendMessage(targetPlayer == player ? 
                        "§aВы получили §fВоенный планшет США" : 
                        "§aВы выдали §fВоенный планшет США§a игроку " + targetPlayer.getName());
                
                if (targetPlayer != player) {
                    targetPlayer.sendMessage("§aВы получили §fВоенный планшет США§a от " + player.getName());
                }
                break;
            case "ru":
                tablet = tabletManager.createRussianTablet();
                player.sendMessage(targetPlayer == player ? 
                        "§aВы получили §fВоенный планшет РФ" : 
                        "§aВы выдали §fВоенный планшет РФ§a игроку " + targetPlayer.getName());
                
                if (targetPlayer != player) {
                    targetPlayer.sendMessage("§aВы получили §fВоенный планшет РФ§a от " + player.getName());
                }
                break;
            default:
                player.sendMessage("§cНеизвестный тип планшета! Доступны: us (США), ru (Россия)");
                return;
        }
        
        if (tablet != null) {
            targetPlayer.getInventory().addItem(tablet);
        }
    }
    
    /**
     * Обрабатывает команду для получения мин
     * @param player игрок, использующий команду
     * @param args аргументы команды
     */
    private void handleMineCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass mine <id> [количество]");
            player.sendMessage("§cДоступные мины:");
            player.sendMessage("§7• §fmpm3 §7- §fПротивопехотная мина МПМ-3 'Лепесток' (РФ)");
            player.sendMessage("§7• §fm7_spider §7- §fПротивопехотная мина XM-7 'Spider' (США)");
            return;
        }
        
        String mineId = args[1].toLowerCase();
        int amount = 1; 
        
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                
                if (amount <= 0 || amount > 64) {
                    player.sendMessage("§cКоличество должно быть от 1 до 64!");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cНекорректное количество мин!");
                return;
            }
        }
        
        mineManager.giveMineToPlayer(player, mineId, amount);
    }
    
    private void handleRadioCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /battleclass radio <id>");
            
            Map<String, Radio> radios = radioManager.getRadios();
            if (!radios.isEmpty()) {
                player.sendMessage("§aДоступные рации:");
                for (Radio radio : radios.values()) {
                    player.sendMessage("§7- §f" + radio.getId() + " §7- " + radio.getDisplayName());
                }
            } else {
                player.sendMessage("§cДоступных раций не найдено");
            }
            return;
        }
        
        String radioId = args[1].toLowerCase();
        boolean success = radioManager.giveRadioToPlayer(player, radioId);
        
        if (!success) {
            player.sendMessage("§cРация §f" + radioId + " §cне найдена!");
            player.sendMessage("§cДоступные рации: " + String.join(", ", radioManager.getRadioIds()));
        }
    }
    
    /**
     * Выдает миномет игроку в зависимости от его фракции
     * 
     * @param sender отправитель команды
     * @param args аргументы команды
     * @return true, если команда успешно выполнена
     */
    private boolean handleMortarCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("battleclass.mortar")) {
            sender.sendMessage(ChatColor.RED + "У вас нет разрешения на использование этой команды.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /battleclass mortar <игрок>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + args[1] + " не найден.");
            return true;
        }
        
        String factionId = plugin.getRankManager().getPlayerFaction(target.getUniqueId());
        if (factionId == null) {
            sender.sendMessage(ChatColor.RED + "У игрока " + target.getName() + " не установлена фракция.");
            return true;
        }
        
        me.wth.battleClass.ranks.Faction faction = me.wth.battleClass.ranks.Faction.getByID(factionId);
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "Неизвестная фракция: " + factionId);
            return true;
        }
        
        ItemStack mortarItem = plugin.getMortarManager().createMortarForFaction(faction);
        if (mortarItem == null) {
            sender.sendMessage(ChatColor.RED + "Не удалось создать миномет для фракции " + faction.getDisplayName());
            return true;
        }
        
        target.getInventory().addItem(mortarItem);
        
        sender.sendMessage(ChatColor.GREEN + "Игроку " + target.getName() + " выдан миномет фракции " + faction.getDisplayName());
        target.sendMessage(ChatColor.GREEN + "Вы получили миномет вашей фракции.");
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage("§6====== BattleClass команды §6======");
        player.sendMessage("§a/battleclass weapon <id> §7- получить оружие");
        player.sendMessage("§a/battleclass attachments <weapon_id> §7- получить все аксессуары для оружия");
        player.sendMessage("§a/battleclass ammo <type> [amount] §7- получить патроны");
        player.sendMessage("§a/battleclass select <type> §7- выбрать тип патронов");
        player.sendMessage("§a/battleclass list §7- список доступного снаряжения");
        player.sendMessage("§a/battleclass armor <id> §7- получить бронежилет");
        player.sendMessage("§a/battleclass helmet <id> §7- получить шлем");
        player.sendMessage("§a/battleclass boots <id> §7- получить ботинки");
        player.sendMessage("§a/battleclass pants <id> §7- получить штаны");
        player.sendMessage("§a/battleclass medical <id> [amount] §7- получить медицинский предмет");
        player.sendMessage("§a/battleclass heal [player] §7- вылечить игрока");
        player.sendMessage("§a/battleclass injury <add|remove|list> [тип] [игрок] §7- управление травмами");
        player.sendMessage("§a/battleclass grenade <id> [amount] §7- получить гранату");
        player.sendMessage("§a/battleclass ration <id> [amount] §7- получить паёк");
        player.sendMessage("§a/battleclass tablet §7- получить военный планшет");
        player.sendMessage("§a/battleclass mine <id> [amount] §7- получить мину");
        player.sendMessage("§a/battleclass radio <id> §7- получить рацию");
        player.sendMessage(ChatColor.GREEN + "/battleclass mortar <игрок> " + 
                           ChatColor.GRAY + "- Выдать миномет игроку (в зависимости от его фракции)");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> commands = Arrays.asList("weapon", "attachments", "ammo", "select", "list", 
                    "armor", "helmet", "boots", "pants", "medical", "heal", "injury", "grenade", 
                    "ration", "tablet", "mine", "radio", "mortar");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "weapon":
                    StringUtil.copyPartialMatches(args[1], weaponManager.getWeapons().keySet(), completions);
                    break;
                case "attachments":
                    StringUtil.copyPartialMatches(args[1], weaponManager.getWeapons().keySet(), completions);
                    break;
                case "ammo":
                    StringUtil.copyPartialMatches(args[1], ammoManager.getAmmoTypeNames(), completions);
                    break;
                case "select":
                    StringUtil.copyPartialMatches(args[1], weaponManager.getWeapons().keySet(), completions);
                    break;
                case "armor":
                    StringUtil.copyPartialMatches(args[1], armorManager.getArmors().keySet(), completions);
                    break;
                case "helmet":
                    StringUtil.copyPartialMatches(args[1], helmetManager.getHelmets().keySet(), completions);
                    break;
                case "boots":
                    StringUtil.copyPartialMatches(args[1], bootsManager.getBootsIds(), completions);
                    break;
                case "pants":
                    StringUtil.copyPartialMatches(args[1], pantsManager.getPantsIds(), completions);
                    break;
                case "medical":
                    StringUtil.copyPartialMatches(args[1], medicalManager.getMedicalItems().keySet(), completions);
                    break;
                case "injury":
                    StringUtil.copyPartialMatches(args[1], Arrays.asList("add", "remove", "list"), completions);
                    break;
                case "grenade":
                    StringUtil.copyPartialMatches(args[1], grenadeManager.getGrenades().keySet(), completions);
                    break;
                case "ration":
                    StringUtil.copyPartialMatches(args[1], rationManager.getRations().keySet(), completions);
                    break;
                case "mine":
                    StringUtil.copyPartialMatches(args[1], mineManager.getMines().keySet(), completions);
                    break;
                case "radio":
                    StringUtil.copyPartialMatches(args[1], radioManager.getRadioIds(), completions);
                    break;
                case "mortar":
                    if (args.length == 2) {
                        return null; 
                    }
                    break;
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            String secondArg = args[1].toLowerCase();
            
            if (subCommand.equals("select")) {
                StringUtil.copyPartialMatches(args[2], ammoManager.getAmmoTypeNames(), completions);
            }
        }
        
        return completions;
    }
}