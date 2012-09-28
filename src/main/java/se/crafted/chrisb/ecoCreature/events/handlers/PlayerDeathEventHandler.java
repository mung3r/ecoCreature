package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.CustomType;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.DeathPenaltySource;

public class PlayerDeathEventHandler extends AbstractEventHandler
{
    public PlayerDeathEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean canCreateRewardEvents(Event event)
    {
        return event instanceof PlayerDeathEvent;
    }

    @Override
    public Set<RewardEvent> createRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        if (canCreateRewardEvents(event)) {
            events = new HashSet<RewardEvent>();
            events.addAll(getRewardEvents((PlayerDeathEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(PlayerDeathEvent event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        Player player = event.getEntity();
        WorldSettings settings = getSettings(player.getWorld());

        if (settings.hasRewardSource(event)) {
            AbstractRewardSource source = settings.getRewardSource(CustomType.DEATH_PENALTY);
            Reward outcome = source.getOutcome(event);

            if (source instanceof DeathPenaltySource && ((DeathPenaltySource) source).isPercentPenalty()) {
                outcome.setCoin(DependencyUtils.getEconomy().getBalance(player.getName()));
            }

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(player, outcome));
        }

        return events;
    }
}
