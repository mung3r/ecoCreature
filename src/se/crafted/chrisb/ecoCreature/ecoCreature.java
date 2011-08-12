package se.crafted.chrisb.ecoCreature;

import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import se.crafted.chrisb.ecoCreature.listeners.ecoBlockListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoEntityListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoPlayerListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoPluginListener;
import se.crafted.chrisb.ecoCreature.managers.ecoConfigManager;
import se.crafted.chrisb.ecoCreature.managers.ecoRewardManager;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijikokun.register.payment.Method;

public class ecoCreature extends JavaPlugin
{

    private final ecoBlockListener blockListener = new ecoBlockListener(this);
    private final ecoEntityListener entityListener = new ecoEntityListener(this);
    private final ecoPlayerListener playerListener = new ecoPlayerListener(this);
    private final ecoPluginListener pluginListener = new ecoPluginListener(this);

    public static final File dataFolder = new File("plugins" + File.separator + "ecoCreature");
    public static final Logger logger = Logger.getLogger("Minecraft");
    public static PermissionHandler permissionsHandler = null;

    private ecoConfigManager configManager;
    private ecoRewardManager rewardManager;

    public Method method = null;

    private boolean setupPermissions()
    {
        Plugin testPlugin = getServer().getPluginManager().getPlugin("Permissions");

        if (permissionsHandler == null) {
            if (testPlugin != null) {
                permissionsHandler = ((Permissions) testPlugin).getHandler();
                return true;
            }
        }

        return false;
    }

    private void registerEvents()
    {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);

        pluginManager.registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);
        pluginManager.registerEvent(Type.PLUGIN_DISABLE, pluginListener, Priority.Monitor, this);
    }

    public ecoConfigManager getConfigManager()
    {
        return configManager;
    }

    public ecoRewardManager getRewardManager()
    {
        return rewardManager;
    }

    public void onLoad()
    {
        dataFolder.mkdirs();
    }

    public void onEnable()
    {
        Locale.setDefault(Locale.US);

        try {
            configManager = new ecoConfigManager(this);
            configManager.load();
        }
        catch (Exception exception) {
            logger.log(Level.SEVERE, "[ecoCreature] Failed to retrieve configuration from directory.");
            logger.log(Level.SEVERE, "[ecoCreature] Please back up your current settings and let ecoCreature recreate it.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!configManager.isEnabled()) {
            logger.log(Level.SEVERE, "[ecoCreature] Please configure ecoCreature (plugins/ecoCreature.yml) before continuing. Plugin disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupPermissions()) {
            logger.log(Level.SEVERE, "[ecoCreature] Denied usage because Permissions can not be found.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        rewardManager = new ecoRewardManager(this);
        registerEvents();

        logger.log(Level.INFO, "[ecoCreature] " + getDescription().getVersion() + " enabled.");
    }

    public void onDisable()
    {
        permissionsHandler = null;
        method = null;
        configManager = null;
        logger.log(Level.INFO, "[ecoCreature] " + getDescription().getVersion() + " is disabled.");
    }
}