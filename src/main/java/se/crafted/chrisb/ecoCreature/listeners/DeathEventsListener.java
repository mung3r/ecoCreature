package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;

public class DeathEventsListener extends CustomEventListener implements Listener
{
    public DeathEventsListener()
    {
    }

    public void onCreatureKilledByPlayer(CreatureKilledByPlayerEvent event)
    {
    }

    public void onPlayerKilledByPlayer(PlayerKilledByPlayerEvent event)
    {
    }

    public void onCustomEvent(Event event)
    {
        if (event instanceof CreatureKilledByPlayerEvent) {
            onCreatureKilledByPlayer((CreatureKilledByPlayerEvent) event);
        }
        else if (event instanceof PlayerKilledByPlayerEvent) {
            onPlayerKilledByPlayer((PlayerKilledByPlayerEvent) event);
        }
    }
}
