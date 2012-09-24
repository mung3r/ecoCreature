package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.CustomType;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public class HeroesEventHandler extends AbstractEventHandler
{
    public HeroesEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public Set<RewardEvent> getRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        if (event instanceof HeroChangeLevelEvent) {
            events = new HashSet<RewardEvent>();
            events.addAll(getRewardEvents((HeroChangeLevelEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(HeroChangeLevelEvent event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        Player player = event.getHero().getPlayer();
        WorldSettings settings = plugin.getWorldSettings(player.getWorld());

        if (settings.hasRewardSource(event)) {
            Reward outcome = settings.getRewardSource(CustomType.HERO_MASTERED).getOutcome(event);

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(player, outcome));
        }

        return events;
    }
}
