package se.crafted.chrisb.ecoCreature.messages;

public class SpawnerMessage extends BasicMessage implements Message
{
    public SpawnerMessage(MessageManager messageManager)
    {
        super(messageManager);
    }

    @Override
    public void send()
    {
        if (messageManager.shouldOutputSpawnerCamp) {
            super.send();
        }
    }
}
