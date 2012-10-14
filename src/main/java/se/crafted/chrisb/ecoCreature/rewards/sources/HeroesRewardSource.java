package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.settings.types.HeroesRewardType;

public class HeroesRewardSource extends AbstractRewardSource
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

    public static AbstractRewardSource createRewardSource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        switch (HeroesRewardType.fromName(name)) {
            case HERO_LEVELED:
            case HERO_MASTERED:
                source = new HeroesRewardSource(config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + name);
        }
        return source;
    }
}
