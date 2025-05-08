package me.wth.battleClass.ranks;

import me.wth.battleClass.BattleClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Класс для управления военными рангами игроков
 */
public class RankManager {
    private final BattleClass plugin;
    private final NamespacedKey rankKey;
    private final NamespacedKey factionKey;
    
    private final Map<String, Rank> usRanks = new HashMap<>();
    private final Map<String, Rank> ruRanks = new HashMap<>();
    
    private File ranksFile;
    private FileConfiguration ranksConfig;
    
    private final Map<UUID, String> playerRanks = new HashMap<>();
    private final Map<UUID, String> playerFactions = new HashMap<>();
    
    /**
     * Конструктор менеджера рангов
     * 
     * @param plugin экземпляр главного класса плагина
     */
    public RankManager(BattleClass plugin) {
        this.plugin = plugin;
        this.rankKey = new NamespacedKey(plugin, "military_rank");
        this.factionKey = new NamespacedKey(plugin, "military_faction");
        
        initUSRanks();
        
        initRURanks();
        
        loadRanksConfig();
        
        loadPlayerRanks();
    }
    
    /**
     * Инициализирует ранги армии США
     */
    private void initUSRanks() {
        usRanks.put("private", new Rank("private", "Рядовой", ChatColor.GRAY, 1, false));
        usRanks.put("private_first_class", new Rank("private_first_class", "Рядовой первого класса", ChatColor.GRAY, 2, false));
        usRanks.put("specialist", new Rank("specialist", "Специалист", ChatColor.GRAY, 3, false));
        usRanks.put("corporal", new Rank("corporal", "Капрал", ChatColor.GRAY, 4, false));
        
        usRanks.put("sergeant", new Rank("sergeant", "Сержант", ChatColor.GREEN, 5, false));
        usRanks.put("staff_sergeant", new Rank("staff_sergeant", "Штаб-сержант", ChatColor.GREEN, 6, false));
        usRanks.put("sergeant_first_class", new Rank("sergeant_first_class", "Сержант первого класса", ChatColor.GREEN, 7, false));
        usRanks.put("master_sergeant", new Rank("master_sergeant", "Мастер-сержант", ChatColor.GREEN, 8, true));
        usRanks.put("first_sergeant", new Rank("first_sergeant", "Первый сержант", ChatColor.GREEN, 9, true));
        
        usRanks.put("warrant_officer_1", new Rank("warrant_officer_1", "Уорент-офицер 1", ChatColor.DARK_GREEN, 10, true));
        usRanks.put("chief_warrant_officer_2", new Rank("chief_warrant_officer_2", "Старший уорент-офицер 2", ChatColor.DARK_GREEN, 11, true));
        usRanks.put("chief_warrant_officer_3", new Rank("chief_warrant_officer_3", "Старший уорент-офицер 3", ChatColor.DARK_GREEN, 12, true));
        
        usRanks.put("lieutenant", new Rank("lieutenant", "Лейтенант", ChatColor.AQUA, 13, true));
        usRanks.put("captain", new Rank("captain", "Капитан", ChatColor.AQUA, 14, true));
        usRanks.put("major", new Rank("major", "Майор", ChatColor.AQUA, 15, true));
        usRanks.put("lieutenant_colonel", new Rank("lieutenant_colonel", "Подполковник", ChatColor.BLUE, 16, true));
        usRanks.put("colonel", new Rank("colonel", "Полковник", ChatColor.BLUE, 17, true));
        
        usRanks.put("brigadier_general", new Rank("brigadier_general", "Бригадный генерал", ChatColor.GOLD, 18, true));
        usRanks.put("major_general", new Rank("major_general", "Генерал-майор", ChatColor.GOLD, 19, true));
        usRanks.put("lieutenant_general", new Rank("lieutenant_general", "Генерал-лейтенант", ChatColor.GOLD, 20, true));
        usRanks.put("general", new Rank("general", "Генерал", ChatColor.GOLD, 21, true));
    }
    
