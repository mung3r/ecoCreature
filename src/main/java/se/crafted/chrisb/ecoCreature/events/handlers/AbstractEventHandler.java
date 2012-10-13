package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.PluginConfig;
import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public abstract class AbstractEventHandler implements RewardEventCreator
{
    private final ecoCreature plugin;

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
        return getWorldSettings(world);
    }

    protected PluginConfig getPluginConfig()
    {
        return plugin.getPluginConfig();
    }

    protected WorldSettings getWorldSettings(World world)
    {
        return getPluginConfig().getWorldSettings(world);
    }
}
