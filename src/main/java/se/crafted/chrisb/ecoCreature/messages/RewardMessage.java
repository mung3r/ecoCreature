package se.crafted.chrisb.ecoCreature.messages;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class RewardMessage extends BasicMessage implements Message
{
    public RewardMessage(MessageManager messageManager)
    {
        super(messageManager);
    }

    @Override
    public void send()
    {
        if (ecoCreature.hasEconomy()) {
            super.send();
        }
    }
}
