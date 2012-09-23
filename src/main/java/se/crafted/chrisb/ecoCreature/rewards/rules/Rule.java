package se.crafted.chrisb.ecoCreature.rewards.rules;

import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.Message;

public interface Rule
{
    boolean isBroken(EntityKilledEvent event);

    boolean isClearDrops();

    void setClearDrops(boolean clearDrops);

    Message getMessage();

    void setMessage(Message message);
}
