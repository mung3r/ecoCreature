package se.crafted.chrisb.ecoCreature;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import se.crafted.chrisb.ecoCreature.commands.CommandHandler;
import se.crafted.chrisb.ecoCreature.commands.HelpCommand;
import se.crafted.chrisb.ecoCreature.commands.ReloadCommand;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
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
    private MetricsManager metrics;
    private ConfigManager configManager;
    private CommandHandler commandHandler;

    public void onEnable()
    {
        DependencyUtils.init();
        initMetrics();

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

    public RewardManager getRewardManager(World world)
    {
        RewardManager rewardManager = configManager.getGlobalRewardManager().get(world.getName());
        if (rewardManager == null) {
            rewardManager = configManager.getGlobalRewardManager().get(ConfigManager.DEFAULT_WORLD);
        }
        return rewardManager;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
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
        if (DependencyUtils.hasDeathTpPlus()) {
            Bukkit.getPluginManager().registerEvents(new StreakEventListener(this), this);
        }
        if (DependencyUtils.hasHeroes()) {
            Bukkit.getPluginManager().registerEvents(new HeroMasteredListener(this), this);
        }
    }
}