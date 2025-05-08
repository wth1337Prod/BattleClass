package me.wth.battleClass.donations;

import me.wth.battleClass.BattleClass;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

/**
 * Класс для обработки команды /donate
 */
public class DonateCommand implements CommandExecutor {
    private final BattleClass plugin;
    private final DonateGUI donateGUI;
    
    /**
     * Конструктор команды
     *
     * @param plugin экземпляр основного класса плагина
     * @param donateGUI GUI для работы с донатами
     */
    public DonateCommand(BattleClass plugin, DonateGUI donateGUI) {
        this.plugin = plugin;
        this.donateGUI = donateGUI;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эта команда может быть использована только игроком!");
            return true;
        }
        
        Player player = (Player) sender;
        
        donateGUI.openMainDonateMenu(player);
        
        return true;
    }
} 