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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class HelpCommand extends BasicCommand
{
    private static final int CMDS_PER_PAGE = 8;
    private CommandHandler commandHandler;

    public HelpCommand(ecoCreature plugin)
    {
        super("Help");
        this.commandHandler = plugin.getCommandHandler();
        setDescription("Displays the help menu");
        setUsage("/ecoc help §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers("ecoc", "help");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            }
            catch (NumberFormatException ignored) {
            }
        }

        List<Command> commands = getCommandsForSender(sender);

        int numPages = commands.size() / CMDS_PER_PAGE;
        if (commands.size() % CMDS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }

        sender.sendMessage("§c-----[ " + "§fecoCreature Help <" + (page + 1) + "/" + numPages + ">§c ]-----");
        int start = page * CMDS_PER_PAGE;
        int end = start + CMDS_PER_PAGE;
        if (end > commands.size()) {
            end = commands.size();
        }
        for (int c = start; c < end; c++) {
            Command cmd = commands.get(c);
            sender.sendMessage("  §a" + cmd.getUsage());
        }

        sender.sendMessage("§cFor more info on a particular command, type §f/<command> ?");

        return true;
    }

    private List<Command> getCommandsForSender(CommandSender sender)
    {
        List<Command> commands = new ArrayList<Command>();

        // Build list of permitted commands
        for (Command command : commandHandler.getCommands()) {
            if (command.isShownOnHelpMenu() && commandHandler.hasPermission(sender, command.getPermission())) {
                commands.add(command);
            }
        }

        return commands;
    }
}
