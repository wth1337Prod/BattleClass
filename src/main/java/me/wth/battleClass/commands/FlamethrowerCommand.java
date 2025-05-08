package me.wth.battleClass.commands;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.flamethrowers.FlamethrowerManager;
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
import java.util.stream.Collectors;

/**
 * Обработчик команды для выдачи огнеметов
 */
public class FlamethrowerCommand implements CommandExecutor, TabCompleter {
    private final BattleClass plugin;
    private final FlamethrowerManager flamethrowerManager;
    
    /**
     * Создает новый обработчик команды огнеметов
     * 
     * @param plugin экземпляр основного плагина
     * @param flamethrowerManager менеджер огнеметов
     */
    public FlamethrowerCommand(BattleClass plugin, FlamethrowerManager flamethrowerManager) {
        this.plugin = plugin;
        this.flamethrowerManager = flamethrowerManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Использование: /flamethrower <give|list|fuel> [игрок] [тип] [количество]");
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "give":
                return handleGiveCommand(sender, args);
            case "list":
                return handleListCommand(sender);
            case "fuel":
                return handleFuelCommand(sender, args);
            default:
                sender.sendMessage(ChatColor.RED + "Неизвестная подкоманда. Используйте: /flamethrower <give|list|fuel>");
                return true;
        }
    }
    
    /**
     * Обрабатывает команду выдачи огнемета
     * 
     * @param sender отправитель команды
     * @param args аргументы команды
     * @return true, если команда выполнена успешно
     */
    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /flamethrower give <игрок> [тип]");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + args[1] + " не найден!");
            return true;
        }
        
        String flamethrowerType = (args.length > 2) ? args[2].toLowerCase() : "";
        ItemStack flamethrowerItem = null;
        
        if (flamethrowerType.isEmpty() || flamethrowerType.equals("auto")) {
            String factionId = plugin.getRankManager().getPlayerFaction(target.getUniqueId());
            Faction faction = Faction.getByID(factionId);
            
            if (faction != null) {
                flamethrowerItem = flamethrowerManager.createFlamethrowerForFaction(faction);
            }
        } else {
            switch (flamethrowerType) {
                case "rpo94":
                case "shmel":
                case "russian":
                    flamethrowerItem = flamethrowerManager.getFlamethrowerById("rpo94_shmel").createItemStack();
                    break;
                case "xm42":
                case "vulcan":
                case "american":
                    flamethrowerItem = flamethrowerManager.getFlamethrowerById("xm42_vulcan").createItemStack();
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Неизвестный тип огнемета: " + flamethrowerType);
                    return true;
            }
        }
        
        if (flamethrowerItem == null) {
            sender.sendMessage(ChatColor.RED + "Не удалось создать огнемет для игрока " + target.getName() + "!");
            return true;
        }
        
        target.getInventory().addItem(flamethrowerItem);
        
        sender.sendMessage(ChatColor.GREEN + "Огнемет выдан игроку " + target.getName() + "!");
        target.sendMessage(ChatColor.GREEN + "Вы получили огнемет!");
        
        return true;
    }
    
    /**
     * Обрабатывает команду списка доступных огнеметов
     * 
     * @param sender отправитель команды
     * @return true, если команда выполнена успешно
     */
    private boolean handleListCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Доступные огнеметы:");
        
        for (String id : flamethrowerManager.getAllFlamethrowers().keySet()) {
            sender.sendMessage(ChatColor.GRAY + " - " + id);
        }
        
        return true;
    }
    
    /**
     * Обрабатывает команду выдачи канистры с топливом
     * 
     * @param sender отправитель команды
     * @param args аргументы команды
     * @return true, если команда выполнена успешно
     */
    private boolean handleFuelCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /flamethrower fuel <игрок> [количество]");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + args[1] + " не найден!");
            return true;
        }
        
        int amount = 100;
        if (args.length > 2) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0) {
                    sender.sendMessage(ChatColor.RED + "Количество топлива должно быть положительным числом!");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Неверное количество топлива: " + args[2]);
                return true;
            }
        }
        
        ItemStack canister = flamethrowerManager.createFuelCanister(amount);
        target.getInventory().addItem(canister);
        
        sender.sendMessage(ChatColor.GREEN + "Канистра с " + amount + " единицами топлива выдана игроку " + target.getName() + "!");
        target.sendMessage(ChatColor.GREEN + "Вы получили канистру с " + amount + " единицами топлива!");
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterStartingWith(Arrays.asList("give", "list", "fuel"), args[0]);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("fuel")) {
                return filterStartingWith(
                    Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList()), 
                    args[1]
                );
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return filterStartingWith(
                    Arrays.asList("auto", "russian", "american", "rpo94", "xm42", "shmel", "vulcan"), 
                    args[2]
                );
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Фильтрует список возможных вариантов по началу ввода
     * 
     * @param list список всех возможных вариантов
     * @param startingWith начало для фильтрации
     * @return отфильтрованный список
     */
    private List<String> filterStartingWith(List<String> list, String startingWith) {
        return list.stream()
            .filter(s -> s.toLowerCase().startsWith(startingWith.toLowerCase()))
            .collect(Collectors.toList());
    }
} 