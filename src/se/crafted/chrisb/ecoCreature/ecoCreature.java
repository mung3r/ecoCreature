package se.crafted.chrisb.ecoCreature;

import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import se.crafted.chrisb.ecoCreature.listeners.ecoBlockListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoEntityListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoPlayerListener;
import se.crafted.chrisb.ecoCreature.managers.ecoConfigManager;
import se.crafted.chrisb.ecoCreature.managers.ecoMessageManager;
import se.crafted.chrisb.ecoCreature.managers.ecoRewardManager;

public class ecoCreature extends JavaPlugin
{

    private final ecoBlockListener blockListener = new ecoBlockListener(this);
    private final ecoEntityListener entityListener = new ecoEntityListener(this);
    private final ecoPlayerListener playerListener = new ecoPlayerListener(this);

    public static final File dataFolder = new File("plugins" + File.separator + "ecoCreature");
    public static final Logger logger = Logger.getLogger("Minecraft");

    public static Permission permission = null;
    public static Economy economy = null;

    private ecoMessageManager messageManager;
    private ecoRewardManager rewardManager;
    private ecoConfigManager configManager;

    private Boolean setupPermission()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        return (permission != null);
    }

    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    private void registerEvents()
    {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
    }

    public ecoMessageManager getMessageManager()
    {
        return messageManager;
    }

    public ecoRewardManager getRewardManager()
    {
        return rewardManager;
    }

    public ecoConfigManager getConfigManager()
    {
        return configManager;
    }

    public void onLoad()
    {
        dataFolder.mkdirs();
    }

    public void onEnable()
    {
        Locale.setDefault(Locale.US);

        messageManager = new ecoMessageManager(this);
        rewardManager = new ecoRewardManager(this);

        try {
            configManager = new ecoConfigManager(this);
            configManager.load();
        }
        catch (Exception exception) {
            logger.log(Level.SEVERE, "[ecoCreature] Failed to retrieve configuration from directory.");
            logger.log(Level.SEVERE, "[ecoCreature] Please back up your current settings and let ecoCreature recreate it.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        if (!configManager.isEnabled()) {
            logger.log(Level.SEVERE, "[ecoCreature] Please configure ecoCreature (plugins/ecoCreature.yml) before continuing. Plugin disabled.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        if (!setupPermission()) {
            logger.log(Level.SEVERE, "[ecoCreature] Permission plugin not found. Plugin disabled.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        if (!setupEconomy()) {
            logger.log(Level.SEVERE, "[ecoCreature] Economy plugin not found. Plugin disabled.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        registerEvents();

        logger.log(Level.INFO, "[ecoCreature] " + getDescription().getVersion() + " enabled.");
    }

    public void onDisable()
    {
        permission = null;
        economy = null;
        configManager = null;
        rewardManager = null;
        messageManager = null;
        logger.log(Level.INFO, "[ecoCreature] " + getDescription().getVersion() + " is disabled.");
    }
}