package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.CustomType;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public class StreakEventHandler extends DefaultEventHandler
{
    public StreakEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public Set<RewardEvent> getRewardEvents(Event event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        if (event instanceof DeathStreakEvent) {
            events.addAll(getEvent((DeathStreakEvent) event));
        }
        else if (event instanceof KillStreakEvent) {
            events.addAll(getEvent((KillStreakEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getEvent(DeathStreakEvent event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        Player player = event.getPlayer();
        int deaths = event.getDeaths();
        WorldSettings settings = plugin.getWorldSettings(player.getWorld());

        if (settings.hasRewardSource(event)) {
            Reward outcome = settings.getRewardSource(CustomType.DEATH_STREAK).getOutcome(event);
            outcome.setGain(deaths);

            events.add(new RewardEvent(player, outcome));
        }

        return events;
    }

    private Set<RewardEvent> getEvent(KillStreakEvent event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        Player player = event.getPlayer();
        int kills = event.getKills();
        WorldSettings settings = plugin.getWorldSettings(player.getWorld());

        if (settings.hasRewardSource(event)) {
            Reward outcome = settings.getRewardSource(CustomType.KILL_STREAK).getOutcome(event);
            outcome.setGain(kills);

            events.add(new RewardEvent(player, outcome));
        }

        return events;
    }
}
