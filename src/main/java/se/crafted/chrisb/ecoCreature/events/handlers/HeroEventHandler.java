package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.RewardSettings;
import se.crafted.chrisb.ecoCreature.rewards.sources.RewardSourceType;

public class HeroEventHandler extends DefaultEventHandler
{
    public HeroEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public Set<RewardEvent> getRewardEvents(Event event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        if (event instanceof HeroChangeLevelEvent) {
            events.addAll(getRewardEvents((HeroChangeLevelEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(HeroChangeLevelEvent event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        Player player = event.getHero().getPlayer();
        RewardSettings settings = plugin.getRewardSettings(player.getWorld());

        if (settings.hasRewardSource(event)) {
            Reward outcome = settings.getRewardSource(RewardSourceType.HERO_MASTERED).getOutcome(event);

            events.add(new RewardEvent(player, outcome));
        }

        return events;
    }
}
