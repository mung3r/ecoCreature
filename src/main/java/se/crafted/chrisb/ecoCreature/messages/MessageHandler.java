package se.crafted.chrisb.ecoCreature.messages;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class MessageHandler
{
    private Message message;
    private Map<MessageToken, String> parameters;

    public MessageHandler(Message message, Map<MessageToken, String> parameters)
    {
        this.message = message;
        this.parameters = parameters;
    }

    public void send(Player player)
    {
        send(player.getName());
    }

    public void send(String playerName)
    {
        String assembledMessage = message.getAssembledMessage(parameters);

        if (assembledMessage != null && assembledMessage.length() > 0) {
            if (message.isMessageOutputEnabled()) {
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    player.sendMessage(assembledMessage);
                }
            }

            if (message.isAmountInMessage() && message.isCoinLoggingEnabled()) {
                ECLogger.getInstance().info(removeColorCodes(String.format("%s: %s", getAwardedPlayerName(message), assembledMessage)));
            }
        }
    }

    private String getAwardedPlayerName(Message message)
    {
        return parameters.get(MessageToken.PLAYER);
    }

    private static String removeColorCodes(String msg)
    {
        return msg.replaceAll("(?i)§[a-fklmnor0-9]", "");
    }
}
