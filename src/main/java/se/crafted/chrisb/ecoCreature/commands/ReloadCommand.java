package se.crafted.chrisb.ecoCreature.commands;

import org.bukkit.command.CommandSender;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ReloadCommand extends BasicCommand
{
    private ecoCreature plugin;

    public ReloadCommand(ecoCreature plugin)
    {
        super("Reload");
        this.plugin = plugin;
        setDescription("Reload configuration");
        setUsage("/ecoc reload");
        setArgumentRange(0, 0);
        setIdentifiers("reload");
        setPermission("ecocreature.command.reload");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        plugin.reloadConfig();
        sender.sendMessage("ecoCreature config reloaded.");
        return true;
    }
}
