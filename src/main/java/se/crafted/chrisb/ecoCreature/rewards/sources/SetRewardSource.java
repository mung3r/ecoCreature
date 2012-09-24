package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class SetRewardSource extends RewardSource
{
    public SetRewardSource(ConfigurationSection config)
    {
        super(config);
    }

    @Override
    protected Location getLocation(Event event)
    {
        return null;
    }
}
