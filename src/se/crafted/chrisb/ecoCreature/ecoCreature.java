package se.crafted.chrisb.ecoCreature;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import hawox.uquest.QuestInteraction;
import hawox.uquest.UQuest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
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

public class ecoCreature extends JavaPlugin
{
  private static Server Server;
  public static boolean uQuestEnabled = false;
  public static QuestInteraction questInteraction = null;
  public static PermissionHandler Permissions = null;
  public static final Logger logger = Logger.getLogger("Minecraft");
  private static ecoBlockListener blockListener;
  private static ecoEntityListener entityListener;
  private static ecoPlayerListener playerListener;
  private static ecoRewardHandler rewardHandler;

  private static boolean setupPermissions(Server paramServer)
  {
    Plugin localPlugin = paramServer.getPluginManager().getPlugin("Permissions");
    if (localPlugin != null)
      Permissions = ((Permissions)localPlugin).getHandler();
    else
      return false;
    return true;
  }

  public void onLoad()
  {
    getDataFolder().mkdir();
    getDataFolder().setWritable(true);
    getDataFolder().setExecutable(true);
    ecoConstants.Plugin_Directory = getDataFolder().getPath();
    logger.log(Level.INFO, "[ecoCreature] v0.0.5b (Spengebab) loaded.");
    extractSettings("ecoCreature.yml");
    try
    {
      ecoConstants.load(new Configuration(new File(getDataFolder(), "ecoCreature.yml")));
    }
    catch (Exception localException)
    {
      logger.log(Level.INFO, "[ecoCreature] Failed to retrieve configuration from directory.");
      logger.log(Level.INFO, "[ecoCreature] Please back up your current settings and let ecoCreature recreate it.");
      Server.getPluginManager().disablePlugin(this);
      return;
    }
  }

  public void onEnable()
  {
    Locale.setDefault(Locale.US);
    Server = getServer();
    if (!ecoConstants.SSA)
    {
      logger.log(Level.INFO, "[ecoCreature] Please configure ecoCreature (plugins/ecoCreature.yml) before continuing. Plugin disabled.");
      Server.getPluginManager().disablePlugin(this);
      return;
    }
    if (!setupPermissions(Server))
    {
      logger.log(Level.INFO, "[ecoCreature] Denied usage because Permissions can not be found.");
      Server.getPluginManager().disablePlugin(this);
      return;
    }
    if (!ecoEcon.initEcon(Server))
    {
      logger.log(Level.INFO, "[ecoCreature] Failed to find a supported economy plugin. ecoCreature disabled.");
      Server.getPluginManager().disablePlugin(this);
      return;
    }
    if ((ecoConstants.uQuestHooking) && (!setupUQuest(Server)))
    {
      logger.log(Level.INFO, "[ecoCreature] uQuest system enabled but not found. ecoCreature disabled.");
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    registerEvents();
  }

  private void registerEvents()
  {
    PluginManager localPluginManager = Server.getPluginManager();
    entityListener = new ecoEntityListener(Server);
    blockListener = new ecoBlockListener();
    playerListener = new ecoPlayerListener();
    rewardHandler = new ecoRewardHandler();
    localPluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
    localPluginManager.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Normal, this);
    localPluginManager.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
    localPluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
  }

  public void onDisable()
  {
  }

  public static boolean setupUQuest(Server paramServer)
  {
    Plugin localPlugin = paramServer.getPluginManager().getPlugin("uQuest");
    if (localPlugin != null)
      questInteraction = new QuestInteraction((UQuest)localPlugin);
    else
      return false;
    return true;
  }

  public QuestInteraction getQuestInteraction()
  {
    return questInteraction;
  }

  public static Server getBukkitServer()
  {
    return Server;
  }

  public static ecoRewardHandler getRewardHandler()
  {
    return rewardHandler;
  }

  public void extractSettings(String paramString)
  {
    File localFile = new File(getDataFolder(), paramString);
    if (!localFile.exists())
    {
      InputStream localInputStream = getClass().getResourceAsStream("/settings/" + paramString);
      if (localInputStream != null)
      {
        FileOutputStream localFileOutputStream = null;
        try
        {
          localFileOutputStream = new FileOutputStream(localFile);
          byte[] arrayOfByte = new byte[8192];
          int i = 0;
          while ((i = localInputStream.read(arrayOfByte)) > 0)
            localFileOutputStream.write(arrayOfByte, 0, i);
          logger.log(Level.INFO, "[ecoCreature] Default settings file written: " + paramString);
        }
        catch (Exception localException5)
        {
          localException5.printStackTrace();
          try
          {
            if (localInputStream != null)
              localInputStream.close();
          }
          catch (Exception localException6)
          {
          }
          try
          {
            if (localFileOutputStream != null)
              localFileOutputStream.close();
          }
          catch (Exception localException7)
          {
          }
        }
        finally
        {
          try
          {
            if (localInputStream != null)
              localInputStream.close();
          }
          catch (Exception localException8)
          {
          }
          try
          {
            if (localFileOutputStream != null)
              localFileOutputStream.close();
          }
          catch (Exception localException9)
          {
          }
        }
      }
    }
  }
}