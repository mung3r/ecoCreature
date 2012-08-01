package se.crafted.chrisb.ecoCreature;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.simiancage.DeathTpPlus.DeathTpPlus;

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
    public Permission permission = null;
    public Economy economy = null;
    public static DeathTpPlus deathTpPlusPlugin = null;
    public static MobArenaHandler mobArenaHandler = null;
    public static Heroes heroesPlugin = null;
    public static mcMMO mcMMOPlugin = null;
    public static WorldGuardPlugin worldGuardPlugin = null;

    private static ecoLogger logger = new ecoLogger();
    private ecoMetrics metrics;
    private Map<String, ecoMessageManager> globalMessageManager;
    private Map<String, ecoRewardManager> globalRewardManager;
    private ecoConfigManager configManager;
    private CommandHandler commandHandler;
    private Set<Integer> spawnerMobs;

    public void onEnable()
    {
        Locale.setDefault(Locale.US);
        logger.setName(this.getDescription().getName());

        initVault();
        initMetrics();
        initPlugins();

        globalMessageManager = new HashMap<String, ecoMessageManager>();
        globalRewardManager = new HashMap<String, ecoRewardManager>();
        configManager = new ecoConfigManager(this);
        spawnerMobs = new HashSet<Integer>();

        addCommands();
        registerEvents();

        new ecoUpdate(this);

        logger.info(getDescription().getVersion() + " enabled.");
    }

    @Override
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

    public Map<String, ecoMessageManager> getGlobalMessageManager()
    {
        return globalMessageManager;
    }

    public ecoMessageManager getMessageManager(World world)
    {
        ecoMessageManager messageManager = globalMessageManager.get(world.getName());
        if (messageManager == null) {
            messageManager = globalMessageManager.get(ecoConfigManager.DEFAULT_WORLD);
        }
        return messageManager;
    }

    public Map<String, ecoRewardManager> getGlobalRewardManager()
    {
        return globalRewardManager;
    }

    public ecoRewardManager getRewardManager(World world)
    {
        ecoRewardManager rewardManager = globalRewardManager.get(world.getName());
        if (rewardManager == null) {
            rewardManager = globalRewardManager.get(ecoConfigManager.DEFAULT_WORLD);
        }
        return rewardManager;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public Permission getPermission()
    {
        return permission;
    }

    public boolean hasPermission(Player player, String perm)
    {
        return permission.has(player.getWorld(), player.getName(), "ecoCreature." + perm) || permission.has(player.getWorld(), player.getName(), "ecocreature." + perm.toLowerCase());

    }

    public Economy getEconomy()
    {
        return economy;
    }

    public boolean hasEconomy()
    {
        return economy != null;
    }

    public boolean isSpawnerMob(Entity entity)
    {
        return spawnerMobs.remove(Integer.valueOf(entity.getEntityId()));
    }

    public void setSpawnerMob(Entity entity)
    {
        // Only add to the array if we're tracking by entity. Avoids a memory leak.
        if (!getRewardManager(entity.getWorld()).canCampSpawner && getRewardManager(entity.getWorld()).campByEntity) {
            spawnerMobs.add(Integer.valueOf(entity.getEntityId()));
        }
    }

    public static ecoLogger getEcoLogger()
    {
        return logger;
    }

    private void initVault()
    {
        Plugin vaultPlugin = getPlugin("Vault", "net.milkbowl.vault.Vault");
        
        if (vaultPlugin != null) {
            RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
                logger.info("Found permissions provider.");
            }
    
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
                logger.info("Economy enabled.");
            }
        }

        if (permission == null) {
            logger.severe("Failed to load permission provider.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (economy == null) {
            logger.warning("Economy disabled.");
        }
    }

    private void initMetrics()
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

    private void initPlugins()
    {
        deathTpPlusPlugin = (DeathTpPlus) getPlugin("DeathTpPlus", "org.simiancage.DeathTpPlus.DeathTpPlus");
        heroesPlugin = (Heroes) getPlugin("Heroes", "com.herocraftonline.heroes.Heroes");
        mcMMOPlugin = (mcMMO) getPlugin("mcMMO", "com.gmail.nossr50.mcMMO");
        worldGuardPlugin = (WorldGuardPlugin) getPlugin("WorldGuard", "com.sk89q.worldguard.bukkit.WorldGuardPlugin");

        Plugin mobArenaPlugin = getPlugin("MobArena", "com.garbagemule.MobArena.MobArena");
        if (mobArenaPlugin != null) {
            mobArenaHandler = new MobArenaHandler();
        }
    }

    private Plugin getPlugin(String pluginName, String className)
    {
        Plugin plugin = this.getServer().getPluginManager().getPlugin(pluginName);
        try {
            Class<?> testClass = Class.forName(className);
            if (testClass.isInstance(plugin)) {
                logger.info("Found plugin " + plugin.getDescription().getName());
                return plugin;
            }
        }
        catch (ClassNotFoundException e) {
            logger.warning("Did not find plugin " + pluginName);
        }
        return null;
    }

    private void addCommands()
    {
        commandHandler = new CommandHandler(this);
        commandHandler.addCommand(new HelpCommand(this));
        commandHandler.addCommand(new ReloadCommand(this));
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new ecoBlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ecoEntityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ecoDeathListener(this), this);
        if (deathTpPlusPlugin != null) {
            Bukkit.getPluginManager().registerEvents(new ecoStreakListener(this), this);
        }
    }
}