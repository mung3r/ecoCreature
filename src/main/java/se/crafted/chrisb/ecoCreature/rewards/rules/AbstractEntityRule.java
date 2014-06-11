package se.crafted.chrisb.ecoCreature.rewards.rules;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public abstract class AbstractEntityRule extends AbstractRule
{
    @Override
    public boolean isBroken(Event event)
    {
        return event instanceof EntityKilledEvent ? isBroken((EntityKilledEvent) event) : false;
    }

    protected abstract boolean isBroken(EntityKilledEvent event);

    @Override
    public void handleDrops(Event event)
    {
        if (event instanceof EntityKilledEvent) {
            if (isClearDrops()) {
                ((EntityKilledEvent) event).getDrops().clear();
            }
            if (isClearDrops() || isClearExpOrbs()) {
                ((EntityKilledEvent) event).setDroppedExp(0);
            }
        }
    }

    @Override
    public Player getKiller(Event event)
    {
        Player player = null;

        if (event instanceof EntityKilledEvent) {
            player = ((EntityKilledEvent) event).getKiller();
        }

        return player;
    }
}
