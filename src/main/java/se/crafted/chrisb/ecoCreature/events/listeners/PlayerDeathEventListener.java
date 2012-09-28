package se.crafted.chrisb.ecoCreature.events.listeners;

import java.util.Collections;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.commons.EventUtils;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.events.handlers.GameEventHandler;

public class PlayerDeathEventListener implements Listener
{
    private final GameEventHandler handler;

    public PlayerDeathEventListener(GameEventHandler handler)
    {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(PlayerDeathEvent event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        if (EventUtils.isPVPDeath(event)) {
            events = handler.createRewardEvents(PlayerKilledEvent.createEvent(event));
        }
        else if (!EventUtils.isSuicide(event)) {
            events = handler.createRewardEvents(event);
        }

        for (RewardEvent rewardEvent : events) {
            Bukkit.getPluginManager().callEvent(rewardEvent);
        }
    }
}
