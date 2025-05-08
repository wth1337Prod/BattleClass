package me.wth.battleClass;

import me.wth.battleClass.armor.ArmorManager;
import me.wth.battleClass.armor.BootsManager;
import me.wth.battleClass.armor.HelmetManager;
import me.wth.battleClass.armor.PantsManager;
import me.wth.battleClass.commands.BattleClassCommand;
import me.wth.battleClass.commands.DroneCommand;
import me.wth.battleClass.commands.MortarCommand;
import me.wth.battleClass.commands.RadioCommand;
import me.wth.battleClass.commands.RankCommand;
import me.wth.battleClass.donations.DonateCommand;
import me.wth.battleClass.donations.DonateGUI;
import me.wth.battleClass.donations.DonateListener;
import me.wth.battleClass.drones.DroneListener;
import me.wth.battleClass.drones.DroneManager;
import me.wth.battleClass.grenades.GrenadeManager;
import me.wth.battleClass.hud.AmmoHUD;
import me.wth.battleClass.listeners.ArmorListener;
import me.wth.battleClass.listeners.BootsListener;
import me.wth.battleClass.listeners.DeathMessageListener;
import me.wth.battleClass.listeners.GrenadeListener;
import me.wth.battleClass.listeners.HelmetListener;
import me.wth.battleClass.listeners.InjuryDeathListener;
import me.wth.battleClass.listeners.MedicalListener;
import me.wth.battleClass.mines.MineListener;
import me.wth.battleClass.listeners.PantsListener;
import me.wth.battleClass.listeners.RationListener;
import me.wth.battleClass.mortars.MortarChatListener;
import me.wth.battleClass.mortars.MortarListener;
import me.wth.battleClass.mortars.MortarManager;
import me.wth.battleClass.radio.RadioListener;
import me.wth.battleClass.radio.RadioManager;
import me.wth.battleClass.ranks.RankListener;
import me.wth.battleClass.ranks.RankManager;
import me.wth.battleClass.weapons.WeaponListener;
import me.wth.battleClass.medical.InjuryManager;
import me.wth.battleClass.medical.MedicalManager;
import me.wth.battleClass.mines.MineManager;
import me.wth.battleClass.rations.RationManager;
import me.wth.battleClass.tablet.TabletGUI;
import me.wth.battleClass.tablet.TabletListener;
import me.wth.battleClass.tablet.TabletManager;
import me.wth.battleClass.weapons.WeaponManager;
import me.wth.battleClass.weapons.ammo.AmmoManager;
import me.wth.battleClass.flamethrowers.FlamethrowerManager;
import me.wth.battleClass.flamethrowers.FlamethrowerListener;
import me.wth.battleClass.commands.FlamethrowerCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BattleClass extends JavaPlugin {
    private WeaponManager weaponManager;
    private AmmoManager ammoManager;
    private ArmorManager armorManager;
    private HelmetManager helmetManager;
    private BootsManager bootsManager;
    private PantsManager pantsManager;
    private MedicalManager medicalManager;
    private InjuryManager injuryManager;
    private GrenadeManager grenadeManager;
    private RationManager rationManager;
    private MineManager mineManager;
    private TabletManager tabletManager;
    private RankManager rankManager;
    private RadioManager radioManager;
    private MortarManager mortarManager;
    private FlamethrowerManager flamethrowerManager;
    private DroneManager droneManager;
    
    private WeaponListener weaponListener;
    private ArmorListener armorListener;
    private HelmetListener helmetListener;
    private BootsListener bootsListener;
    private PantsListener pantsListener;
    private MedicalListener medicalListener;
    private GrenadeListener grenadeListener;
    private RationListener rationListener;
    private MineListener mineListener;
    private InjuryDeathListener injuryDeathListener;
    private DeathMessageListener deathMessageListener;
    private TabletListener tabletListener;
    private RankListener rankListener;
    private DonateListener donateListener;
    private RadioListener radioListener;
    private MortarListener mortarListener;
    private MortarChatListener mortarChatListener;
    private FlamethrowerListener flamethrowerListener;
    private DroneListener droneListener;
    
    private AmmoHUD ammoHUD;
    private TabletGUI tabletGUI;
    private DonateGUI donateGUI;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        weaponManager = new WeaponManager(this);
        ammoManager = new AmmoManager(this);
        armorManager = new ArmorManager(this);
        helmetManager = new HelmetManager(this);
        bootsManager = new BootsManager(this);
        pantsManager = new PantsManager(this);
        medicalManager = new MedicalManager(this);
        injuryManager = new InjuryManager(this);
        grenadeManager = new GrenadeManager(this);
        rationManager = new RationManager(this);
        mineManager = new MineManager(this);
        tabletManager = new TabletManager(this);
        rankManager = new RankManager(this);
        radioManager = new RadioManager(this);
        mortarManager = new MortarManager(this);
        flamethrowerManager = new FlamethrowerManager(this);
        droneManager = new DroneManager(this);
        
        tabletGUI = new TabletGUI(this);
        donateGUI = new DonateGUI(this);
        
        weaponListener = new WeaponListener(this, ammoManager, armorManager, helmetManager);
        armorListener = new ArmorListener(this);
        helmetListener = new HelmetListener(this);
        bootsListener = new BootsListener(this);
        pantsListener = new PantsListener(this);
        medicalListener = new MedicalListener(this, medicalManager, injuryManager);
        grenadeListener = new GrenadeListener(this, grenadeManager);
        rationListener = new RationListener(this, rationManager);
        mineListener = new MineListener(this, mineManager);
        
        injuryDeathListener = new InjuryDeathListener(this, injuryManager);
        
        deathMessageListener = new DeathMessageListener(this, weaponListener);
        
        tabletListener = new TabletListener(this, tabletManager, tabletGUI);
        
        rankListener = new RankListener(this, rankManager, tabletManager);
        
        donateListener = new DonateListener(this, donateGUI);
        
        radioListener = new RadioListener(this, radioManager);
        
        mortarListener = new MortarListener(this, mortarManager);
        mortarChatListener = new MortarChatListener(mortarListener);
        
        flamethrowerListener = new FlamethrowerListener(this, flamethrowerManager);
        
        droneListener = new DroneListener(this, droneManager);
        
        ammoHUD = new AmmoHUD(this, weaponListener, ammoManager);
        
        BattleClassCommand battleClassCommand = new BattleClassCommand(this, weaponManager, ammoManager, 
                armorManager, helmetManager, medicalManager, tabletManager, mineManager);
        getCommand("battleclass").setExecutor(battleClassCommand);
        getCommand("battleclass").setTabCompleter(battleClassCommand);
        
        RankCommand rankCommand = new RankCommand(this, rankManager);
        getCommand("rank").setExecutor(rankCommand);
        getCommand("rank").setTabCompleter(rankCommand);
        
        DonateCommand donateCommand = new DonateCommand(this, donateGUI);
        getCommand("donate").setExecutor(donateCommand);
        
        RadioCommand radioCommand = new RadioCommand(this, radioManager);
        getCommand("r").setExecutor(radioCommand);
        getCommand("r").setTabCompleter(radioCommand);
        
        MortarCommand mortarCommand = new MortarCommand(this, mortarManager);
        getCommand("mortar").setExecutor(mortarCommand);
        getCommand("mortar").setTabCompleter(mortarCommand);
        
        FlamethrowerCommand flamethrowerCommand = new FlamethrowerCommand(this, flamethrowerManager);
        getCommand("flamethrower").setExecutor(flamethrowerCommand);
        getCommand("flamethrower").setTabCompleter(flamethrowerCommand);
        
        DroneCommand droneCommand = new DroneCommand(this, droneManager);
        getCommand("drone").setExecutor(droneCommand);
        getCommand("drone").setTabCompleter(droneCommand);
        
        getServer().getPluginManager().registerEvents(weaponListener, this);
        getServer().getPluginManager().registerEvents(armorListener, this);
        getServer().getPluginManager().registerEvents(helmetListener, this);
        getServer().getPluginManager().registerEvents(bootsListener, this);
        getServer().getPluginManager().registerEvents(pantsListener, this);
        getServer().getPluginManager().registerEvents(medicalListener, this);
        getServer().getPluginManager().registerEvents(grenadeListener, this);
        getServer().getPluginManager().registerEvents(rationListener, this);
        getServer().getPluginManager().registerEvents(mineListener, this);
        getServer().getPluginManager().registerEvents(injuryDeathListener, this);
        getServer().getPluginManager().registerEvents(deathMessageListener, this);
        getServer().getPluginManager().registerEvents(tabletListener, this);
        getServer().getPluginManager().registerEvents(rankListener, this);
        getServer().getPluginManager().registerEvents(donateListener, this);
        getServer().getPluginManager().registerEvents(radioListener, this);
        getServer().getPluginManager().registerEvents(mortarListener, this);
        getServer().getPluginManager().registerEvents(mortarChatListener, this);
        getServer().getPluginManager().registerEvents(flamethrowerListener, this);
        getServer().getPluginManager().registerEvents(droneListener, this);
        
        startMineExpirationChecker();
        
        getLogger().info("BattleClass плагин успешно запущен");
    }

    /**
     * Запускает задачу для периодической проверки срока действия мин
     */
    private void startMineExpirationChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                mineManager.checkMinesToExpire();
            }
        }.runTaskTimer(this, 300L, 300L); 
    }

    @Override
    public void onDisable() {
        if (rankManager != null) {
            rankManager.savePlayerRanks();
        }
        
        if (ammoHUD != null) {
            ammoHUD.cleanup();
        }
        
        getLogger().info("BattleClass плагин успешно отключен");
    }
    
    public WeaponManager getWeaponManager() {
        return weaponManager;
    }
    
    public AmmoManager getAmmoManager() {
        return ammoManager;
    }
    
    public ArmorManager getArmorManager() {
        return armorManager;
    }
    
    public HelmetManager getHelmetManager() {
        return helmetManager;
    }
    
    /**
     * Получение менеджера ботинок
     * @return экземпляр BootsManager
     */
    public BootsManager getBootsManager() {
        return bootsManager;
    }
    
    /**
     * Получение менеджера штанов
     * @return экземпляр PantsManager
     */
    public PantsManager getPantsManager() {
        return pantsManager;
    }
    
    /**
     * Получение менеджера медицинских предметов
     * @return экземпляр MedicalManager
     */
    public MedicalManager getMedicalManager() {
        return medicalManager;
    }
    
    /**
     * Получение менеджера травм
     * @return экземпляр InjuryManager
     */
    public InjuryManager getInjuryManager() {
        return injuryManager;
    }
    
    /**
     * Получение менеджера гранат
     * @return экземпляр GrenadeManager
     */
    public GrenadeManager getGrenadeManager() {
        return grenadeManager;
    }
    
    /**
     * Получение менеджера сухих пайков
     * @return экземпляр RationManager
     */
    public RationManager getRationManager() {
        return rationManager;
    }

    /**
     * Получение менеджера мин
     * @return экземпляр MineManager
     */
    public MineManager getMineManager() {
        return mineManager;
    }
    
    /**
     * Получение слушателя событий оружия
     * @return экземпляр WeaponListener
     */
    public WeaponListener getListeners() {
        return weaponListener;
    }
    
    /**
     * Получение менеджера планшетов
     * @return экземпляр TabletManager
     */
    public TabletManager getTabletManager() {
        return tabletManager;
    }
    
    /**
     * Получение GUI планшета
     * @return экземпляр TabletGUI
     */
    public TabletGUI getTabletGUI() {
        return tabletGUI;
    }
    
    /**
     * Получение слушателя событий планшета
     * @return экземпляр TabletListener
     */
    public TabletListener getTabletListener() {
        return tabletListener;
    }
    
    /**
     * Получение менеджера рангов
     * @return экземпляр RankManager
     */
    public RankManager getRankManager() {
        return rankManager;
    }
    
    /**
     * Получает GUI для донатов
     * @return экземпляр DonateGUI
     */
    public DonateGUI getDonateGUI() {
        return donateGUI;
    }
    
    public RadioManager getRadioManager() {
        return radioManager;
    }
    
    public MortarManager getMortarManager() {
        return mortarManager;
    }
    
    public MortarListener getMortarListener() {
        return mortarListener;
    }
    
    /**
     * Получение менеджера огнеметов
     * @return экземпляр FlamethrowerManager
     */
    public FlamethrowerManager getFlamethrowerManager() {
        return flamethrowerManager;
    }
    
    /**
     * Получение слушателя огнеметов
     * @return экземпляр FlamethrowerListener
     */
    public FlamethrowerListener getFlamethrowerListener() {
        return flamethrowerListener;
    }
    
    /**
     * Получает экземпляр менеджера дронов
     * 
     * @return экземпляр DroneManager
     */
    public DroneManager getDroneManager() {
        return droneManager;
    }
    
    /**
     * Получает экземпляр слушателя дронов
     * 
     * @return экземпляр DroneListener
     */
    public DroneListener getDroneListener() {
        return droneListener;
    }
}