    /**
     * Инициализирует ранги армии РФ
     */
    private void initRURanks() {
        ruRanks.put("ryadovoy", new Rank("ryadovoy", "Рядовой", ChatColor.GRAY, 1, false));
        ruRanks.put("efreitor", new Rank("efreitor", "Ефрейтор", ChatColor.GRAY, 2, false));
        
        ruRanks.put("mladshiy_serzhant", new Rank("mladshiy_serzhant", "Младший сержант", ChatColor.GREEN, 3, false));
        ruRanks.put("serzhant", new Rank("serzhant", "Сержант", ChatColor.GREEN, 4, false));
        ruRanks.put("starshiy_serzhant", new Rank("starshiy_serzhant", "Старший сержант", ChatColor.GREEN, 5, false));
        ruRanks.put("starshina", new Rank("starshina", "Старшина", ChatColor.GREEN, 6, false));
        ruRanks.put("praporschik", new Rank("praporschik", "Прапорщик", ChatColor.GREEN, 7, true));
        ruRanks.put("starshiy_praporschik", new Rank("starshiy_praporschik", "Старший прапорщик", ChatColor.GREEN, 8, true));
        
        ruRanks.put("mladshiy_leytenant", new Rank("mladshiy_leytenant", "Младший лейтенант", ChatColor.AQUA, 9, true));
        ruRanks.put("leytenant", new Rank("leytenant", "Лейтенант", ChatColor.AQUA, 10, true));
        ruRanks.put("starshiy_leytenant", new Rank("starshiy_leytenant", "Старший лейтенант", ChatColor.AQUA, 11, true));
        ruRanks.put("kapitan", new Rank("kapitan", "Капитан", ChatColor.AQUA, 12, true));
        
        ruRanks.put("mayor", new Rank("mayor", "Майор", ChatColor.BLUE, 13, true));
        ruRanks.put("podpolkovnik", new Rank("podpolkovnik", "Подполковник", ChatColor.BLUE, 14, true));
        ruRanks.put("polkovnik", new Rank("polkovnik", "Полковник", ChatColor.BLUE, 15, true));
        
        ruRanks.put("general_mayor", new Rank("general_mayor", "Генерал-майор", ChatColor.GOLD, 16, true));
        ruRanks.put("general_leytenant", new Rank("general_leytenant", "Генерал-лейтенант", ChatColor.GOLD, 17, true));
        ruRanks.put("general_polkovnik", new Rank("general_polkovnik", "Генерал-полковник", ChatColor.GOLD, 18, true));
        ruRanks.put("general_armii", new Rank("general_armii", "Генерал армии", ChatColor.GOLD, 19, true));
        ruRanks.put("marshal", new Rank("marshal", "Маршал Российской Федерации", ChatColor.RED, 20, true));
    }
    
    /**
     * Загружает конфигурацию рангов игроков
     */
    private void loadRanksConfig() {
        ranksFile = new File(plugin.getDataFolder(), "ranks.yml");
        
        if (!ranksFile.exists()) {
            try {
                ranksFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать файл ranks.yml: " + e.getMessage());
            }
        }
        
        ranksConfig = YamlConfiguration.loadConfiguration(ranksFile);
    }
    
    /**
     * Загружает ранги игроков из конфигурации
     */
    private void loadPlayerRanks() {
        ConfigurationSection playersSection = ranksConfig.getConfigurationSection("players");
        
        if (playersSection != null) {
            for (String uuidString : playersSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    String rankId = playersSection.getString(uuidString + ".rank");
                    String faction = playersSection.getString(uuidString + ".faction");
                    
                    if (rankId != null && faction != null) {
                        playerRanks.put(uuid, rankId);
                        playerFactions.put(uuid, faction);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Некорректный UUID игрока в конфигурации: " + uuidString);
                }
            }
        }
    }
    
