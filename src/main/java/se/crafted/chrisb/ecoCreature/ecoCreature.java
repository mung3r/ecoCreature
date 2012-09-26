package se.crafted.chrisb.ecoCreature;

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
import se.crafted.chrisb.ecoCreature.events.handlers.BlockEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.EntityDeathEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.HeroesEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.McMMOEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.PlayerDeathEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.StreakEventHandler;
import se.crafted.chrisb.ecoCreature.events.listeners.BlockEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.EntityDeathEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.McMMOEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.PlayerDeathEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.HeroesEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.RewardEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.SpawnEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.StreakEventListener;
import se.crafted.chrisb.ecoCreature.metrics.RewardMetrics;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public class ecoCreature extends JavaPlugin
{
    private RewardMetrics metrics;
    private PluginConfig pluginConfig;
    private CommandHandler commandHandler;

    @Override
    public void onEnable()
    {
        DependencyUtils.init();

        metrics = new RewardMetrics(this);
        pluginConfig = new PluginConfig(this);

        if (pluginConfig.isInitialized()) {
            addCommands();
            registerEvents();

            new UpdateTask(this);

            ECLogger.getInstance().info(getDescription().getVersion() + " enabled.");
        }
        else {
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
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
        pluginConfig = new PluginConfig(this);
    };

    public RewardMetrics getMetrics()
    {
        return metrics;
    }

    public WorldSettings getWorldSettings(World world)
    {
        return pluginConfig.getWorldSettings(world);
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    private void addCommands()
    {
        commandHandler = new CommandHandler();
        commandHandler.addCommand(new HelpCommand(this));
        commandHandler.addCommand(new ReloadCommand(this));
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new RewardEventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnEventListener(this), this);

        Bukkit.getPluginManager().registerEvents(new BlockEventListener(new BlockEventHandler(this)), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathEventListener(new PlayerDeathEventHandler(this)), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathEventListener(new EntityDeathEventHandler(this)), this);
        if (DependencyUtils.hasDeathTpPlus()) {
            Bukkit.getPluginManager().registerEvents(new StreakEventListener(new StreakEventHandler(this)), this);
        }
        if (DependencyUtils.hasHeroes()) {
            Bukkit.getPluginManager().registerEvents(new HeroesEventListener(new HeroesEventHandler(this)), this);
        }
        if (DependencyUtils.hasMcMMO()) {
            Bukkit.getPluginManager().registerEvents(new McMMOEventListener(new McMMOEventHandler(this)), this);
        }
    }
}
