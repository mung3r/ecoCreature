package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

public class MaterialRewardSource extends RewardSource
{
    public MaterialRewardSource(ConfigurationSection config)
    {
        super(config);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (event instanceof BlockBreakEvent) {
            return ((BlockBreakEvent) event).getBlock().getLocation();
        }
        else {
            throw new IllegalArgumentException("Unrecognized event");
        }
    }
}
