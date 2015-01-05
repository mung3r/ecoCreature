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
package se.crafted.chrisb.ecoCreature.commands;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ReloadCommand extends BasicCommand
{
    private final ecoCreature plugin;

    public ReloadCommand(ecoCreature plugin)
    {
        super("Reload");
        this.plugin = plugin;
        setDescription("Reload configuration");
        setUsage("/ecoc reload [config] <world>");
        setArgumentRange(0, 2);
        setIdentifiers("reload");
        setPermission("ecocreature.command.reload");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (args != null) {
            try {
                switch (args.length) {
                    case 0:
                        plugin.reloadConfig();
                        sender.sendMessage("config reloaded.");
                        break;
                    case 1:
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            plugin.loadConfig(args[0], player.getWorld().getName());
                        }
                        else {
                            sender.sendMessage("you must specify a world");
                        }
                        break;
                    case 2:
                        World world = Bukkit.getWorld(args[1]);
                        if (world != null) {
                            plugin.loadConfig(args[0], world.getName());
                        }
                        else {
                            sender.sendMessage("that world doesn't exist");
                        }
                        break;
                    default:
                        sender.sendMessage(getUsage());
                }
            }
            catch (InvalidConfigurationException|IOException e) {
                sender.sendMessage("failed to load config");
            }
        }
        return true;
    }
}
