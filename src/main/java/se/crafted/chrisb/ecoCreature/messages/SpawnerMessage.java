package se.crafted.chrisb.ecoCreature.messages;

public class SpawnerMessage extends BasicMessage
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
