package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.RewardSettings;

public interface RewardEventHandler
{
    Set<RewardEvent> getRewardEvents(Event event);

    RewardSettings getSettings(World world);
}
