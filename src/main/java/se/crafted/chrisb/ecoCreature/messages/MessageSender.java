package se.crafted.chrisb.ecoCreature.messages;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class MessageSender
{
    String playerName;
    Message message;

    public MessageSender(Player player, Message message)
    {
        this(player.getName(), message);
    }

    public MessageSender(String playerName, Message message)
    {
        this.playerName = playerName;
        this.message = message;
    }

    public void send()
    {
        String template = message.getTemplate();
        Map<MessageToken, String> parameters = message.getParameters();

        if (template != null && template.length() > 0) {
            for (MessageToken token : parameters.keySet()) {
                if (token == MessageToken.AMOUNT) {
                    template = template.replaceAll(token.toString(), parameters.get(token).replaceAll("\\$", "\\\\\\$"));
                }
                else if (token == MessageToken.ITEM) {
                    template = template.replaceAll(token.toString(), toCamelCase(parameters.get(token)));
                }
                else {
                    template = template.replaceAll(token.toString(), parameters.get(token));
                }
            }

            if (message.isMessageOutputEnabled()) {
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    player.sendMessage(template);
                }
            }

            if (message.isCoinLoggingEnabled() && parameters.containsKey(MessageToken.AMOUNT)) {
                ECLogger.getInstance().info(removeColorCodes(String.format("%s: %s", parameters.get(MessageToken.PLAYER), template)));
            }
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
