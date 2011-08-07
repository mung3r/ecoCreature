package se.crafted.chrisb.ecoCreature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import se.crafted.chrisb.ecoCreature.entities.ecoBlockListener;
import se.crafted.chrisb.ecoCreature.entities.ecoEntityListener;
import se.crafted.chrisb.ecoCreature.entities.ecoPlayerListener;
import se.crafted.chrisb.ecoCreature.entities.ecoRewardHandler;
import se.crafted.chrisb.ecoCreature.utils.ecoConstants;
import se.crafted.chrisb.ecoCreature.utils.ecoEcon;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class ecoCreature extends JavaPlugin
{
    private final ecoBlockListener blockListener = new ecoBlockListener(this);
    private final ecoEntityListener entityListener = new ecoEntityListener(this);
    private final ecoPlayerListener playerListener = new ecoPlayerListener(this);
    private final ecoRewardHandler rewardHandler = new ecoRewardHandler(this);

    public static PermissionHandler permissionsHandler = null;
    public static final File dataFolder = new File("plugins" + File.separator + "ecoCreature");
    public static final Logger logger = Logger.getLogger("Minecraft");

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

    public void onLoad()
    {
        dataFolder.mkdirs();
        ecoConstants.pluginDirectory = dataFolder.getPath();
    }

    public void onEnable()
    {
        Locale.setDefault(Locale.US);
        extractSettings("ecoCreature.yml");
        
        try {
            ecoConstants.load(new Configuration(new File(dataFolder, "ecoCreature.yml")));
        }
        catch (Exception exception) {
            logger.log(Level.SEVERE, "[ecoCreature] Failed to retrieve configuration from directory.");
            logger.log(Level.SEVERE, "[ecoCreature] Please back up your current settings and let ecoCreature recreate it.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!ecoConstants.isConfigurationEnabled) {
            logger.log(Level.SEVERE, "[ecoCreature] Please configure ecoCreature (plugins/ecoCreature.yml) before continuing. Plugin disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!setupPermissions()) {
            logger.log(Level.SEVERE, "[ecoCreature] Denied usage because Permissions can not be found.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!ecoEcon.initEcon(getServer())) {
            logger.log(Level.SEVERE, "[ecoCreature] Failed to find a supported economy plugin. ecoCreature disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerEvents();
        logger.log(Level.INFO, "[ecoCreature] " + getDescription().getVersion() + " enabled.");
    }

    private void registerEvents()
    {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
    }

    public void onDisable()
    {
        permissionsHandler = null;
        logger.log(Level.INFO, "[ecoCreature] " + getDescription().getVersion() + " is disabled.");
    }

    public ecoRewardHandler getRewardHandler()
    {
        return rewardHandler;
    }

    public void extractSettings(String filename)
    {
        File file = new File(dataFolder, filename);
        if (!file.exists()) {
            InputStream inputStream = getClass().getResourceAsStream("/settings/" + filename);
            if (inputStream != null) {
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(file);
                    byte[] arrayOfByte = new byte[8192];
                    int i = 0;
                    while ((i = inputStream.read(arrayOfByte)) > 0)
                        fileOutputStream.write(arrayOfByte, 0, i);
                    logger.log(Level.INFO, "[ecoCreature] Default settings file written: " + filename);
                }
                catch (Exception localException5) {
                    localException5.printStackTrace();
                    try {
                        if (inputStream != null)
                            inputStream.close();
                    }
                    catch (Exception localException6) {
                    }
                    try {
                        if (fileOutputStream != null)
                            fileOutputStream.close();
                    }
                    catch (Exception localException7) {
                    }
                }
                finally {
                    try {
                        if (inputStream != null)
                            inputStream.close();
                    }
                    catch (Exception localException8) {
                    }
                    try {
                        if (fileOutputStream != null)
                            fileOutputStream.close();
                    }
                    catch (Exception localException9) {
                    }
                }
            }
        }
    }
}