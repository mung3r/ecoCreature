package se.crafted.chrisb.ecoCreature.rewards.rules;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

public abstract class AbstractPlayerRule extends AbstractRule
{
    @Override
    public boolean isBroken(Event event)
    {
        return event instanceof PlayerKilledEvent ? isBroken((PlayerKilledEvent) event) : false;
    }

    protected abstract boolean isBroken(PlayerKilledEvent event);

    @Override
    public void handleDrops(Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            if (isClearDrops()) {
                ((PlayerKilledEvent) event).getDrops().clear();
            }
            if (isClearDrops() || isClearExpOrbs()) {
                ((PlayerKilledEvent) event).setDroppedExp(0);
            }
        }
    }

    @Override
    public Player getKiller(Event event)
    {
        Player player = null;

        if (event instanceof PlayerKilledEvent) {
            player = ((PlayerKilledEvent) event).getKiller();
        }

        return player;
    }
}
