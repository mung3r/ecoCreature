package se.crafted.chrisb.ecoCreature.messages;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class BasicMessage implements Message
{
    protected MessageManager messageManager;
    protected String template;
    protected Map<MessageToken, String> parameters;

    public BasicMessage(MessageManager messageManager)
    {
        this.messageManager = messageManager;
        parameters = new HashMap<MessageToken, String>();
    }

    @Override
    public void setTemplate(String template)
    {
        this.template = template;
    }

    @Override
    public void addParameter(MessageToken token, String parameter)
    {
        parameters.put(token, parameter);
    }

    @Override
    public void send()
    {
        for (MessageToken token : parameters.keySet()) {
            if (token == MessageToken.AMOUNT_TOKEN) {
                template = template.replaceAll(token.toString(), parameters.get(token).replaceAll("\\$", "\\\\\\$"));
            }
            else if (token == MessageToken.ITEM_TOKEN) {
                template = template.replaceAll(token.toString(), toCamelCase(parameters.get(token)));
            }
            else {
                template = template.replaceAll(token.toString(), parameters.get(token));
            }
        }

        if (messageManager.shouldOutputMessages && parameters.containsKey(MessageToken.PLAYER_TOKEN)) {
            Bukkit.getPlayer(parameters.get(MessageToken.PLAYER_TOKEN)).sendMessage(template);
        }

        if (messageManager.shouldLogCoinRewards && parameters.containsKey(MessageToken.AMOUNT_TOKEN)) {
            ECLogger.getInstance().info(removeColorCodes(String.format("%s: %s", parameters.get(MessageToken.PLAYER_TOKEN), template)));
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

    private static String removeColorCodes(String msg)
    {
        return msg.replaceAll("(?i)§[a-fklmnor0-9]", "");
    }
}
