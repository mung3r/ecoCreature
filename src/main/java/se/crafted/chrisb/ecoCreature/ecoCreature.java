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
import se.crafted.chrisb.ecoCreature.metrics.MetricsManager;
import se.crafted.chrisb.ecoCreature.rewards.RewardManager;

public class ecoCreature extends JavaPlugin
{
    private static Plugin vaultPlugin;
    private static Plugin deathTpPlusPlugin;
    private static MobArenaHandler mobArenaHandler;
    private static Heroes heroesPlugin;
    private static WorldGuardPlugin worldGuardPlugin;
    private static Plugin mcMMOPlugin;
    private static Residence residencePlugin;
    private static RegiosAPI regiosAPI;
    private static Towny townyPlugin;
    private static Plugin factionsPlugin;

    private static Permission permission;
    private static Economy economy;

    private MetricsManager metrics;
    private Map<String, RewardManager> globalRewardManager;
    private ConfigManager configManager;
    private CommandHandler commandHandler;

    public void onEnable()
    {
        Locale.setDefault(Locale.US);
        ECLogger.getInstance().setName(this.getDescription().getName());

        initPlugins();
        initVault();
        initMetrics();

        globalRewardManager = new HashMap<String, RewardManager>();
        configManager = new ConfigManager(this);

        addCommands();
        registerEvents();

        new UpdateTask(this);

        ECLogger.getInstance().info(getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable()
    {
        configManager.save();
        ECLogger.getInstance().info(getDescription().getVersion() + " is disabled.");
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

    public static boolean hasPermission(Player player, String perm)
    {
        return permission.has(player.getWorld(), player.getName(), "ecoCreature." + perm) || permission.has(player.getWorld(), player.getName(), "ecocreature." + perm.toLowerCase());
    }

    public static boolean hasPermission()
    {
        return getPermission() != null;
    }

    public static Permission getPermission()
    {
        return permission;
    }

    public static boolean hasEconomy()
    {
        return getEconomy() != null;
    }

    public static Economy getEconomy()
    {
        return economy;
    }

    public static boolean hasVault()
    {
        return getVault() != null;
    }

    public static Plugin getVault()
    {
        if (vaultPlugin == null) {
            vaultPlugin = getPlugin("Vault", "net.milkbowl.vault.Vault");
        }
        return vaultPlugin;
    }

    public static boolean hasMobArena()
    {
        return mobArenaHandler != null;
    }

    public static MobArenaHandler getMobArenaHandler()
    {
        return mobArenaHandler;
    }

    public static boolean hasHeroes()
    {
        return heroesPlugin != null;
    }

    public static Heroes getHeroes()
    {
        return heroesPlugin;
    }

    public static boolean hasResidence()
    {
        return residencePlugin != null;
    }

    public static boolean hasRegios()
    {
        return regiosAPI != null;
    }

    public static RegiosAPI getRegiosAPI()
    {
        return regiosAPI;
    }

    public static boolean hasWorldGuard()
    {
        return worldGuardPlugin != null;
    }

    public static RegionManager getRegionManager(World world)
    {
        return worldGuardPlugin.getRegionManager(world);
    }

    public static boolean hasMcMMO()
    {
        return mcMMOPlugin != null;
    }

    public static boolean hasTowny()
    {
        return townyPlugin != null;
    }

    public static boolean hasFactions()
    {
        return factionsPlugin != null;
    }

    public static boolean hasDeathTpPlus()
    {
        return deathTpPlusPlugin != null;
    }

    private void initVault()
    {
        if (hasVault()) {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }

            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
        }

        if (hasPermission()) {
            ECLogger.getInstance().info("Found permissions provider.");
        }
        else {
            ECLogger.getInstance().severe("Failed to load permission provider.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (hasEconomy()) {
            ECLogger.getInstance().info("Economy enabled.");
        }
        else {
            ECLogger.getInstance().warning("Economy disabled.");
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
            ECLogger.getInstance().warning("Metrics failed to load.");
        }
    }

    private static void initPlugins()
    {
        vaultPlugin = getPlugin("Vault", "net.milkbowl.vault.Vault");
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

    private static Plugin getPlugin(String pluginName, String className)
    {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        try {
            Class<?> testClass = Class.forName(className);
            if (testClass.isInstance(plugin) && plugin.isEnabled()) {
                ECLogger.getInstance().info("Found plugin " + plugin.getDescription().getName());
                return plugin;
            }
        }
        catch (ClassNotFoundException e) {
            ECLogger.getInstance().warning("Did not find plugin " + pluginName);
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