package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;

public class HeroesRewardSource extends RewardSource
{
    public HeroesRewardSource(ConfigurationSection config)
    {
        super(config);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (DependencyUtils.hasHeroes() && event instanceof HeroChangeLevelEvent) {
            return ((HeroChangeLevelEvent) event).getHero().getPlayer().getLocation();
        }
        else {
            throw new IllegalArgumentException("Unrecognized event");
        }
    }
}
