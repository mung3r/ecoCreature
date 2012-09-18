package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;
import se.crafted.chrisb.ecoCreature.rewards.sources.DeathPenaltySource;
import se.crafted.chrisb.ecoCreature.rewards.sources.PVPRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.RewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.CustomType;

public class PlayerDeathEventHandler extends DefaultEventHandler
{
    public PlayerDeathEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public Set<RewardEvent> getRewardEvents(Event event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        if (event instanceof PlayerKilledEvent) {
            events.addAll(getRewardEvents((PlayerKilledEvent) event));
        }
        else if (event instanceof PlayerDeathEvent) {
            events.addAll(getRewardEvents((PlayerDeathEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(PlayerKilledEvent event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        Player killer = event.getKiller();
        Player victim = event.getVictim();
        WorldSettings settings = plugin.getWorldSettings(killer.getWorld());

        if (settings.hasRewardSource(event)) {
            RewardSource winnerSource = settings.getRewardSource(event);
            Reward winnerOutcome = winnerSource.getOutcome(event);

            if (winnerSource instanceof PVPRewardSource && ((PVPRewardSource) winnerSource).isPercentReward()) {
                winnerOutcome.setCoin(DependencyUtils.getEconomy().getBalance(victim.getName()));
            }
            else {
                winnerOutcome.setGain(settings.getGainMultiplier(killer));
            }

            winnerOutcome.getMessage().addParameter(MessageToken.CREATURE, victim.getName());

            if (winnerOutcome.getExp() > 0) {
                event.setDroppedExp(0);
            }

            if (settings.isOverrideDrops()) {
                event.getDrops().clear();
            }

            events.add(new RewardEvent(killer, winnerOutcome));

            if (settings.hasRewardSource(CustomType.DEATH_PENALTY)) {
                DeathPenaltySource loserSource = (DeathPenaltySource) settings.getRewardSource(CustomType.DEATH_PENALTY);
                Reward loserOutcome = loserSource.getOutcome(event);
                loserOutcome.setCoin(winnerOutcome.getCoin());
                loserOutcome.setGain(-winnerOutcome.getGain());

                events.add(new RewardEvent(victim, loserOutcome));
            }
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(PlayerDeathEvent event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        Player player = event.getEntity();
        WorldSettings settings = plugin.getWorldSettings(player.getWorld());

        if (settings.hasRewardSource(event)) {
            RewardSource source = settings.getRewardSource(CustomType.DEATH_PENALTY);
            Reward outcome = source.getOutcome(event);

            if (source instanceof DeathPenaltySource && ((DeathPenaltySource) source).isPercentPenalty()) {
                outcome.setCoin(DependencyUtils.getEconomy().getBalance(player.getName()));
            }

            events.add(new RewardEvent(player, outcome));
        }

        return events;
    }
}
