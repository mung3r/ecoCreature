package se.crafted.chrisb.ecoCreature;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.simiancage.DeathTpPlus.DeathTpPlus;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaHandler;
import com.gmail.nossr50.mcMMO;
import com.herocraftonline.heroes.Heroes;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import se.crafted.chrisb.ecoCreature.commands.CommandHandler;
import se.crafted.chrisb.ecoCreature.commands.HelpCommand;
import se.crafted.chrisb.ecoCreature.commands.ReloadCommand;
import se.crafted.chrisb.ecoCreature.listeners.ecoBlockListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoEntityListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoDeathListener;
import se.crafted.chrisb.ecoCreature.listeners.ecoStreakListener;
import se.crafted.chrisb.ecoCreature.managers.ecoConfigManager;
import se.crafted.chrisb.ecoCreature.managers.ecoMessageManager;
import se.crafted.chrisb.ecoCreature.managers.ecoRewardManager;
import se.crafted.chrisb.ecoCreature.utils.ecoLogger;
import se.crafted.chrisb.ecoCreature.utils.ecoMetrics;
import se.crafted.chrisb.ecoCreature.utils.ecoUpdate;

public class ecoCreature extends JavaPlugin
{
    private static final String DEV_BUKKIT_URL = "http://dev.bukkit.org/server-mods/ecocreature";
    private static final long CHECK_DELAY = 0;
    private static final long CHECK_PERIOD = 432000;

    public static Permission permission = null;
    public static Economy economy = null;
    public static DeathTpPlus deathTpPlusPlugin = null;
    public static MobArenaHandler mobArenaHandler = null;
    public static Heroes heroesPlugin = null;
    public static mcMMO mcMMOPlugin = null;
    public static WorldGuardPlugin worldGuardPlugin = null;

    private static ecoLogger logger = new ecoLogger();
    private ecoMetrics metrics;
    public static Map<String, ecoMessageManager> messageManagers;
    public static Map<String, ecoRewardManager> rewardManagers;
    private ecoConfigManager configManager;
    private CommandHandler commandHandler;

    public void onEnable()
    {
        Locale.setDefault(Locale.US);
        logger.setName(this.getDescription().getName());

        setupVault();
        setupMetrics();
        setupDeathTpPlus();
        setupMobArenaHandler();
        setupHeroes();
        setupMcMMO();
        setupWorldGuard();

        messageManagers = new HashMap<String, ecoMessageManager>();
        rewardManagers = new HashMap<String, ecoRewardManager>();
        configManager = new ecoConfigManager(this);

        registerCommands();
        registerEvents();

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new ecoUpdate(this, DEV_BUKKIT_URL), CHECK_DELAY, CHECK_PERIOD);

        logger.info(getDescription().getVersion() + " enabled.");
    }

    public void onDisable()
    {
        configManager.save();
        logger.info(getDescription().getVersion() + " is disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return commandHandler.dispatch(sender, cmd, commandLabel, args);
    }

    @Override
    public void reloadConfig()
    {
        super.reloadConfig();
        configManager = new ecoConfigManager(this);
    };

    public ecoMetrics getMetrics()
    {
        return metrics;
    }

    public ecoConfigManager getConfigManager()
    {
        return configManager;
    }

    public static ecoMessageManager getMessageManager(Entity entity)
    {
        ecoMessageManager messageManager = messageManagers.get(entity.getWorld().getName());
        if (messageManager == null) {
            messageManager = messageManagers.get(ecoConfigManager.DEFAULT_WORLD);
        }
        return messageManager;
    }

    public static ecoRewardManager getRewardManager(Entity entity)
    {
        ecoRewardManager rewardManager = rewardManagers.get(entity.getWorld().getName());
        if (rewardManager == null) {
            rewardManager = rewardManagers.get(ecoConfigManager.DEFAULT_WORLD);
        }
        return rewardManager;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public static ecoLogger getEcoLogger()
    {
        return logger;
    }

    public boolean hasEconomy()
    {
        return economy != null;
    }

    public boolean has(Player player, String perm)
    {
        return permission.has(player, "ecoCreature." + perm) || permission.has(player, "ecocreature." + perm.toLowerCase());
    }

    private void setupVault()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            logger.info("Found permissions provider.");
        }
        else {
            logger.severe("Failed to load permission provider.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            logger.info("Economy enabled.");
        }
        else {
            logger.warning("Economy disabled.");
        }
    }

    private void setupMetrics()
    {
        try {
            metrics = new ecoMetrics(this);
            metrics.setupGraphs();
            metrics.start();
        }
        catch (IOException e) {
            logger.warning("Metrics failed to load.");
        }
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

    private void setupMcMMO()
    {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("mcMMO");
        if (plugin instanceof mcMMO) {
            mcMMOPlugin = (mcMMO) plugin;
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

    private void registerCommands()
    {
        commandHandler = new CommandHandler(this);
        commandHandler.addCommand(new HelpCommand(this));
        commandHandler.addCommand(new ReloadCommand(this));
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
}