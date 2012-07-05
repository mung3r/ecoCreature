package se.crafted.chrisb.ecoCreature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.simiancage.DeathTpPlus.DeathTpPlus;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaHandler;
import com.herocraftonline.heroes.Heroes;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import se.crafted.chrisb.ecoCreature.listeners.ecoBlockListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoEntityListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoDeathListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoStreakListener;
import se.crafted.chrisb.ecoCreature.managers.ecoConfigManager;
import se.crafted.chrisb.ecoCreature.managers.ecoMessageManager;
import se.crafted.chrisb.ecoCreature.managers.ecoRewardManager;
import se.crafted.chrisb.ecoCreature.utils.ecoLogger;
import se.crafted.chrisb.ecoCreature.utils.ecoMetrics;

public class ecoCreature extends JavaPlugin
{
    public static final File DATA_FOLDER = new File("plugins" + File.separator + "ecoCreature");
    public static final File DATA_WORLDS_FOLDER = new File(DATA_FOLDER, "worlds");

    public static Permission permission = null;
    public static Economy economy = null;
    public static DeathTpPlus deathTpPlusPlugin = null;
    public static MobArenaHandler mobArenaHandler = null;
    public static Heroes heroesPlugin = null;
    public static WorldGuardPlugin worldGuardPlugin = null;

    private static ecoLogger logger = new ecoLogger();
    public static Map<String, ecoMessageManager> messageManagers;
    public static Map<String, ecoRewardManager> rewardManagers;
    private ecoConfigManager configManager;
    private ecoMetrics metrics;

    private boolean setupDependencies()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            logger.info("Found permissions provider.");
        }
        else {
            logger.severe("Failed to load permission provider.");
        }

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            logger.info("Economy enabled.");
        }
        else {
            logger.warning("Economy disabled.");
        }

        return (permission != null);
    }

    private void setupDeathTpPlus()
    {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("DeathTpPlus");
        if (plugin instanceof DeathTpPlus) {
            DeathTpPlus testPlugin = (DeathTpPlus) plugin;
            if (testPlugin.getDescription().getVersion().startsWith("1.95")) {
                deathTpPlusPlugin = testPlugin;
                logger.info("Successfully hooked " + plugin.getDescription().getName());
            }
        }
    }

    private void setupMobArenaHandler()
    {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("MobArena");
        if (plugin instanceof MobArena) {
            mobArenaHandler = new MobArenaHandler();
            logger.info("Successfully hooked " + plugin.getDescription().getName());
        }
    }

    private void setupHeroes()
    {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Heroes");
        if (plugin instanceof Heroes) {
            heroesPlugin = (Heroes) plugin;
            logger.info("Successfully hooked " + plugin.getDescription().getName());
        }
    }

    private void setupWorldGuard()
    {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin instanceof WorldGuardPlugin) {
            worldGuardPlugin = (WorldGuardPlugin) plugin;
            logger.info("Successfully hooked " + plugin.getDescription().getName());
        }
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new ecoBlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new ecoEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new ecoDeathListener(), this);
        if (deathTpPlusPlugin != null) {
            Bukkit.getPluginManager().registerEvents(new ecoStreakListener(), this);
        }
    }

    public void onEnable()
    {
        Locale.setDefault(Locale.US);

        logger.setName(this.getDescription().getName());
        messageManagers = new HashMap<String, ecoMessageManager>();
        rewardManagers = new HashMap<String, ecoRewardManager>();

        try {
            configManager = new ecoConfigManager(this);
            configManager.load();
            if (!configManager.isEnabled()) {
                logger.severe("Edit ecoCreature config to enable the plugin.");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }
        catch (Exception e) {
            logger.severe("Failed to load config: " + e.toString());
            getPluginLoader().disablePlugin(this);
            return;
        }

        setupDependencies();
        setupDeathTpPlus();
        setupMobArenaHandler();
        setupHeroes();
        setupWorldGuard();
        registerEvents();

        try {
            metrics = new ecoMetrics(this);
            metrics.setupGraphs();
            metrics.start();
        }
        catch (IOException e) {
            logger.warning("Metrics failed to load.");
        }

        logger.info(getDescription().getVersion() + " enabled.");
    }

    public void onDisable()
    {
        logger.info(getDescription().getVersion() + " is disabled.");
    }

    public void onLoad()
    {
        DATA_WORLDS_FOLDER.mkdirs();
    }

    public static ecoLogger getEcoLogger()
    {
        return logger;
    }

    public static ecoMessageManager getMessageManager(Entity entity)
    {
        ecoMessageManager messageManager = messageManagers.get(entity.getWorld().getName());
        if (messageManager == null) {
            messageManager = messageManagers.get("default");
        }
        return messageManager;
    }

    public static ecoRewardManager getRewardManager(Entity entity)
    {
        ecoRewardManager rewardManager = rewardManagers.get(entity.getWorld().getName());
        if (rewardManager == null) {
            rewardManager = rewardManagers.get("default");
        }
        return rewardManager;
    }

    public ecoConfigManager getConfigManager()
    {
        return configManager;
    }

    public ecoMetrics getMetrics()
    {
        return metrics;
    }

    public boolean hasEconomy()
    {
        return economy != null;
    }

    public boolean has(Player player, String perm)
    {
        return permission.has(player, "ecoCreature." + perm) || permission.has(player, "ecocreature." + perm.toLowerCase());
    }
}