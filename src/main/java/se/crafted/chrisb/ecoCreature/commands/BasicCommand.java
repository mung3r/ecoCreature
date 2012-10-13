package se.crafted.chrisb.ecoCreature.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class BasicCommand implements Command {

    private String name;
    private String description;
    private String usage;
    private String permission;
    private List<String> notes;
    private List<String> identifiers;
    private int minArguments;
    private int maxArguments;

    public BasicCommand(String name) {
        this.name = name;
        description = "";
        usage = "";
        permission = "";
        notes = new ArrayList<String>();
        identifiers = new ArrayList<String>();
        minArguments = 0;
        maxArguments = 0;
    }

    @Override
    public void cancelInteraction(CommandSender executor) {}

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<String> getIdentifiers() {
        return identifiers;
    }

    @Override
    public int getMaxArguments() {
        return maxArguments;
    }

    @Override
    public int getMinArguments() {
        return minArguments;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getNotes() {
        return notes;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public boolean isIdentifier(CommandSender executor, String input) {
        for (String identifier : identifiers) {
            if (input.equalsIgnoreCase(identifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInProgress(CommandSender executor) {
        return false;
    }

    @Override
    public boolean isInteractive() {
        return false;
    }

    @Override
    public boolean isShownOnHelpMenu() {
        return true;
    }

    public void setArgumentRange(int min, int max) {
        this.minArguments = min;
        this.maxArguments = max;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdentifiers(String... identifiers) {
        this.identifiers.addAll(Arrays.asList(identifiers));
    }

    public void setNotes(String... notes) {
        this.notes.addAll(Arrays.asList(notes));
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

}
