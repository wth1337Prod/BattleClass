package me.wth.battleClass.weapons.ammo;

import me.wth.battleClass.BattleClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class AmmoManager {
    private final BattleClass plugin;
    private final Map<String, List<AmmoType>> ammoByWeapon = new HashMap<>();
    private final Map<UUID, Map<String, AmmoType>> playerAmmoSelection = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> playerAmmoCount = new HashMap<>();
    
    private final UdavAmmo udavAmmo;
    private final SigSauerM17Ammo sigSauerM17Ammo;
    
    public AmmoManager(BattleClass plugin) {
        this.plugin = plugin;
        this.udavAmmo = new UdavAmmo(plugin);
        this.sigSauerM17Ammo = new SigSauerM17Ammo(plugin);
        registerAmmoTypes();
    }
    
    private void registerAmmoTypes() {
        registerAmmoType(new AmmoType(
            "xm7_standard",
            "6.8×51 мм стандартный патрон",
            Arrays.asList(
                "§7Стандартный патрон калибра 6.8×51 мм",
                "§7Производитель: §fSIG Sauer",
                "§7Тип: §fСтандартный"
            ),
            Material.IRON_NUGGET,
            "xm7",
            0.0, 
            0.0, 
            0.0, 
            0.0, 
            0.0, 
            0.0  
        ));
        
        registerAmmoType(new AmmoType(
            "xm7_armor_piercing",
            "6.8×51 мм бронебойный патрон",
            Arrays.asList(
                "§7Бронебойный патрон калибра 6.8×51 мм",
                "§7Производитель: §fSIG Sauer",
                "§7Тип: §fБронебойный"
            ),
            Material.GOLD_NUGGET,
            "xm7",
            0.1,  
            0.3,  
            -0.05, 
            0.0,   
            0.1,   
            0.15   
        ));
        
        registerAmmoType(new AmmoType(
            "xm7_tracer",
            "6.8×51 мм трассирующий патрон",
            Arrays.asList(
                "§7Трассирующий патрон калибра 6.8×51 мм",
                "§7Производитель: §fSIG Sauer",
                "§7Тип: §fТрассирующий"
            ),
            Material.GLOWSTONE_DUST,
            "xm7",
            -0.05, 
            -0.1,  
            0.15,  
            0.1,   
            -0.05, 
            -0.1   
        ));
        
        registerAmmoType(new AmmoType(
            "ak12_standard",
            "5.45×39 мм стандартный патрон",
            Arrays.asList(
                "§7Стандартный патрон калибра 5.45×39 мм",
                "§7Производитель: §fИжмаш",
                "§7Тип: §fСтандартный"
            ),
            Material.IRON_NUGGET,
            "ak12",
            0.0, 
            0.0, 
            0.0, 
            0.0, 
            0.0, 
            0.0  
        ));
        
        registerAmmoType(new AmmoType(
            "ak12_armor_piercing",
            "5.45×39 мм бронебойный патрон БС",
            Arrays.asList(
                "§7Бронебойный патрон калибра 5.45×39 мм",
                "§7Производитель: §fИжмаш",
                "§7Тип: §fБронебойный БС"
            ),
            Material.GOLD_NUGGET,
            "ak12",
            0.15,  
            0.25,  
            -0.1,  
            -0.05, 
            0.15,  
            0.2    
        ));
        
        registerAmmoType(new AmmoType(
            "ak12_tracer",
            "5.45×39 мм трассирующий патрон Т",
            Arrays.asList(
                "§7Трассирующий патрон калибра 5.45×39 мм",
                "§7Производитель: §fИжмаш",
                "§7Тип: §fТрассирующий Т"
            ),
            Material.GLOWSTONE_DUST,
            "ak12",
            -0.05, 
            -0.05, 
            0.1,   
            0.05,  
            -0.1,  
            -0.05  
        ));
        
        registerAmmoType(new AmmoType(
            "ak12_subsonic",
            "5.45×39 мм дозвуковой патрон УС",
            Arrays.asList(
                "§7Дозвуковой патрон калибра 5.45×39 мм",
                "§7Производитель: §fИжмаш",
                "§7Тип: §fДозвуковой УС",
                "§7Идеален для использования с глушителем"
            ),
            Material.PRISMARINE_CRYSTALS,
            "ak12",
            -0.2,  
            -0.1,  
            0.05,  
            -0.15, 
            -0.05, 
            -0.3   
        ));
        
        registerAmmoType(new AmmoType(
            "udav_standard",
            "9×21 мм стандартный патрон",
            Arrays.asList(
                "§7Стандартный патрон калибра 9×21 мм",
                "§7Производитель: §fЦНИИТОЧМАШ",
                "§7Тип: §fСтандартный"
            ),
            Material.IRON_NUGGET,
            "udav",
            0.0, 
            0.0, 
            0.0, 
            0.0, 
            0.0, 
            0.0  
        ));
        
        registerAmmoType(new AmmoType(
            "udav_ap",
            "9×21 мм бронебойный патрон СП-12",
            Arrays.asList(
                "§7Бронебойный патрон калибра 9×21 мм",
                "§7Производитель: §fЦНИИТОЧМАШ",
                "§7Тип: §fБронебойный СП-12"
            ),
            Material.GOLD_NUGGET,
            "udav",
            0.15,  
            0.25,  
            -0.05, 
            -0.0,  
            0.1,   
            0.15   
        ));
        
        registerAmmoType(new AmmoType(
            "udav_expansive",
            "9×21 мм экспансивный патрон",
            Arrays.asList(
                "§7Экспансивный патрон калибра 9×21 мм",
                "§7Производитель: §fЦНИИТОЧМАШ",
                "§7Тип: §fЭкспансивный",
                "§7Увеличивает урон по мягким целям"
            ),
            Material.COPPER_INGOT,
            "udav",
            0.25,  
            -0.2,  
            0.05,  
            -0.0,  
            -0.05, 
            -0.1   
        ));
        
        registerAmmoType(new AmmoType(
            "sigm17_standard",
            "9×19 мм стандартный патрон",
            Arrays.asList(
                "§7Стандартный патрон калибра 9×19 мм",
                "§7Производитель: §fSig Sauer",
                "§7Тип: §fСтандартный"
            ),
            Material.IRON_NUGGET,
            "sigm17",
            0.0, 
            0.0, 
            0.0, 
            0.0, 
            0.0, 
            0.0  
        ));
        
        registerAmmoType(new AmmoType(
            "sigm17_ap",
            "9×19 мм бронебойный патрон M1152",
            Arrays.asList(
                "§7Бронебойный патрон калибра 9×19 мм",
                "§7Производитель: §fSig Sauer",
                "§7Тип: §fБронебойный M1152"
            ),
            Material.GOLD_NUGGET,
            "sigm17",
            0.1,   
            0.2,   
            -0.05, 
            0.0,   
            0.1,   
            0.1    
        ));
        
        registerAmmoType(new AmmoType(
            "sigm17_jhp",
            "9×19 мм JHP патрон M1153",
            Arrays.asList(
                "§7Экспансивный патрон калибра 9×19 мм",
                "§7Производитель: §fSig Sauer",
                "§7Тип: §fЭкспансивный JHP M1153",
                "§7Увеличивает урон по мягким целям"
            ),
            Material.COPPER_INGOT,
            "sigm17",
            0.3,   
            -0.25, 
            0.05,  
            -0.0,  
            -0.0,  
            -0.05  
        ));
        
        registerAmmoType(new AmmoType(
            "sigm17_subsonic",
            "9×19 мм дозвуковой патрон",
            Arrays.asList(
                "§7Дозвуковой патрон калибра 9×19 мм",
                "§7Производитель: §fSig Sauer",
                "§7Тип: §fДозвуковой",
                "§7Идеален для использования с глушителем"
            ),
            Material.PRISMARINE_CRYSTALS,
            "sigm17",
            -0.15, 
            -0.1,  
            0.1,   
            -0.15, 
            -0.0,  
            -0.25  
        ));
    }
    
    private void registerAmmoType(AmmoType ammoType) {
        String weaponId = ammoType.getWeaponId();
        ammoByWeapon.computeIfAbsent(weaponId, k -> new ArrayList<>()).add(ammoType);
    }
    
    public List<AmmoType> getAmmoTypesForWeapon(String weaponId) {
        return ammoByWeapon.getOrDefault(weaponId, Collections.emptyList());
    }
    
    public AmmoType getAmmoType(String weaponId, String ammoId) {
        for (AmmoType ammoType : getAmmoTypesForWeapon(weaponId)) {
            if (ammoType.getId().equals(ammoId)) {
                return ammoType;
            }
        }
        return null;
    }
    
    /**
     * Получает список всех типов боеприпасов для всех видов оружия
     * @return список всех типов боеприпасов
     */
    public List<AmmoType> getAllAmmoTypes() {
        List<AmmoType> allAmmoTypes = new ArrayList<>();
        for (List<AmmoType> weaponAmmoTypes : ammoByWeapon.values()) {
            allAmmoTypes.addAll(weaponAmmoTypes);
        }
        return allAmmoTypes;
    }
    
    /**
     * Получает список идентификаторов всех типов боеприпасов
     * @return список идентификаторов всех типов боеприпасов
     */
    public List<String> getAmmoTypeNames() {
        List<String> ammoTypeNames = new ArrayList<>();
        for (AmmoType ammoType : getAllAmmoTypes()) {
            ammoTypeNames.add(ammoType.getId());
        }
        return ammoTypeNames;
    }
    
    public void giveAmmoToPlayer(Player player, String ammoId, int amount) {
        for (List<AmmoType> ammoTypes : ammoByWeapon.values()) {
            for (AmmoType ammoType : ammoTypes) {
                if (ammoType.getId().equals(ammoId)) {
                    player.getInventory().addItem(ammoType.createItemStack(amount));
                    player.sendMessage("§aВы получили " + amount + " §f" + ammoType.getDisplayName());
                    
                    UUID playerUUID = player.getUniqueId();
                    playerAmmoCount.computeIfAbsent(playerUUID, k -> new HashMap<>())
                            .merge(ammoId, amount, Integer::sum);
                            
                    String weaponId = ammoType.getWeaponId();
                    if (!playerAmmoSelection.containsKey(playerUUID)) {
                        playerAmmoSelection.put(playerUUID, new HashMap<>());
                    }
                    
                    Map<String, AmmoType> selectedAmmo = playerAmmoSelection.get(playerUUID);
                    if (!selectedAmmo.containsKey(weaponId)) {
                        selectedAmmo.put(weaponId, ammoType);
                    }
                    
                    return;
                }
            }
        }
        
        player.sendMessage("§cПатроны с ID " + ammoId + " не найдены!");
    }
    
    public AmmoType getSelectedAmmo(Player player, String weaponId) {
        UUID playerUUID = player.getUniqueId();
        
        if (!playerAmmoSelection.containsKey(playerUUID) || 
            !playerAmmoSelection.get(playerUUID).containsKey(weaponId)) {
            
            AmmoType standardAmmo = null;
            for (AmmoType ammoType : getAmmoTypesForWeapon(weaponId)) {
                if (ammoType.getId().endsWith("_standard")) {
                    standardAmmo = ammoType;
                    break;
                }
            }
            
            if (standardAmmo != null) {
                int ammoCount = getAmmoCount(player, standardAmmo.getId());
                if (ammoCount > 0) {
                    playerAmmoSelection.computeIfAbsent(playerUUID, k -> new HashMap<>())
                        .put(weaponId, standardAmmo);
                } else {
                    for (AmmoType ammoType : getAmmoTypesForWeapon(weaponId)) {
                        int count = getAmmoCount(player, ammoType.getId());
                        if (count > 0) {
                            playerAmmoSelection.computeIfAbsent(playerUUID, k -> new HashMap<>())
                                .put(weaponId, ammoType);
                            break;
                        }
                    }
                }
            }
        }
        
        return playerAmmoSelection.getOrDefault(playerUUID, Collections.emptyMap())
            .getOrDefault(weaponId, null);
    }
    
    public void selectAmmo(Player player, String weaponId, String ammoId) {
        AmmoType ammoType = getAmmoType(weaponId, ammoId);
        
        if (ammoType != null) {
            UUID playerUUID = player.getUniqueId();
            playerAmmoSelection.computeIfAbsent(playerUUID, k -> new HashMap<>())
                .put(weaponId, ammoType);
                
            player.sendMessage("§aВыбран тип патронов: §f" + ammoType.getDisplayName());
        } else {
            player.sendMessage("§cПатроны с ID " + ammoId + " для оружия " + weaponId + " не найдены!");
        }
    }
    
    public int getAmmoCount(Player player, String ammoId) {
        UUID playerUUID = player.getUniqueId();
        return playerAmmoCount.getOrDefault(playerUUID, Collections.emptyMap())
                .getOrDefault(ammoId, 0);
    }
    
    public boolean useAmmo(Player player, String ammoId, int amount) {
        UUID playerUUID = player.getUniqueId();
        Map<String, Integer> ammoCount = playerAmmoCount.getOrDefault(playerUUID, Collections.emptyMap());
        int currentAmount = ammoCount.getOrDefault(ammoId, 0);
        
        if (currentAmount >= amount) {
            ammoCount.put(ammoId, currentAmount - amount);
            return true;
        }
        
        return false;
    }
    
    public int getAmmoCountForWeapon(Player player, String weaponId) {
        int totalAmmo = 0;
        
        for (AmmoType ammoType : getAmmoTypesForWeapon(weaponId)) {
            totalAmmo += getAmmoCount(player, ammoType.getId());
        }
        
        return totalAmmo;
    }
    
    public UdavAmmo getUdavAmmo() {
        return udavAmmo;
    }
    
    public SigSauerM17Ammo getSigSauerM17Ammo() {
        return sigSauerM17Ammo;
    }
} 