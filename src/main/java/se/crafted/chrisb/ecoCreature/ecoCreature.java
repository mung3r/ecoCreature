package se.crafted.chrisb.ecoCreature;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import se.crafted.chrisb.ecoCreature.commands.CommandHandler;
import se.crafted.chrisb.ecoCreature.commands.DebugCommand;
import se.crafted.chrisb.ecoCreature.commands.HelpCommand;
import se.crafted.chrisb.ecoCreature.commands.ReloadCommand;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.UpdateTask;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.handlers.BlockEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.DeathStreakEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.EntityDeathEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.PluginEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.EntityFarmedEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.HeroesEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.McMMOEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.PlayerDeathEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.KillStreakEventHandler;
import se.crafted.chrisb.ecoCreature.events.handlers.PlayerKilledEventHandler;
import se.crafted.chrisb.ecoCreature.events.listeners.BlockEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.EntityDeathEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.McMMOEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.PlayerDeathEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.HeroesEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.RewardEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.SpawnEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.StreakEventListener;
import se.crafted.chrisb.ecoCreature.metrics.RewardMetrics;

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

    public PluginConfig getPluginConfig()
    {
        return pluginConfig;
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
        commandHandler.addCommand(new DebugCommand());
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new RewardEventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnEventListener(this), this);

        PluginEventHandler eventHandler = new PluginEventHandler();
        eventHandler.add(new BlockEventHandler(this));
        eventHandler.add(new PlayerKilledEventHandler(this));
        eventHandler.add(new PlayerDeathEventHandler(this));
        eventHandler.add(new EntityDeathEventHandler(this));
        eventHandler.add(new EntityFarmedEventHandler(this));

        Bukkit.getPluginManager().registerEvents(new BlockEventListener(eventHandler), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathEventListener(eventHandler), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathEventListener(eventHandler), this);

        if (DependencyUtils.hasDeathTpPlus()) {
            eventHandler.add(new KillStreakEventHandler(this));
            eventHandler.add(new DeathStreakEventHandler(this));
            Bukkit.getPluginManager().registerEvents(new StreakEventListener(eventHandler), this);
        }

        if (DependencyUtils.hasHeroes()) {
            eventHandler.add(new HeroesEventHandler(this));
            Bukkit.getPluginManager().registerEvents(new HeroesEventListener(eventHandler), this);
        }

        if (DependencyUtils.hasMcMMO()) {
            eventHandler.add(new McMMOEventHandler(this));
            Bukkit.getPluginManager().registerEvents(new McMMOEventListener(eventHandler), this);
        }
    }
}
