package me.wth.battleClass.radio;

import me.wth.battleClass.BattleClass;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

/**
 * Менеджер для управления радиостанциями
 */
public class RadioManager {
    private final BattleClass plugin;
    private final Map<String, Radio> radios = new HashMap<>();
    
    private final Map<UUID, String> activeRadios = new HashMap<>();
    
    private final Map<UUID, Double> playerFrequencies = new HashMap<>();
    
    private final Set<UUID> playersWithRadioOn = new HashSet<>();
    
    /**
     * Конструктор менеджера радиостанций
     * 
     * @param plugin экземпляр плагина
     */
    public RadioManager(BattleClass plugin) {
        this.plugin = plugin;
        registerRadios();
    }
    
    /**
     * Регистрирует доступные радиостанции
     */
    private void registerRadios() {
        registerRadio(new R187P1Azart(plugin));
        
        registerRadio(new ANPRC163(plugin));
    }
    
    /**
     * Регистрирует радиостанцию в менеджере
     * 
     * @param radio экземпляр радиостанции
     */
    private void registerRadio(Radio radio) {
        radios.put(radio.getId(), radio);
    }
    
    /**
     * Выдает радиостанцию игроку
     * 
     * @param player игрок, которому выдается рация
     * @param radioId идентификатор радиостанции
     * @return true, если рация успешно выдана
     */
    public boolean giveRadioToPlayer(Player player, String radioId) {
        Radio radio = radios.get(radioId);
        
        if (radio == null) {
            player.sendMessage("§cРадиостанция " + radioId + " не найдена!");
            return false;
        }
        
        ItemStack radioItem = radio.createItemStack();
        player.getInventory().addItem(radioItem);
        player.sendMessage("§aВы получили §f" + radio.getDisplayName());
        return true;
    }
    
