package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.CustomType;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public class KillStreakEventHandler extends AbstractEventHandler
{
    public KillStreakEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean canCreateRewardEvents(Event event)
    {
        return event instanceof KillStreakEvent;
    }

    @Override
    public Set<RewardEvent> createRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        if (canCreateRewardEvents(event)) {
            events = new HashSet<RewardEvent>();
            events.addAll(getEvent((KillStreakEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getEvent(KillStreakEvent event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        Player player = event.getPlayer();
        int kills = event.getKills();
        WorldSettings settings = getSettings(player.getWorld());

        if (settings.hasRewardSource(event)) {
            Reward outcome = settings.getRewardSource(CustomType.KILL_STREAK).getOutcome(event);
            outcome.setGain(kills);

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(player, outcome));
        }

        return events;
    }
}
