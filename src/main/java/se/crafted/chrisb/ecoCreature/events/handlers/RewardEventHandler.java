package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public interface RewardEventHandler
{
    Set<RewardEvent> getRewardEvents(Event event);

    WorldSettings getSettings(World world);
}
