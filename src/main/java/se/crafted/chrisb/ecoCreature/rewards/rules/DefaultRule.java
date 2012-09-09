package se.crafted.chrisb.ecoCreature.rewards.rules;

import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;

public class DefaultRule implements Rule
{
    private boolean clearDrops;
    private Message message;

    public DefaultRule()
    {
        clearDrops = false;
        message = new DefaultMessage();
    }

    @Override
    public Message getMessage()
    {
        return message;
    }

    @Override
    public void setMessage(Message message)
    {
        this.message = message;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        return false;
    }

    @Override
    public boolean isClearDrops()
    {
        return clearDrops;
    }

    @Override
    public void setClearDropsEnabled(boolean clearDrops)
    {
        this.clearDrops = clearDrops;
    }
}