    /**
     * Сохраняет ранги игроков в конфигурационный файл
     */
    public void savePlayerRanks() {
        ranksConfig.set("players", null);
        
        for (Map.Entry<UUID, String> entry : playerRanks.entrySet()) {
            UUID uuid = entry.getKey();
            String rankId = entry.getValue();
            String faction = playerFactions.get(uuid);
            
            if (faction != null) {
                ranksConfig.set("players." + uuid.toString() + ".rank", rankId);
                ranksConfig.set("players." + uuid.toString() + ".faction", faction);
            }
        }
        
        try {
            ranksConfig.save(ranksFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить файл ranks.yml: " + e.getMessage());
        }
    }
    
    /**
     * Устанавливает ранг и фракцию для игрока
     * 
     * @param player игрок, которому устанавливается ранг
     * @param rankId идентификатор ранга
     * @param faction фракция ("us" или "ru")
     * @return true, если ранг успешно установлен
     */
    public boolean setPlayerRank(Player player, String rankId, String faction) {
        if (!faction.equals("us") && !faction.equals("ru")) {
            return false;
        }
        
        Map<String, Rank> ranks = faction.equals("us") ? usRanks : ruRanks;
        
        if (!ranks.containsKey(rankId)) {
            return false;
        }
        
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(rankKey, PersistentDataType.STRING, rankId);
        pdc.set(factionKey, PersistentDataType.STRING, faction);
        
        playerRanks.put(player.getUniqueId(), rankId);
        playerFactions.put(player.getUniqueId(), faction);
        
        ranksConfig.set("players." + player.getUniqueId().toString() + ".rank", rankId);
        ranksConfig.set("players." + player.getUniqueId().toString() + ".faction", faction);
        
        try {
            ranksConfig.save(ranksFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить файл ranks.yml: " + e.getMessage());
        }
        
        updatePlayerDisplayName(player);
        
        return true;
    }
    
    /**
     * Получает ранг игрока
     * 
     * @param player игрок
     * @return объект ранга или null, если у игрока нет ранга
     */
    public Rank getPlayerRank(Player player) {
        String rankId = playerRanks.get(player.getUniqueId());
        String faction = playerFactions.get(player.getUniqueId());
        
        if (rankId == null || faction == null) {
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            
            if (pdc.has(rankKey, PersistentDataType.STRING) && 
                pdc.has(factionKey, PersistentDataType.STRING)) {
                
                rankId = pdc.get(rankKey, PersistentDataType.STRING);
                faction = pdc.get(factionKey, PersistentDataType.STRING);
                
                if (rankId != null && faction != null) {
                    playerRanks.put(player.getUniqueId(), rankId);
                    playerFactions.put(player.getUniqueId(), faction);
                }
            }
        }
        
        if (rankId != null && faction != null) {
            Map<String, Rank> ranks = faction.equals("us") ? usRanks : ruRanks;
            return ranks.get(rankId);
        }
        
        return null;
    }
    
    /**
     * Возвращает фракцию игрока ("us" или "ru")
     * 
     * @param player игрок
     * @return строка с названием фракции или null, если игрок не имеет фракции
     */
    public String getPlayerFaction(Player player) {
        String faction = playerFactions.get(player.getUniqueId());
        
        if (faction == null) {
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            
            if (pdc.has(factionKey, PersistentDataType.STRING)) {
                faction = pdc.get(factionKey, PersistentDataType.STRING);
                
                if (faction != null) {
                    playerFactions.put(player.getUniqueId(), faction);
                }
            }
        }
        
        return faction;
    }
    
    /**
     * Возвращает фракцию игрока по его UUID
     * 
     * @param uuid UUID игрока
     * @return строка с названием фракции или null, если игрок не имеет фракции
     */
    public String getPlayerFaction(UUID uuid) {
        String faction = playerFactions.get(uuid);
        
        if (faction == null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                return getPlayerFaction(player);
            }
            
            String uuidString = uuid.toString();
            if (ranksConfig.contains("players." + uuidString + ".faction")) {
                faction = ranksConfig.getString("players." + uuidString + ".faction");
                
                if (faction != null) {
                    playerFactions.put(uuid, faction);
                }
            }
        }
        
        return faction;
    }
    
    /**
     * Обновляет отображаемое имя игрока, добавляя префикс с рангом
     * 
     * @param player игрок для обновления
     */
    public void updatePlayerDisplayName(Player player) {
        Rank rank = getPlayerRank(player);
        String faction = getPlayerFaction(player);
        
        if (rank != null && faction != null) {
            ChatColor factionColor = faction.equals("us") ? ChatColor.BLUE : ChatColor.RED;
            String factionPrefix = faction.equals("us") ? "[США] " : "[РФ] ";
            
            player.setDisplayName(factionColor + factionPrefix + rank.getColor() + rank.getDisplayName() + " " + 
                    ChatColor.WHITE + player.getName());
            
            player.setPlayerListName(factionColor + factionPrefix + rank.getColor() + rank.getDisplayName() + " " + 
                    ChatColor.WHITE + player.getName());
        }
    }
    
    /**
     * Получает список всех рангов указанной фракции
     * 
     * @param faction фракция ("us" или "ru")
     * @return карта с идентификаторами рангов и их объектами
     */
    public Map<String, Rank> getRanks(String faction) {
        return faction.equals("us") ? usRanks : ruRanks;
    }
    
    /**
     * Проверяет, является ли указанный ранг командирским
     * 
     * @param faction фракция ("us" или "ru")
     * @param rankId идентификатор ранга
     * @return true, если ранг командирский
     */
    public boolean isCommanderRank(String faction, String rankId) {
        Map<String, Rank> ranks = faction.equals("us") ? usRanks : ruRanks;
        Rank rank = ranks.get(rankId);
        return rank != null && rank.isCommander();
    }
    
    /**
     * Получает список идентификаторов всех рангов указанной фракции
     * 
     * @param faction фракция ("us" или "ru")
     * @return список идентификаторов рангов
     */
    public List<String> getRankIds(String faction) {
        return new ArrayList<>(faction.equals("us") ? usRanks.keySet() : ruRanks.keySet());
    }
    
    /**
     * Обновляет ранги всех онлайн-игроков
     */
    public void updateAllPlayerRanks() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerDisplayName(player);
        }
    }
    
    /**
     * Проверяет, может ли игрок использовать планшет
     * 
     * @param player игрок для проверки
     * @return true, если игрок имеет право использовать планшет
     */
    public boolean canUseTablet(Player player) {
        Rank rank = getPlayerRank(player);
        
        if (rank == null) {
            return false;
        }
        
        return rank.isCommander();
    }
} 