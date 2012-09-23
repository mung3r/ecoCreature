package se.crafted.chrisb.ecoCreature.messages;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class MessageHandler
{
    private String playerName;
    private Message message;

    public MessageHandler(Player player, Message message)
    {
        this(player.getName(), message);
    }

    public MessageHandler(String playerName, Message message)
    {
        this.playerName = playerName;
        this.message = message;
    }

    public void send()
    {
        String assembledMessage = message.getAssembledMessage();

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

    private static String getAwardedPlayerName(Message message)
    {
        return message.getParameters().get(MessageToken.PLAYER);
    }

    private static String removeColorCodes(String msg)
    {
        return msg.replaceAll("(?i)§[a-fklmnor0-9]", "");
    }
}
