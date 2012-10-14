package se.crafted.chrisb.ecoCreature.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface Command {

    void cancelInteraction(CommandSender executor);

    boolean execute(CommandSender executor, String identifier, String[] args);

    String getDescription();

    List<String> getIdentifiers();

    int getMaxArguments();

    int getMinArguments();

    String getName();

    List<String> getNotes();

    String getPermission();

    String getUsage();

    boolean isIdentifier(CommandSender executor, String input);

    boolean isInProgress(CommandSender executor);

    boolean isInteractive();

    boolean isShownOnHelpMenu();

}
