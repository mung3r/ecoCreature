package se.crafted.chrisb.ecoCreature;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.bekvon.bukkit.residence.Residence;
import com.garbagemule.MobArena.MobArenaHandler;
import com.herocraftonline.heroes.Heroes;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

import couk.Adamki11s.Regios.API.RegiosAPI;

import se.crafted.chrisb.ecoCreature.commands.CommandHandler;
import se.crafted.chrisb.ecoCreature.commands.HelpCommand;
import se.crafted.chrisb.ecoCreature.commands.ReloadCommand;
import se.crafted.chrisb.ecoCreature.commons.UpdateTask;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.config.ConfigManager;
import se.crafted.chrisb.ecoCreature.listeners.BlockEventListener;
import se.crafted.chrisb.ecoCreature.listeners.DeathEventListener;
import se.crafted.chrisb.ecoCreature.listeners.HeroMasteredListener;
import se.crafted.chrisb.ecoCreature.listeners.KillEventListener;
import se.crafted.chrisb.ecoCreature.listeners.SpawnEventListener;
import se.crafted.chrisb.ecoCreature.listeners.StreakEventListener;
import se.crafted.chrisb.ecoCreature.messages.MessageManager;
import se.crafted.chrisb.ecoCreature.metrics.MetricsManager;
import se.crafted.chrisb.ecoCreature.rewards.RewardManager;

public class ecoCreature extends JavaPlugin
{
    private static ECLogger logger = new ECLogger();

    private Permission permission;
    private Economy economy;
    private MetricsManager metrics;
    private Plugin deathTpPlusPlugin;
    private MobArenaHandler mobArenaHandler;
    private Heroes heroesPlugin;
    private WorldGuardPlugin worldGuardPlugin;
    private Plugin mcMMOPlugin;
    private Residence residencePlugin;
    private RegiosAPI regiosAPI;
    private Towny townyPlugin;
    private Plugin factionsPlugin;

    private Map<String, MessageManager> globalMessageManager;
    private Map<String, RewardManager> globalRewardManager;
    private ConfigManager configManager;
    private CommandHandler commandHandler;

    public void onEnable()
    {
        Locale.setDefault(Locale.US);
        logger.setName(this.getDescription().getName());

        initVault();
        initMetrics();
        initPlugins();

        globalMessageManager = new HashMap<String, MessageManager>();
        globalRewardManager = new HashMap<String, RewardManager>();
        configManager = new ConfigManager(this);

        addCommands();
        registerEvents();

        new UpdateTask(this);

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
        configManager = new ConfigManager(this);
    };

    public MetricsManager getMetrics()
    {
        return metrics;
    }

    public ConfigManager getConfigManager()
    {
        return configManager;
    }

    public Map<String, MessageManager> getGlobalMessageManager()
    {
        return globalMessageManager;
    }

    public MessageManager getMessageManager(World world)
    {
        MessageManager messageManager = globalMessageManager.get(world.getName());
        if (messageManager == null) {
            messageManager = globalMessageManager.get(ConfigManager.DEFAULT_WORLD);
        }
        return messageManager;
    }

    public Map<String, RewardManager> getGlobalRewardManager()
    {
        return globalRewardManager;
    }

    public RewardManager getRewardManager(World world)
    {
        RewardManager rewardManager = globalRewardManager.get(world.getName());
        if (rewardManager == null) {
            rewardManager = globalRewardManager.get(ConfigManager.DEFAULT_WORLD);
        }
        return rewardManager;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public boolean hasPermission(Player player, String perm)
    {
        return permission.has(player.getWorld(), player.getName(), "ecoCreature." + perm) || permission.has(player.getWorld(), player.getName(), "ecocreature." + perm.toLowerCase());
    }

    public Permission getPermission()
    {
        return permission;
    }

    public boolean hasEconomy()
    {
        return economy != null;
    }

    public Economy getEconomy()
    {
        return economy;
    }

    public boolean hasMobArena()
    {
        return mobArenaHandler != null;
    }

    public MobArenaHandler getMobArenaHandler()
    {
        return mobArenaHandler;
    }

    public boolean hasHeroes()
    {
        return heroesPlugin != null;
    }

    public Heroes getHeroes()
    {
        return heroesPlugin;
    }

    public boolean hasResidence()
    {
        return residencePlugin != null;
    }

    public boolean hasRegios()
    {
        return regiosAPI != null;
    }

    public RegiosAPI getRegiosAPI()
    {
        return regiosAPI;
    }

    public boolean hasWorldGuard()
    {
        return worldGuardPlugin != null;
    }

    public RegionManager getRegionManager(World world)
    {
        return worldGuardPlugin.getRegionManager(world);
    }

    public boolean hasMcMMO()
    {
        return mcMMOPlugin != null;
    }

    public boolean hasTowny()
    {
        return townyPlugin != null;
    }

    public boolean hasFactions()
    {
        return factionsPlugin != null;
    }

    public boolean hasDeathTpPlus()
    {
        return deathTpPlusPlugin != null;
    }

    public static ECLogger getECLogger()
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
            metrics = new MetricsManager(this);
            metrics.setupGraphs();
            metrics.start();
        }
        catch (IOException e) {
            logger.warning("Metrics failed to load.");
        }
    }

    private void initPlugins()
    {
        deathTpPlusPlugin = getPlugin("DeathTpPlus", "org.simiancage.DeathTpPlus.DeathTpPlus");
        heroesPlugin = (Heroes) getPlugin("Heroes", "com.herocraftonline.heroes.Heroes");
        worldGuardPlugin = (WorldGuardPlugin) getPlugin("WorldGuard", "com.sk89q.worldguard.bukkit.WorldGuardPlugin");
        residencePlugin = (Residence) getPlugin("Residence", "com.bekvon.bukkit.residence.Residence");
        townyPlugin = (Towny) getPlugin("Towny", "com.palmergames.bukkit.towny.Towny");
        factionsPlugin = getPlugin("Factions", "com.massivecraft.factions.P");
        mcMMOPlugin = getPlugin("mcMMO", "com.gmail.nossr50.mcMMO");

        Plugin regiosPlugin = getPlugin("Regios", "couk.Adamki11s.Regios.Main.Regios");
        if (regiosPlugin != null) {
            regiosAPI = new RegiosAPI();
        }

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
            if (testClass.isInstance(plugin) && plugin.isEnabled()) {
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
        Bukkit.getPluginManager().registerEvents(new BlockEventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DeathEventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new KillEventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnEventListener(this), this);
        if (hasDeathTpPlus()) {
            Bukkit.getPluginManager().registerEvents(new StreakEventListener(this), this);
        }
        if (hasHeroes()) {
            Bukkit.getPluginManager().registerEvents(new HeroMasteredListener(this), this);
        }
    }
}