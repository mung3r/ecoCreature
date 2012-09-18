package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public class DefaultEventHandler implements RewardEventHandler
{
    protected ecoCreature plugin;

    public DefaultEventHandler(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public Set<RewardEvent> getRewardEvents(Event event)
    {
        return new HashSet<RewardEvent>();
    }

    @Override
    public WorldSettings getSettings(World world)
    {
        return plugin.getWorldSettings(world);
    }
}
