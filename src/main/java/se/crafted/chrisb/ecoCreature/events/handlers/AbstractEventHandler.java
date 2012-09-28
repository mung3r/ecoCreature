package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public abstract class AbstractEventHandler implements RewardEventCreator
{
    private ecoCreature plugin;

    public AbstractEventHandler(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public abstract boolean canCreateRewardEvents(Event event);

    @Override
    public abstract Set<RewardEvent> createRewardEvents(Event event);

    @Override
    public WorldSettings getSettings(World world)
    {
        return plugin.getWorldSettings(world);
    }
}
