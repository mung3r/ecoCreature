package se.crafted.chrisb.ecoCreature.messages;

public class NoRewardMessage extends BasicMessage implements Message
{
    public NoRewardMessage(MessageManager messageManager)
    {
        super(messageManager);
    }

    @Override
    public void send()
    {
        if (messageManager.shouldOutputNoReward) {
            super.send();
        }
    }
}
