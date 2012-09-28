package se.crafted.chrisb.ecoCreature.rewards.rules;

import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;

public abstract class AbstractRule implements Rule
{
    private boolean clearDrops;
    private Message message;

    public AbstractRule()
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
    public abstract boolean isBroken(EntityKilledEvent event);

    @Override
    public boolean isClearDrops()
    {
        return clearDrops;
    }

    @Override
    public void setClearDrops(boolean clearDrops)
    {
        this.clearDrops = clearDrops;
    }
}
