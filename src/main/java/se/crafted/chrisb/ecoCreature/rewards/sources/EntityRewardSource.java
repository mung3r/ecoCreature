package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

public class EntityRewardSource extends AbstractRewardSource
{
    public EntityRewardSource(ConfigurationSection config)
    {
        super(config);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            return ((PlayerKilledEvent) event).getEntity().getLocation();
        }
        else if (event instanceof EntityKilledEvent) {
            return ((EntityKilledEvent) event).getEntity().getLocation();
        }
        else {
            throw new IllegalArgumentException("Unrecognized event");
        }
    }
}
