package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public interface RewardEventCreator
{
    boolean canCreateRewardEvents(Event event);

    Set<RewardEvent> createRewardEvents(Event event);

    WorldSettings getSettings(World world);
}
