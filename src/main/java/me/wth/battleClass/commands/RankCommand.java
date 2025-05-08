package me.wth.battleClass.commands;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.ranks.Rank;
import me.wth.battleClass.ranks.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс команды для управления военными рангами игроков
 */
public class RankCommand implements CommandExecutor, TabCompleter {
    private final BattleClass plugin;
    private final RankManager rankManager;
    
    public RankCommand(BattleClass plugin, RankManager rankManager) {
        this.plugin = plugin;
        this.rankManager = rankManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "set":
                handleSetCommand(sender, args);
                break;
            case "info":
                handleInfoCommand(sender, args);
                break;
            case "list":
                handleListCommand(sender, args);
                break;
            case "remove":
                handleRemoveCommand(sender, args);
                break;
            default:
                sendHelpMessage(sender);
                break;
        }
        
        return true;
    }
    
    /**
     * Обрабатывает команду установки ранга
     */
    private void handleSetCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("battleclass.rank.set")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав для установки военных рангов!");
            return;
        }
        
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Использование: /rank set <игрок> <фракция> <ранг>");
            sender.sendMessage(ChatColor.RED + "Фракции: us (США), ru (РФ)");
            return;
        }
        
        String playerName = args[1];
        Player target = Bukkit.getPlayer(playerName);
        
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + playerName + " не найден или не в сети!");
            return;
        }
        
        String faction = args[2].toLowerCase();
        if (!faction.equals("us") && !faction.equals("ru")) {
            sender.sendMessage(ChatColor.RED + "Неверная фракция! Доступные фракции: us (США), ru (РФ)");
            return;
        }
        
        String rankId = args[3].toLowerCase();
        
        Map<String, Rank> ranks = rankManager.getRanks(faction);
        if (!ranks.containsKey(rankId)) {
            sender.sendMessage(ChatColor.RED + "Ранг '" + rankId + "' не существует для фракции " + 
                    (faction.equals("us") ? "США" : "РФ") + "!");
            sender.sendMessage(ChatColor.RED + "Используйте /rank list " + faction + 
                    " для просмотра доступных рангов");
            return;
        }
        
        boolean success = rankManager.setPlayerRank(target, rankId, faction);
        
        if (success) {
            Rank rank = ranks.get(rankId);
            
            String factionName = faction.equals("us") ? "США" : "РФ";
            ChatColor factionColor = faction.equals("us") ? ChatColor.BLUE : ChatColor.RED;
            
            sender.sendMessage(ChatColor.GREEN + "Вы успешно установили ранг " + rank.getColor() + 
                    rank.getDisplayName() + ChatColor.GREEN + " (фракция: " + factionColor + factionName + 
                    ChatColor.GREEN + ") игроку " + target.getName());
            
            target.sendMessage(ChatColor.GREEN + "Вам был присвоен военный ранг " + rank.getColor() + 
                    rank.getDisplayName() + ChatColor.GREEN + " (" + factionColor + factionName + ChatColor.GREEN + ")");
        } else {
            sender.sendMessage(ChatColor.RED + "Не удалось установить ранг игроку " + target.getName());
        }
    }
    
    /**
     * Обрабатывает команду получения информации о ранге игрока
     */
    private void handleInfoCommand(CommandSender sender, String[] args) {
        Player target;
        
        if (args.length < 2) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(ChatColor.RED + "Использование для консоли: /rank info <игрок>");
                return;
            }
        } else {
            String playerName = args[1];
            target = Bukkit.getPlayer(playerName);
            
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Игрок " + playerName + " не найден или не в сети!");
                return;
            }
        }
        
        Rank rank = rankManager.getPlayerRank(target);
        String faction = rankManager.getPlayerFaction(target);
        
        if (rank == null || faction == null) {
            sender.sendMessage(ChatColor.RED + "У игрока " + target.getName() + " нет военного ранга!");
            return;
        }
        
        String factionName = faction.equals("us") ? "США" : "РФ";
        ChatColor factionColor = faction.equals("us") ? ChatColor.BLUE : ChatColor.RED;
        
        sender.sendMessage(ChatColor.GOLD + "=== Информация о военном ранге ===");
        sender.sendMessage(ChatColor.YELLOW + "Игрок: " + ChatColor.WHITE + target.getName());
        sender.sendMessage(ChatColor.YELLOW + "Фракция: " + factionColor + factionName);
        sender.sendMessage(ChatColor.YELLOW + "Ранг: " + rank.getColor() + rank.getDisplayName() + 
                ChatColor.YELLOW + " (ID: " + rank.getId() + ")");
        sender.sendMessage(ChatColor.YELLOW + "Уровень ранга: " + ChatColor.WHITE + rank.getLevel());
        sender.sendMessage(ChatColor.YELLOW + "Командир: " + (rank.isCommander() ? 
                ChatColor.GREEN + "Да" : ChatColor.RED + "Нет"));
        
        if (rank.isCommander()) {
            sender.sendMessage(ChatColor.GREEN + "Имеет доступ к использованию военного планшета");
        } else {
            sender.sendMessage(ChatColor.RED + "Нет доступа к использованию военного планшета");
        }
    }
    
    /**
     * Обрабатывает команду для вывода списка доступных рангов
     */
    private void handleListCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /rank list <us|ru>");
            return;
        }
        
        String faction = args[1].toLowerCase();
        if (!faction.equals("us") && !faction.equals("ru")) {
            sender.sendMessage(ChatColor.RED + "Неверная фракция! Доступные фракции: us (США), ru (РФ)");
            return;
        }
        
        String factionName = faction.equals("us") ? "США" : "РФ";
        ChatColor factionColor = faction.equals("us") ? ChatColor.BLUE : ChatColor.RED;
        
        sender.sendMessage(factionColor + "=== Список военных рангов " + factionName + " ===");
        
        Map<String, Rank> ranks = rankManager.getRanks(faction);
        
        int lastLevel = -1;
        String lastGroup = "";
        
        for (Rank rank : ranks.values().stream()
                .sorted((r1, r2) -> Integer.compare(r1.getLevel(), r2.getLevel()))
                .collect(Collectors.toList())) {
            
            String currentGroup;
            if (faction.equals("us")) {
                if (rank.getLevel() <= 4) {
                    currentGroup = "Рядовой состав";
                } else if (rank.getLevel() <= 9) {
                    currentGroup = "Сержантский состав";
                } else if (rank.getLevel() <= 12) {
                    currentGroup = "Уорент-офицеры";
                } else if (rank.getLevel() <= 17) {
                    currentGroup = "Офицерский состав";
                } else {
                    currentGroup = "Генералы";
                }
            } else {
                if (rank.getLevel() <= 2) {
                    currentGroup = "Рядовой состав";
                } else if (rank.getLevel() <= 8) {
                    currentGroup = "Сержантский состав";
                } else if (rank.getLevel() <= 12) {
                    currentGroup = "Младший офицерский состав";
                } else if (rank.getLevel() <= 15) {
                    currentGroup = "Старший офицерский состав";
                } else {
                    currentGroup = "Высший офицерский состав";
                }
            }
            
            if (!currentGroup.equals(lastGroup)) {
                sender.sendMessage(ChatColor.GOLD + "== " + currentGroup + " ==");
                lastGroup = currentGroup;
            }
            
            sender.sendMessage(ChatColor.YELLOW + "• " + rank.getColor() + rank.getDisplayName() + 
                    ChatColor.GRAY + " (ID: " + rank.getId() + ")" + 
                    (rank.isCommander() ? ChatColor.GREEN + " [Командир]" : ""));
        }
    }
    
    /**
     * Обрабатывает команду для удаления военного ранга игрока
     */
    private void handleRemoveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("battleclass.rank.remove")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав для удаления военных рангов!");
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /rank remove <игрок>");
            return;
        }
        
        String playerName = args[1];
        Player target = Bukkit.getPlayer(playerName);
        
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + playerName + " не найден или не в сети!");
            return;
        }
        
        Rank currentRank = rankManager.getPlayerRank(target);
        if (currentRank == null) {
            sender.sendMessage(ChatColor.RED + "У игрока " + target.getName() + " нет военного ранга!");
            return;
        }
        
        rankManager.setPlayerRank(target, null, null);
        
        sender.sendMessage(ChatColor.GREEN + "Вы успешно удалили военный ранг у игрока " + target.getName());
        target.sendMessage(ChatColor.YELLOW + "Ваш военный ранг был удален");
    }
    
    /**
     * Отправляет сообщение с помощью по команде
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Помощь по команде /rank ===");
        sender.sendMessage(ChatColor.YELLOW + "/rank set <игрок> <фракция> <ранг> " + ChatColor.WHITE + 
                "- Установить военный ранг игроку");
        sender.sendMessage(ChatColor.YELLOW + "/rank info [игрок] " + ChatColor.WHITE + 
                "- Просмотреть информацию о ранге игрока");
        sender.sendMessage(ChatColor.YELLOW + "/rank list <us|ru> " + ChatColor.WHITE + 
                "- Просмотреть список всех рангов фракции");
        sender.sendMessage(ChatColor.YELLOW + "/rank remove <игрок> " + ChatColor.WHITE + 
                "- Удалить военный ранг у игрока");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String[] subcommands = {"set", "info", "list", "remove"};
            return filterCompletions(subcommands, args[0]);
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "set":
                case "info":
                case "remove":
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                case "list":
                    return filterCompletions(new String[]{"us", "ru"}, args[1]);
            }
        } else if (args.length == 3 && args[0].toLowerCase().equals("set")) {
            return filterCompletions(new String[]{"us", "ru"}, args[2]);
        } else if (args.length == 4 && args[0].toLowerCase().equals("set")) {
            if (args[2].equalsIgnoreCase("us") || args[2].equalsIgnoreCase("ru")) {
                List<String> rankIds = rankManager.getRankIds(args[2].toLowerCase());
                return filterCompletions(rankIds.toArray(new String[0]), args[3]);
            }
        }
        
        return completions;
    }
    
    /**
     * Фильтрует список автодополнений по вводимому тексту
     */
    private List<String> filterCompletions(String[] options, String input) {
        return Arrays.stream(options)
                .filter(option -> option.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
} 