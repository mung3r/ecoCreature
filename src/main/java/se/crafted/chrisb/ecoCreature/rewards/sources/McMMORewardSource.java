package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;

public class McMMORewardSource extends AbstractRewardSource
{
    public McMMORewardSource(ConfigurationSection config)
    {
        super(config);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (DependencyUtils.hasMcMMO() && event instanceof McMMOPlayerLevelUpEvent) {
            return ((McMMOPlayerLevelUpEvent) event).getPlayer().getLocation();
        }
        else {
            throw new IllegalArgumentException();
        }
    }
}
