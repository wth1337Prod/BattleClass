package me.wth.battleClass.radio;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Слушатель событий для работы с радиостанциями
 */
public class RadioListener implements Listener {
    private final BattleClass plugin;
    private final RadioManager radioManager;
    
    /**
     * Конструктор слушателя событий радиостанций
     * 
     * @param plugin экземпляр плагина
     * @param radioManager менеджер радиостанций
     */
    public RadioListener(BattleClass plugin, RadioManager radioManager) {
        this.plugin = plugin;
        this.radioManager = radioManager;
    }
    
    /**
     * Обрабатывает события нажатия на рацию
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !radioManager.isRadio(item)) {
            return;
        }
        
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        event.setCancelled(true);
        
        Radio radio = radioManager.getRadioFromItem(item);
        if (radio == null) {
            player.sendMessage("§cОшибка: неизвестная модель рации!");
            return;
        }
        
        boolean isRussianRadio = radio.getId().equals("r187p1_azart");
        boolean isAmericanRadio = radio.getId().equals("anprc163");
        
        if (radioManager.hasActiveRadio(player)) {
            radioManager.deactivateRadio(player);
            player.sendMessage("§cРация выключена");
        } else {
            double frequency = radioManager.getFrequencyFromItem(item);
            if (frequency <= 0) {
                frequency = radio.getDefaultFrequency();
                radioManager.setFrequency(item, frequency);
            }
            
            radioManager.activateRadio(player, radio, frequency);
            player.sendMessage("§aРация §f" + radio.getDisplayName() + "§a активирована");
            player.sendMessage("§7Текущая частота: §f" + frequency + " МГц");
            player.sendMessage("§7Используйте команду §f/r <сообщение> §7для передачи по рации");
            player.sendMessage("§7Используйте команду §f/r set <частота> §7для настройки частоты");
            
            if (isRussianRadio) {
                player.sendMessage("§cВнимание: §7Эта российская рация несовместима с американскими рациями AN/PRC-163");
                player.sendMessage("§7Вы не сможете общаться с владельцами американских раций даже на одной частоте");
            } else if (isAmericanRadio) {
                player.sendMessage("§cВнимание: §7Эта американская рация несовместима с российскими рациями Р-187П1");
                player.sendMessage("§7Вы не сможете общаться с владельцами российских раций даже на одной частоте");
            }
            
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.5f);
        }
    }
    
    /**
     * Обрабатывает сообщения чата для радио-команд
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        if (message.startsWith("р: ") || message.startsWith("r: ")) {
            event.setCancelled(true);
            
            String radioMessage = message.substring(3).trim();
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (radioManager.hasActiveRadio(player)) {
                    radioManager.broadcastRadioMessage(player, radioMessage);
                } else {
                    player.sendMessage("§cУ вас не активирована рация! Сначала активируйте её с помощью ПКМ.");
                }
            });
            
            return;
        }
        
        if (message.startsWith("частота ") || message.startsWith("frequency ")) {
            event.setCancelled(true);
            
            String[] parts = message.split(" ", 2);
            if (parts.length < 2) {
                return;
            }
            
            try {
                double frequency = Double.parseDouble(parts[1]);
                
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    ItemStack mainHand = player.getInventory().getItemInMainHand();
                    if (radioManager.isRadio(mainHand)) {
                        if (frequency >= 20.0 && frequency <= 200.0) {
                            radioManager.setFrequency(mainHand, frequency);
                            player.sendMessage("§aЧастота рации установлена на §f" + frequency + " МГц");
                            
                            if (radioManager.hasActiveRadio(player)) {
                                radioManager.deactivateRadio(player);
                                Radio radio = radioManager.getRadioFromItem(mainHand);
                                radioManager.activateRadio(player, radio, frequency);
                            }
                        } else {
                            player.sendMessage("§cДоступный диапазон частот: от 20.0 до 200.0 МГц");
                        }
                    } else {
                        player.sendMessage("§cВы должны держать рацию в руке, чтобы изменить частоту");
                    }
                });
            } catch (NumberFormatException e) {
                player.sendMessage("§cНеверный формат частоты. Используйте число (например: частота 27.5)");
            }
        }
    }
} 