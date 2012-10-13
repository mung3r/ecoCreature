package se.crafted.chrisb.ecoCreature.events.listeners;

import java.util.Collections;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.commons.EventUtils;
import se.crafted.chrisb.ecoCreature.events.EntityFarmedEvent;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.events.handlers.PluginEventHandler;

public class EntityDeathEventListener implements Listener
{
    private final PluginEventHandler handler;

    public EntityDeathEventListener(PluginEventHandler handler)
    {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event instanceof PlayerDeathEvent) {
            return;
        }

        Set<RewardEvent> events = Collections.emptySet();

        if (EventUtils.isEntityKilledEvent(event)) {
            events = handler.createRewardEvents(EntityKilledEvent.createEvent(event));
        }
        else if (EventUtils.isEntityFarmed(event) || EventUtils.isEntityFireFarmed(event)) {
            events = handler.createRewardEvents(EntityFarmedEvent.createEvent(event));
        }

        for (RewardEvent rewardEvent : events) {
            Bukkit.getPluginManager().callEvent(rewardEvent);
        }
    }
}
