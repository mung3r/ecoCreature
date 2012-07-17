/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package se.crafted.chrisb.ecoCreature.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class CommandHandler
{

    private final Permission permission;
    protected Map<String, Command> commands;

    public CommandHandler(ecoCreature plugin)
    {
        permission = plugin.getPermission();
        commands = new LinkedHashMap<String, Command>();
    }

    public void addCommand(Command command)
    {
        commands.put(command.getName().toLowerCase(), command);
    }

    public void removeCommand(Command command)
    {
        commands.remove(command.getName().toLowerCase());
    }

    public Command getCommand(String name)
    {
        return commands.get(name.toLowerCase());
    }

    public List<Command> getCommands()
    {
        return new ArrayList<Command>(commands.values());
    }

    public boolean dispatch(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
    {

        String[] arguments;
        if (args.length < 1) {
            arguments = new String[] { command.getName() };
        }
        else {
            arguments = args;
        }

        for (int argsIncluded = arguments.length; argsIncluded >= 0; argsIncluded--) {
            StringBuilder identifierBuilder = new StringBuilder();
            for (int i = 0; i < argsIncluded; i++) {
                identifierBuilder.append(' ').append(arguments[i]);
            }

            String identifier = identifierBuilder.toString().trim();
            for (Command cmd : commands.values()) {
                if (cmd.isIdentifier(sender, identifier)) {
                    String[] realArgs = Arrays.copyOfRange(arguments, argsIncluded, arguments.length);

                    if (!cmd.isInProgress(sender)) {
                        if (realArgs.length < cmd.getMinArguments() || realArgs.length > cmd.getMaxArguments()) {
                            displayCommandHelp(cmd, sender);
                            return true;
                        }
                        else if (realArgs.length > 0 && "?".equals(realArgs[0])) {
                            displayCommandHelp(cmd, sender);
                            return true;
                        }
                    }

                    if (!hasPermission(sender, cmd.getPermission())) {
                        sender.sendMessage("Insufficient permission.");
                        return true;
                    }

                    cmd.execute(sender, identifier, realArgs);
                    return true;
                }
            }
        }

        return true;
    }

    private void displayCommandHelp(Command cmd, CommandSender sender)
    {
        sender.sendMessage("§cCommand:§e " + cmd.getName());
        sender.sendMessage("§cDescription:§e " + cmd.getDescription());
        sender.sendMessage("§cUsage:§e " + cmd.getUsage());
        if (cmd.getNotes() != null) {
            for (String note : cmd.getNotes()) {
                sender.sendMessage("§e" + note);
            }
        }
    }

    public boolean hasPermission(CommandSender sender, String permString)
    {
        if (!(sender instanceof Player) || permString == null || permString.isEmpty()) {
            return true;
        }

        Player player = (Player) sender;
        if (permission != null) {
            return permission.has(player, permString);
        }
        return player.hasPermission(permString);
    }
}
