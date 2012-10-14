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
            catch (NumberFormatException e) {
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
