package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.RewardEvent;

public class PluginEventHandler
{
    private List<RewardEventCreator> handlers;

    public PluginEventHandler()
    {
        handlers = new ArrayList<RewardEventCreator>();
    }

    public void add(RewardEventCreator handler)
    {
        handlers.add(handler);
    }

    public Set<RewardEvent> createRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        for (RewardEventCreator handler : handlers) {
            if (handler.canCreateRewardEvents(event)) {
                events = new HashSet<RewardEvent>();
                events.addAll(handler.createRewardEvents(event));
                break;
            }
        }

        return events;
    }
}
