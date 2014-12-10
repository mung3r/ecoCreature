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

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
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
import se.crafted.chrisb.ecoCreature.events.mappers.DropEventFactory;
import se.crafted.chrisb.ecoCreature.metrics.DropMetrics;

public class ecoCreature extends JavaPlugin
{
    private DropMetrics metrics;
    private DropConfigLoader dropConfigLoader;
    private DropEventFactory dropEventFactory;
    private UpdateTask updateTask;
    private CommandHandler commandHandler;

    @Override
    public void onEnable()
    {
        DependencyUtils.init();

        metrics = new DropMetrics(this);
        dropConfigLoader = new DropConfigLoader(this);
        dropEventFactory = new DropEventFactory(dropConfigLoader);
        updateTask = new UpdateTask(this);

        if (dropConfigLoader.isInitialized()) {
            addCommands();
            registerEvents();

            if (dropConfigLoader.isCheckForUpdates()) {
                updateTask.start();
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
        dropConfigLoader = new DropConfigLoader(this);
        dropEventFactory = new DropEventFactory(dropConfigLoader);
        restartUpdateTask();
    }

    public void loadConfig(String file) throws IOException, InvalidConfigurationException
    {
        dropConfigLoader.loadConfig(file);
        dropEventFactory = new DropEventFactory(dropConfigLoader);
        restartUpdateTask();
    }

    private void restartUpdateTask()
    {
        updateTask.stop();
    
        if (dropConfigLoader.isInitialized() && dropConfigLoader.isCheckForUpdates()) {
            updateTask.start();
        }
    }

    private void addCommands()
    {
        commandHandler = new CommandHandler();
        commandHandler.addCommand(new HelpCommand(commandHandler));
        commandHandler.addCommand(new ReloadCommand(this));
        commandHandler.addCommand(new DebugCommand());
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new DropEventListener(metrics), this);
        Bukkit.getPluginManager().registerEvents(new SpawnEventListener(dropConfigLoader), this);

        Bukkit.getPluginManager().registerEvents(new BlockEventListener(dropEventFactory), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathEventListener(dropEventFactory), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathEventListener(dropEventFactory), this);

        if (DependencyUtils.hasDeathTpPlus()) {
            Bukkit.getPluginManager().registerEvents(new StreakEventListener(dropEventFactory), this);
        }

        if (DependencyUtils.hasHeroes()) {
            Bukkit.getPluginManager().registerEvents(new HeroesEventListener(dropEventFactory), this);
        }

        if (DependencyUtils.hasMcMMO()) {
            Bukkit.getPluginManager().registerEvents(new McMMOEventListener(dropEventFactory), this);
        }
    }
}