    /**
     * Проверяет, является ли предмет радиостанцией
     * 
     * @param item предмет для проверки
     * @return true, если предмет является рацией
     */
    public boolean isRadio(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "radio_id");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        return container.has(key, PersistentDataType.STRING);
    }
    
    /**
     * Получает радиостанцию из предмета
     * 
     * @param item предмет для проверки
     * @return объект радиостанции или null, если предмет не является рацией
     */
    public Radio getRadioFromItem(ItemStack item) {
        if (!isRadio(item)) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "radio_id");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        String radioId = container.get(key, PersistentDataType.STRING);
        if (radioId == null) {
            return null;
        }
        
        return radios.get(radioId);
    }
    
    /**
     * Получает текущую частоту рации из предмета
     * 
     * @param item предмет рации
     * @return текущая частота или -1.0, если частота не найдена
     */
    public double getFrequencyFromItem(ItemStack item) {
        if (!isRadio(item)) {
            return -1.0;
        }
        
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "radio_frequency");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        if (container.has(key, PersistentDataType.DOUBLE)) {
            return container.getOrDefault(key, PersistentDataType.DOUBLE, -1.0);
        }
        
        return -1.0;
    }
    
    /**
     * Устанавливает частоту рации
     * 
     * @param item предмет рации
     * @param frequency новая частота
     * @return true, если частота успешно установлена
     */
    public boolean setFrequency(ItemStack item, double frequency) {
        if (!isRadio(item)) {
            return false;
        }
        
        if (frequency < 20.0 || frequency > 200.0) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        NamespacedKey key = new NamespacedKey(plugin, "radio_frequency");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.DOUBLE, frequency);
        
        List<String> lore = meta.getLore();
        if (lore != null) {
            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).startsWith("§7Частота:")) {
                    lore.set(i, "§7Частота: §f" + frequency + " МГц");
                    break;
                }
            }
            meta.setLore(lore);
        }
        
        item.setItemMeta(meta);
        return true;
    }
    
    /**
     * Активирует рацию для игрока
     * 
     * @param player игрок, который активирует рацию
     * @param radio объект радиостанции
     * @param frequency частота передачи
     * @return true, если рация успешно активирована
     */
    public boolean activateRadio(Player player, Radio radio, double frequency) {
        UUID playerUUID = player.getUniqueId();
        
        activeRadios.put(playerUUID, radio.getId());
        playerFrequencies.put(playerUUID, frequency);
        playersWithRadioOn.add(playerUUID);
        
        return true;
    }
    
    /**
     * Деактивирует рацию для игрока
     * 
     * @param player игрок, у которого деактивируется рация
     * @return true, если рация успешно деактивирована
     */
    public boolean deactivateRadio(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        activeRadios.remove(playerUUID);
        playersWithRadioOn.remove(playerUUID);
        
        return true;
    }
    
    /**
     * Проверяет, активирована ли рация у игрока
     * 
     * @param player игрок для проверки
     * @return true, если у игрока активирована рация
     */
    public boolean hasActiveRadio(Player player) {
        return playersWithRadioOn.contains(player.getUniqueId());
    }
    
    /**
     * Получает список игроков, которые могут услышать сообщение от данного игрока
     * 
     * @param speaker игрок, который говорит по рации
     * @return список игроков, которые слышат сообщение
     */
    public List<Player> getPlayersInRange(Player speaker) {
        List<Player> receivers = new ArrayList<>();
        
        UUID speakerUUID = speaker.getUniqueId();
        if (!hasActiveRadio(speaker) || !activeRadios.containsKey(speakerUUID)) {
            return receivers;
        }
        
        String speakerRadioId = activeRadios.get(speakerUUID);
        Radio speakerRadio = radios.get(speakerRadioId);
        double speakerFrequency = playerFrequencies.getOrDefault(speakerUUID, -1.0);
        
        if (speakerRadio == null || speakerFrequency < 0) {
            return receivers;
        }
        
        boolean isSpeakerRussian = speakerRadioId.equals("r187p1_azart");
        boolean isSpeakerAmerican = speakerRadioId.equals("anprc163");
        
        for (UUID listenerUUID : playersWithRadioOn) {
            if (listenerUUID.equals(speakerUUID)) {
                continue; 
            }
            
            Player listener = plugin.getServer().getPlayer(listenerUUID);
            if (listener == null || !listener.isOnline()) {
                continue;
            }
            
            double listenerFrequency = playerFrequencies.getOrDefault(listenerUUID, -1.0);
            if (Math.abs(listenerFrequency - speakerFrequency) > 0.1) {
                continue; 
            }
            
            String listenerRadioId = activeRadios.get(listenerUUID);
            Radio listenerRadio = radios.get(listenerRadioId);
            
            if (listenerRadio != null) {
                boolean isListenerRussian = listenerRadioId.equals("r187p1_azart");
                boolean isListenerAmerican = listenerRadioId.equals("anprc163");
                
                if ((isSpeakerRussian && isListenerAmerican) || (isSpeakerAmerican && isListenerRussian)) {
                    continue; 
                }
                
                int maxRange = Math.min(speakerRadio.getRange(), listenerRadio.getRange());
                
                if (speaker.getWorld().equals(listener.getWorld()) && 
                    speaker.getLocation().distance(listener.getLocation()) <= maxRange) {
                    receivers.add(listener);
                }
            }
        }
        
        return receivers;
    }
    
    /**
     * Отправляет сообщение по рации всем игрокам на той же частоте в пределах дальности
     * 
     * @param speaker игрок, который говорит
     * @param message сообщение
     * @return true, если сообщение успешно отправлено хотя бы одному игроку
     */
    public boolean broadcastRadioMessage(Player speaker, String message) {
        if (!hasActiveRadio(speaker)) {
            speaker.sendMessage("§cУ вас нет активной рации!");
            return false;
        }
        
        UUID speakerUUID = speaker.getUniqueId();
        double frequency = playerFrequencies.getOrDefault(speakerUUID, -1.0);
        
        if (frequency < 0) {
            speaker.sendMessage("§cНеверная частота рации!");
            return false;
        }
        
        String speakerRadioId = activeRadios.get(speakerUUID);
        boolean isRussianRadio = speakerRadioId.equals("r187p1_azart");
        boolean isAmericanRadio = speakerRadioId.equals("anprc163");
        
        List<Player> receivers = getPlayersInRange(speaker);
        
        boolean incompatibleSignalsDetected = false;
        
        for (UUID listenerUUID : playersWithRadioOn) {
            if (listenerUUID.equals(speakerUUID)) {
                continue; 
            }
            
            Player listener = plugin.getServer().getPlayer(listenerUUID);
            if (listener == null || !listener.isOnline()) {
                continue;
            }
            
            double listenerFrequency = playerFrequencies.getOrDefault(listenerUUID, -1.0);
            if (Math.abs(listenerFrequency - frequency) > 0.1) {
                continue; 
            }
            
            String listenerRadioId = activeRadios.get(listenerUUID);
            if ((isRussianRadio && listenerRadioId.equals("anprc163")) || 
                (isAmericanRadio && listenerRadioId.equals("r187p1_azart"))) {
                incompatibleSignalsDetected = true;
                break;
            }
        }
        
        if (receivers.isEmpty()) {
            if (incompatibleSignalsDetected) {
                if (isRussianRadio) {
                    speaker.sendMessage("§cОбнаружены несовместимые сигналы от раций США на частоте " + frequency + " МГц. Связь невозможна.");
                } else if (isAmericanRadio) {
                    speaker.sendMessage("§cОбнаружены несовместимые сигналы от российских раций на частоте " + frequency + " МГц. Связь невозможна.");
                }
            } else {
                speaker.sendMessage("§eНикто не слышит вашу передачу на частоте " + frequency + " МГц");
            }
            return false;
        }
        
        String radioName = isRussianRadio ? "Р-187П1" : (isAmericanRadio ? "AN/PRC-163" : "Рация");
        String radioMessage = "§8[§a" + radioName + ": " + frequency + " МГц§8] §f" + speaker.getName() + ": §e" + message;
        
        speaker.sendMessage(radioMessage);
        for (Player receiver : receivers) {
            receiver.sendMessage(radioMessage);
            
            receiver.playSound(receiver.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.0f);
        }
        
        return true;
    }
    
    /**
     * Получает карту всех зарегистрированных радиостанций
     * 
     * @return карта радиостанций (id -> объект)
     */
    public Map<String, Radio> getRadios() {
        return radios;
    }
    
    /**
     * Получает список всех идентификаторов радиостанций
     * 
     * @return список идентификаторов
     */
    public List<String> getRadioIds() {
        return new ArrayList<>(radios.keySet());
    }
} 