/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2014, R. Ramos <http://github.com/mung3r/>
 * ecoCreature is licensed under the GNU Lesser General Public License.
 *
 * ecoCreature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ecoCreature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.commons.UpdateTask;
import se.crafted.chrisb.ecoCreature.events.listeners.BlockEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.DropEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.EntityDeathEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.HeroesEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.McMMOEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.PlayerDeathEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.SpawnEventListener;
import se.crafted.chrisb.ecoCreature.events.listeners.StreakEventListener;
import se.crafted.chrisb.ecoCreature.events.mappers.BlockEventMapper;
import se.crafted.chrisb.ecoCreature.events.mappers.DeathStreakEventMapper;
import se.crafted.chrisb.ecoCreature.events.DropEventFactory;
import se.crafted.chrisb.ecoCreature.events.mappers.EntityFarmedEventMapper;
import se.crafted.chrisb.ecoCreature.events.mappers.EntityKilledEventMapper;
import se.crafted.chrisb.ecoCreature.events.mappers.HeroesEventMapper;
import se.crafted.chrisb.ecoCreature.events.mappers.KillStreakEventMapper;
import se.crafted.chrisb.ecoCreature.events.mappers.McMMOEventMapper;
import se.crafted.chrisb.ecoCreature.events.mappers.PlayerDeathEventMapper;
import se.crafted.chrisb.ecoCreature.events.mappers.PlayerKilledEventMapper;
import se.crafted.chrisb.ecoCreature.metrics.DropMetrics;

public class ecoCreature extends JavaPlugin
{
    private DropMetrics metrics;
    private PluginConfig pluginConfig;
    private CommandHandler commandHandler;

    @Override
    public void onEnable()
    {
        DependencyUtils.init();

        metrics = new DropMetrics(this);
        pluginConfig = new PluginConfig(this);

        if (pluginConfig.isInitialized()) {
            addCommands();
            registerEvents();

            if (pluginConfig.isCheckForUpdates()) {
                new UpdateTask(this);
            }

            LoggerUtil.getInstance().info(getDescription().getVersion() + " enabled.");
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
    }

    public DropMetrics getMetrics()
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
        Bukkit.getPluginManager().registerEvents(new DropEventListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnEventListener(this), this);

        DropEventFactory factory = new DropEventFactory();
        factory.addMapper(new BlockEventMapper(this));
        factory.addMapper(new PlayerKilledEventMapper(this));
        factory.addMapper(new PlayerDeathEventMapper(this));
        factory.addMapper(new EntityKilledEventMapper(this));
        factory.addMapper(new EntityFarmedEventMapper(this));

        Bukkit.getPluginManager().registerEvents(new BlockEventListener(factory), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathEventListener(factory), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathEventListener(factory), this);

        if (DependencyUtils.hasDeathTpPlus()) {
            factory.addMapper(new KillStreakEventMapper(this));
            factory.addMapper(new DeathStreakEventMapper(this));
            Bukkit.getPluginManager().registerEvents(new StreakEventListener(factory), this);
        }

        if (DependencyUtils.hasHeroes()) {
            factory.addMapper(new HeroesEventMapper(this));
            Bukkit.getPluginManager().registerEvents(new HeroesEventListener(factory), this);
        }

        if (DependencyUtils.hasMcMMO()) {
            factory.addMapper(new McMMOEventMapper(this));
            Bukkit.getPluginManager().registerEvents(new McMMOEventListener(factory), this);
        }
    }
}
