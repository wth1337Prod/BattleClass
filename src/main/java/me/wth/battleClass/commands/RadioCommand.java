package me.wth.battleClass.commands;

import me.wth.battleClass.BattleClass;
import me.wth.battleClass.radio.Radio;
import me.wth.battleClass.radio.RadioManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Команда для работы с рацией
 */
public class RadioCommand implements CommandExecutor, TabCompleter {
    private final BattleClass plugin;
    private final RadioManager radioManager;
    
    /**
     * Конструктор команды рации
     * 
     * @param plugin экземпляр плагина
     * @param radioManager менеджер раций
     */
    public RadioCommand(BattleClass plugin, RadioManager radioManager) {
        this.plugin = plugin;
        this.radioManager = radioManager;
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
            case "set":
                handleSetFrequencyCommand(player, args);
                break;
            case "off":
                handleOffCommand(player);
                break;
            case "info":
                handleInfoCommand(player);
                break;
            case "help":
                sendHelpMessage(player);
                break;
            default:
                handleRadioMessage(player, args);
                break;
        }
        
        return true;
    }
    
    /**
     * Обрабатывает команду для установки частоты рации
     * 
     * @param player игрок, использующий команду
     * @param args аргументы команды
     */
    private void handleSetFrequencyCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cИспользование: /r set <частота>");
            player.sendMessage("§cПример: /r set 27.5");
            return;
        }
        
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!radioManager.isRadio(mainHand)) {
            player.sendMessage("§cВы должны держать рацию в руке, чтобы изменить частоту");
            return;
        }
        
        try {
            double frequency = Double.parseDouble(args[1]);
            
            if (frequency < 20.0 || frequency > 200.0) {
                player.sendMessage("§cДоступный диапазон частот: от 20.0 до 200.0 МГц");
                return;
            }
            
            radioManager.setFrequency(mainHand, frequency);
            player.sendMessage("§aЧастота рации установлена на §f" + frequency + " МГц");
            
            if (radioManager.hasActiveRadio(player)) {
                radioManager.deactivateRadio(player);
                Radio radio = radioManager.getRadioFromItem(mainHand);
                radioManager.activateRadio(player, radio, frequency);
                player.sendMessage("§aРация переключена на новую частоту");
            }
            
        } catch (NumberFormatException e) {
            player.sendMessage("§cНеверный формат частоты. Используйте число (например: /r set 27.5)");
        }
    }
    
    /**
     * Обрабатывает команду для выключения рации
     * 
     * @param player игрок, использующий команду
     */
    private void handleOffCommand(Player player) {
        if (radioManager.hasActiveRadio(player)) {
            radioManager.deactivateRadio(player);
            player.sendMessage("§cРация выключена");
        } else {
            player.sendMessage("§cУ вас не активирована рация!");
        }
    }
    
    /**
     * Обрабатывает команду для получения информации о рации
     * 
     * @param player игрок, использующий команду
     */
    private void handleInfoCommand(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        
        if (!radioManager.isRadio(mainHand)) {
            player.sendMessage("§cВы должны держать рацию в руке для получения информации");
            return;
        }
        
        Radio radio = radioManager.getRadioFromItem(mainHand);
        if (radio == null) {
            player.sendMessage("§cОшибка: неизвестная модель рации!");
            return;
        }
        
        double frequency = radioManager.getFrequencyFromItem(mainHand);
        if (frequency <= 0) {
            frequency = radio.getDefaultFrequency();
        }
        
        boolean isRussianRadio = radio.getId().equals("r187p1_azart");
        boolean isAmericanRadio = radio.getId().equals("anprc163");
        
        player.sendMessage("§6====== Информация о рации §6======");
        player.sendMessage("§7Модель: §f" + radio.getDisplayName());
        player.sendMessage("§7Производитель: §f" + (isRussianRadio ? "ЦНИИТ" : (isAmericanRadio ? "L3Harris" : "Неизвестно")));
        player.sendMessage("§7Частота: §f" + frequency + " МГц");
        player.sendMessage("§7Дальность связи: §f" + radio.getRange() + " блоков");
        player.sendMessage("§7Работает под водой: " + (radio.isWaterproof() ? "§aДа" : "§cНет"));
        player.sendMessage("§7Шифрование: " + (radio.hasEncryption() ? "§aДа" : "§cНет"));
        player.sendMessage("§7Время работы: §f" + radio.getBatteryLife() + " минут");
        player.sendMessage("§7Статус: " + (radioManager.hasActiveRadio(player) ? "§aВключена" : "§cВыключена"));
        
        player.sendMessage("");
        if (isRussianRadio) {
            player.sendMessage("§cНесовместимость: §7Не может связываться с американскими рациями AN/PRC-163");
            player.sendMessage("§7Рация использует российский протокол шифрования и кодирования сигнала");
        } else if (isAmericanRadio) {
            player.sendMessage("§cНесовместимость: §7Не может связываться с российскими рациями Р-187П1");
            player.sendMessage("§7Рация использует американский протокол шифрования SINCGARS и кодирования сигнала");
        }
    }
    
    /**
     * Отправляет сообщение по рации
     * 
     * @param player игрок, отправляющий сообщение
     * @param args аргументы команды
     */
    private void handleRadioMessage(Player player, String[] args) {
        if (!radioManager.hasActiveRadio(player)) {
            player.sendMessage("§cУ вас не активирована рация! Сначала активируйте её с помощью ПКМ.");
            return;
        }
        
        StringBuilder messageBuilder = new StringBuilder();
        for (String arg : args) {
            messageBuilder.append(arg).append(" ");
        }
        String message = messageBuilder.toString().trim();
        
        if (message.isEmpty()) {
            player.sendMessage("§cВведите сообщение для передачи по рации");
            return;
        }
        
        radioManager.broadcastRadioMessage(player, message);
    }
    
    /**
     * Отправляет сообщение с помощью по команде
     * 
     * @param player игрок, которому отправляется помощь
     */
    private void sendHelpMessage(Player player) {
        player.sendMessage("§6====== Команды рации §6======");
        player.sendMessage("§a/r <сообщение> §7- отправить сообщение по рации");
        player.sendMessage("§a/r set <частота> §7- установить частоту рации");
        player.sendMessage("§a/r off §7- выключить рацию");
        player.sendMessage("§a/r info §7- информация о рации");
        player.sendMessage("§a/r help §7- показать эту справку");
        player.sendMessage("");
        player.sendMessage("§7Вы также можете писать сообщения с префиксом §fр: §7или §fr: §7для отправки по рации");
        player.sendMessage("§7Например: §fр: Всем постам, как слышно?");
        player.sendMessage("§7Для изменения частоты можно написать: §fчастота 27.5");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> commands = Arrays.asList("set", "off", "info", "help");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                List<String> frequencies = Arrays.asList("27.5", "30.0", "42.5", "68.2", "127.5", "150.0");
                StringUtil.copyPartialMatches(args[1], frequencies, completions);
            }
        }
        
        return completions;
    }
} 