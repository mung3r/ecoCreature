package se.crafted.chrisb.ecoCreature.commands;

import org.bukkit.command.CommandSender;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class DebugCommand extends BasicCommand
{
    public DebugCommand()
    {
        super("Debug");
        setDescription("Toggle debug log output");
        setUsage("/ecoc debug");
        setArgumentRange(0, 0);
        setIdentifiers("debug");
        setPermission("ecocreature.command.debug");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        ECLogger.getInstance().setDebug(!ECLogger.getInstance().isDebug());
        sender.sendMessage(ECLogger.getInstance().isDebug() ? "debug log enabled." : "debug log disabled.");
        return true;
    }
}
