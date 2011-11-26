package se.crafted.chrisb.ecoCreature.managers;

import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoMessage;

public class ecoMessageManager implements Cloneable
{
    private final static String PLAYER_TOKEN = "<plr>";
    private final static String AMOUNT_TOKEN = "<amt>";
    private final static String ITEM_TOKEN = "<itm>";
    private final static String CREATURE_TOKEN = "<crt>";

    public final static String NO_CAMP_MESSAGE = "&7You find no rewards camping monster spawners.";
    public final static String NO_BOW_REWARD_MESSAGE = "&7You find no rewards on this creature.";
    public final static String DEATH_PENALTY_MESSAGE = "&7You wake up to find &6<amt>&7 missing from your pockets!";
    public final static String PVP_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for murdering &5<crt>.";

    public final static String NO_REWARD_MESSAGE = "'&7You slayed a &5<crt>&7 using a &3<itm>.";
    public final static String REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for slaying a &5<crt>.";
    public final static String PENALTY_MESSAGE = "&7You are penalized &6<amt>&7 for slaying a &5<crt>.";

    public final static String DTP_DEATHSTREAK_MESSAGE = "&7That death streak cost you &6<amt>&7.";
    public final static String DTP_KILLSTREAK_MESSAGE = "&7You are awarded &6<amt>&7 for that kill streak!";

    public boolean shouldOutputMessages;

    public ecoMessage noBowRewardMessage;
    public ecoMessage noCampMessage;
    public ecoMessage deathPenaltyMessage;
    public ecoMessage pvpRewardMessage;
    public ecoMessage dtpDeathStreakMessage;
    public ecoMessage dtpKillStreakMessage;

    private ecoCreature plugin;

    public ecoMessageManager(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public ecoMessageManager clone()
    {
        try {
            return (ecoMessageManager) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void sendMessage(ecoMessage message, Player player)
    {
        if (shouldOutputMessages && message.isEnabled()) {
            player.sendMessage(message.getMessage().replaceAll(PLAYER_TOKEN, player.getName()));
        }
    }

    public void sendMessage(ecoMessage message, Player player, Double amount)
    {
        if (shouldOutputMessages && message.isEnabled() && plugin.hasEconomy()) {
            player.sendMessage(message.getMessage().replaceAll(PLAYER_TOKEN, player.getName()).replaceAll(AMOUNT_TOKEN, ecoCreature.economy.format(amount).replaceAll("\\$", "\\\\\\$")));
        }
    }

    public void sendMessage(ecoMessage message, Player player, Double amount, String creatureName, String weaponName)
    {
        if (shouldOutputMessages && message.isEnabled() && plugin.hasEconomy()) {
            player.sendMessage(message.getMessage().replaceAll(PLAYER_TOKEN, player.getName()).replaceAll(AMOUNT_TOKEN, ecoCreature.economy.format(amount).replaceAll("\\$", "\\\\\\$")).replaceAll(ITEM_TOKEN, toCamelCase(weaponName))
                    .replaceAll(CREATURE_TOKEN, creatureName));
        }
    }

    public void sendMessage(ecoMessage message, Player player, String creatureName, String weaponName)
    {
        if (shouldOutputMessages && message.isEnabled()) {
            player.sendMessage(message.getMessage().replaceAll(PLAYER_TOKEN, player.getName()).replaceAll(CREATURE_TOKEN, creatureName).replaceAll(ITEM_TOKEN, toCamelCase(weaponName)));
        }
    }

    private static String toCamelCase(String rawItemName)
    {
        String[] rawItemNameParts = rawItemName.split("_");
        String itemName = "";

        for (String itemNamePart : rawItemNameParts) {
            itemName = itemName + " " + toProperCase(itemNamePart);
        }

        if (itemName.trim().equals("Air")) {
            itemName = "Fists";
        }

        if (itemName.trim().equals("Bow")) {
            itemName = "Bow & Arrow";
        }

        return itemName.trim();
    }

    private static String toProperCase(String str)
    {
        if (str.length() < 1) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
