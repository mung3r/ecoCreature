package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.settings.types.StreakRewardType;

public class StreakRewardSource extends AbstractRewardSource
{
    public StreakRewardSource(ConfigurationSection config)
    {
        super(config);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (DependencyUtils.hasDeathTpPlus() && event instanceof DeathStreakEvent) {
            return ((DeathStreakEvent) event).getPlayer().getLocation();
        }
        else if (DependencyUtils.hasDeathTpPlus() && event instanceof KillStreakEvent) {
            return ((KillStreakEvent) event).getPlayer().getLocation();
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    public static AbstractRewardSource createRewardSource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        switch (StreakRewardType.fromName(name)) {
            case DEATH_STREAK:
            case KILL_STREAK:
                source = new StreakRewardSource(config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + name);
        }
        return source;
    }
}
