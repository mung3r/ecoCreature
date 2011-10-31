package se.crafted.chrisb.ecoCreature.managers;

import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoMessage;

public class ecoMessageManager
{
    private final static String PLAYER_TOKEN = "<plr>";
    private final static String AMOUNT_TOKEN = "<amt>";
    private final static String ITEM_TOKEN = "<itm>";
    private final static String CREATURE_TOKEN = "<crt>";

    public static boolean shouldOutputMessages;

    public static ecoMessage noBowRewardMessage;
    public static ecoMessage noCampMessage;
    public static ecoMessage deathPenaltyMessage;
    public static ecoMessage pvpRewardMessage;

    private ecoCreature plugin;

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

    public void sendMessage(ecoMessage message, Player player, Double amount)
    {
        if (shouldOutputMessages && message.isEnabled()) {
            player.sendMessage(message.getMessage().replaceAll(PLAYER_TOKEN, player.getName()).replaceAll(AMOUNT_TOKEN, ecoCreature.economy.format(amount).replaceAll("\\$", "\\\\\\$")));
        }
    }

    public void sendMessage(ecoMessage message, Player player, Double amount, String creatureName, String weaponName)
    {
        if (shouldOutputMessages && message.isEnabled()) {
            player.sendMessage(message.getMessage().replaceAll(PLAYER_TOKEN, player.getName()).replaceAll(AMOUNT_TOKEN, ecoCreature.economy.format(amount).replaceAll("\\$", "\\\\\\$")).replaceAll(ITEM_TOKEN, toCamelCase(weaponName)).replaceAll(CREATURE_TOKEN, creatureName));
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
