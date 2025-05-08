package me.wth.battleClass.commands;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.drones.DroneManager;
import me.wth.battleClass.drones.DroneListener;
import me.wth.battleClass.ranks.Faction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Команда для управления дронами
 */
public class DroneCommand implements CommandExecutor, TabCompleter {
    private final BattleClass plugin;
    private final DroneManager droneManager;
    private final DroneListener droneListener;
    
    /**
     * Конструктор команды дронов
     * 
     * @param plugin экземпляр основного плагина
     * @param droneManager менеджер дронов
     */
    public DroneCommand(BattleClass plugin, DroneManager droneManager) {
        this.plugin = plugin;
        this.droneManager = droneManager;
        this.droneListener = plugin.getDroneListener();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "give":
                return handleGiveCommand(sender, args);
            case "battery":
                return handleBatteryCommand(sender, args);
            case "key":
                return handleKeyCommand(sender, args);
            default:
                sendHelpMessage(sender);
                return true;
        }
    }
    
    /**
     * Обработка команды выдачи дрона
     * 
     * @param sender отправитель команды
     * @param args аргументы команды
     * @return успех выполнения команды
     */
    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("battleclass.drone.give")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды!");
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Использование: /drone give <игрок> <тип>");
            sender.sendMessage(ChatColor.RED + "Типы: lancet, predator");
            return true;
        }
        
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден!");
            return true;
        }
        
        String factionId = plugin.getRankManager().getPlayerFaction(targetPlayer.getUniqueId());
        Faction playerFaction = Faction.getByID(factionId);
        
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не принадлежит ни к одной фракции!");
            return true;
        }
        
        String droneType = args[2].toLowerCase();
        ItemStack droneItem = null;
        
        switch (droneType) {
            case "lancet":
                if (playerFaction != Faction.RUSSIA) {
                    sender.sendMessage(ChatColor.RED + "Дрон-камикадзе \"Ланцет\" доступен только для фракции РФ!");
                    return true;
                }
                droneItem = droneManager.createDroneForFaction(Faction.RUSSIA, true);
                break;
            case "predator":
                if (playerFaction != Faction.USA) {
                    sender.sendMessage(ChatColor.RED + "Боевой дрон \"Predator\" доступен только для фракции США!");
                    return true;
                }
                droneItem = droneManager.createDroneForFaction(Faction.USA, false);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Неизвестный тип дрона! Доступные типы: lancet, predator");
                return true;
        }
        
        if (droneItem != null) {
            targetPlayer.getInventory().addItem(droneItem);
            
            targetPlayer.sendMessage(ChatColor.GREEN + "Вы получили дрон!");
            if (sender != targetPlayer) {
                sender.sendMessage(ChatColor.GREEN + "Дрон выдан игроку " + targetPlayer.getName());
            }
        }
        
        return true;
    }
    
    /**
     * Обработка команды выдачи батареи
     * 
     * @param sender отправитель команды
     * @param args аргументы команды
     * @return успех выполнения команды
     */
    private boolean handleBatteryCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("battleclass.drone.battery")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды!");
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Использование: /drone battery <игрок> <заряд>");
            return true;
        }
        
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден!");
            return true;
        }
        
        int charge;
        try {
            charge = Integer.parseInt(args[2]);
            if (charge <= 0) {
                sender.sendMessage(ChatColor.RED + "Заряд должен быть положительным числом!");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Неверный формат заряда! Укажите целое число.");
            return true;
        }
        
        ItemStack batteryItem = droneManager.createDroneBattery(charge);
        targetPlayer.getInventory().addItem(batteryItem);
        
        targetPlayer.sendMessage(ChatColor.GREEN + "Вы получили батарею для дрона с зарядом " + charge + "!");
        if (sender != targetPlayer) {
            sender.sendMessage(ChatColor.GREEN + "Батарея выдана игроку " + targetPlayer.getName());
        }
        
        return true;
    }
    
    /**
     * Обработка команды симуляции нажатия клавиш для управления дроном
     * 
     * @param sender отправитель команды
     * @param args аргументы команды
     * @return успех выполнения команды
     */
    private boolean handleKeyCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эта команда доступна только для игроков!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Использование: /drone key <клавиша> <нажата>");
            player.sendMessage(ChatColor.RED + "Клавиши: W, A, S, D, SPACE, SHIFT");
            player.sendMessage(ChatColor.RED + "Нажата: true, false");
            return true;
        }
        
        String key = args[1].toUpperCase();
        if (!Arrays.asList("W", "A", "S", "D", "SPACE", "SHIFT").contains(key)) {
            player.sendMessage(ChatColor.RED + "Неверная клавиша! Доступные клавиши: W, A, S, D, SPACE, SHIFT");
            return true;
        }
        
        boolean pressed;
        try {
            pressed = Boolean.parseBoolean(args[2].toLowerCase());
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Неверное состояние клавиши! Используйте true или false.");
            return true;
        }
        
        droneListener.simulateKeyPress(player, key, pressed);
        
        if (pressed) {
            player.sendMessage(ChatColor.GREEN + "Клавиша " + key + " нажата.");
        } else {
            player.sendMessage(ChatColor.GREEN + "Клавиша " + key + " отпущена.");
        }
        
        return true;
    }
    
    /**
     * Отправляет сообщение с помощью по команде
     * 
     * @param sender получатель сообщения
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "===== Помощь по команде /drone =====");
        sender.sendMessage(ChatColor.GOLD + "/drone give <игрок> <тип> " + ChatColor.WHITE + "- выдать дрон игроку");
        sender.sendMessage(ChatColor.GOLD + "/drone battery <игрок> <заряд> " + ChatColor.WHITE + "- выдать батарею игроку");
        sender.sendMessage(ChatColor.GOLD + "/drone key <клавиша> <нажата> " + ChatColor.WHITE + "- симуляция нажатия клавиши для управления");
        sender.sendMessage(ChatColor.YELLOW + "================================");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("give", "battery", "key");
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("key")) {
                List<String> keys = Arrays.asList("W", "A", "S", "D", "SPACE", "SHIFT");
                for (String key : keys) {
                    if (key.startsWith(args[1].toUpperCase())) {
                        completions.add(key);
                    }
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                List<String> droneTypes = Arrays.asList("lancet", "predator");
                for (String type : droneTypes) {
                    if (type.startsWith(args[2].toLowerCase())) {
                        completions.add(type);
                    }
                }
            } else if (args[0].equalsIgnoreCase("battery")) {
                List<String> charges = Arrays.asList("100", "200", "500", "1000");
                for (String charge : charges) {
                    if (charge.startsWith(args[2])) {
                        completions.add(charge);
                    }
                }
            } else if (args[0].equalsIgnoreCase("key")) {
                List<String> states = Arrays.asList("true", "false");
                for (String state : states) {
                    if (state.startsWith(args[2].toLowerCase())) {
                        completions.add(state);
                    }
                }
            }
        }
        
        return completions;
    }
} 