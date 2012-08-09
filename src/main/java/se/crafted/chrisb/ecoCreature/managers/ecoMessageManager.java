package se.crafted.chrisb.ecoCreature.managers;

import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoMessage;

public class ecoMessageManager
{
    private static final String PLAYER_TOKEN = "<plr>";
    private static final String AMOUNT_TOKEN = "<amt>";
    private static final String ITEM_TOKEN = "<itm>";
    private static final String CREATURE_TOKEN = "<crt>";

    public static final String NO_CAMP_MESSAGE = "&7You find no rewards camping monster spawners.";
    public static final String NO_BOW_REWARD_MESSAGE = "&7You find no rewards on this creature.";
    public static final String DEATH_PENALTY_MESSAGE = "&7You wake up to find &6<amt>&7 missing from your pockets!";
    public static final String PVP_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for murdering &5<crt>&7.";

    public static final String NO_REWARD_MESSAGE = "&7You slayed a &5<crt>&7 using a &3<itm>&7.";
    public static final String REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for slaying a &5<crt>&7.";
    public static final String PENALTY_MESSAGE = "&7You are penalized &6<amt>&7 for slaying a &5<crt>&7.";

    public boolean shouldOutputMessages;
    public boolean shouldLogCoinRewards;

    public ecoMessage noBowRewardMessage;
    public ecoMessage noCampMessage;
    public ecoMessage deathPenaltyMessage;
    public ecoMessage pvpRewardMessage;

    private final ecoCreature plugin;

    public ecoMessageManager(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    public void sendMessage(ecoMessage message, Player player)
    {
        if (shouldOutputMessages && message.isEnabled()) {
            player.sendMessage(message.getMessage().replaceAll(PLAYER_TOKEN, player.getName()));
        }
    }

    public void sendMessage(ecoMessage message, Player player, double amount)
    {
        if (shouldOutputMessages && message.isEnabled() && plugin.hasEconomy()) {
            player.sendMessage(message.getMessage().replaceAll(PLAYER_TOKEN, player.getName()).replaceAll(AMOUNT_TOKEN, plugin.getEconomy().format(amount).replaceAll("\\$", "\\\\\\$")));
        }

        if (shouldLogCoinRewards) {
            ecoCreature.getEcoLogger().info(player.getName() + " received " + plugin.getEconomy().format(amount));
        }
    }

    public void sendMessage(ecoMessage message, Player player, double amount, String creatureName, String weaponName)
    {
        if (shouldOutputMessages && message.isEnabled() && plugin.hasEconomy()) {
            player.sendMessage(message.getMessage().replaceAll(PLAYER_TOKEN, player.getName()).replaceAll(AMOUNT_TOKEN, plugin.getEconomy().format(amount).replaceAll("\\$", "\\\\\\$")).replaceAll(ITEM_TOKEN, toCamelCase(weaponName)).replaceAll(CREATURE_TOKEN, creatureName));
        }

        if (shouldLogCoinRewards) {
            ecoCreature.getEcoLogger().info(player.getName() + " received " + plugin.getEconomy().format(amount));
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
        StringBuilder itemNameBuilder = new StringBuilder("");

        for (String itemNamePart : rawItemNameParts) {
            itemNameBuilder.append(" ").append(toProperCase(itemNamePart));
        }

        String itemName = itemNameBuilder.toString();
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
