package me.wth.battleClass.commands;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.mortars.MortarManager;
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
 * Команда для выдачи и управления минометами
 */
public class MortarCommand implements CommandExecutor, TabCompleter {
    private final BattleClass plugin;
    private final MortarManager mortarManager;
    
    /**
     * Конструктор для команды минометов
     * 
     * @param plugin экземпляр основного плагина
     * @param mortarManager менеджер минометов
     */
    public MortarCommand(BattleClass plugin, MortarManager mortarManager) {
        this.plugin = plugin;
        this.mortarManager = mortarManager;
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
            case "list":
                return handleListCommand(sender);
            case "help":
                sendHelpMessage(sender);
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "Неизвестная подкоманда. Используйте /mortar help для справки.");
                return true;
        }
    }
    
    /**
     * Обрабатывает команду выдачи миномета
     * 
     * @param sender отправитель команды
     * @param args аргументы команды
     * @return true, если команда успешно выполнена
     */
    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("battleclass.mortar.give")) {
            sender.sendMessage(ChatColor.RED + "У вас нет разрешения на использование этой команды.");
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Использование: /mortar give <игрок> <russia|usa>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + args[1] + " не найден.");
            return true;
        }
        
        Faction faction;
        if (args[2].equalsIgnoreCase("russia")) {
            faction = Faction.RUSSIA;
        } else if (args[2].equalsIgnoreCase("usa")) {
            faction = Faction.USA;
        } else {
            sender.sendMessage(ChatColor.RED + "Неверная фракция. Используйте 'russia' или 'usa'.");
            return true;
        }
        
        ItemStack mortarItem = mortarManager.createMortarForFaction(faction);
        if (mortarItem == null) {
            sender.sendMessage(ChatColor.RED + "Ошибка создания миномета для фракции " + faction);
            return true;
        }
        
        target.getInventory().addItem(mortarItem);
        
        String factionName = faction == Faction.RUSSIA ? "России" : "США";
        sender.sendMessage(ChatColor.GREEN + "Выдан миномет фракции " + factionName + " игроку " + target.getName());
        target.sendMessage(ChatColor.GREEN + "Вы получили миномет фракции " + factionName);
        
        return true;
    }
    
    /**
     * Обрабатывает команду списка минометов
     * 
     * @param sender отправитель команды
     * @return true, если команда успешно выполнена
     */
    private boolean handleListCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Доступные миномёты:");
        sender.sendMessage(ChatColor.RED + "РФ: 2Б14 \"Поднос\" - 82-мм миномёт");
        sender.sendMessage(ChatColor.BLUE + "США: M224 - 60-мм миномёт");
        return true;
    }
    
    /**
     * Отправляет сообщение с помощью по команде
     * 
     * @param sender отправитель команды
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "=== Помощь по команде /mortar ===");
        sender.sendMessage(ChatColor.YELLOW + "/mortar give <игрок> <russia|usa> " + 
                          ChatColor.GRAY + "- Выдать миномет игроку");
        sender.sendMessage(ChatColor.YELLOW + "/mortar list " + 
                          ChatColor.GRAY + "- Список доступных минометов");
        sender.sendMessage(ChatColor.YELLOW + "/mortar help " + 
                          ChatColor.GRAY + "- Показать эту справку");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("give", "list", "help");
            return filterCompletions(subCommands, args[0]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return null; 
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> factions = Arrays.asList("russia", "usa");
            return filterCompletions(factions, args[2]);
        }
        
        return completions;
    }
    
    /**
     * Фильтрует список завершений по введенному частичному аргументу
     * 
     * @param completions список возможных завершений
     * @param current текущий введенный частичный аргумент
     * @return отфильтрованный список завершений
     */
    private List<String> filterCompletions(List<String> completions, String current) {
        return completions.stream()
            .filter(s -> s.toLowerCase().startsWith(current.toLowerCase()))
            .collect(Collectors.toList());
    }
} 