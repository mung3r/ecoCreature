package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.RewardSettings;
import se.crafted.chrisb.ecoCreature.rewards.sources.DeathPenaltySource;
import se.crafted.chrisb.ecoCreature.rewards.sources.PVPRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.RewardSourceType;

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
        RewardSettings settings = plugin.getRewardSettings(killer.getWorld());

        if (DependencyUtils.hasPermission(killer, "reward.player")) {
            Reward winnerOutcome = null;

            if (settings.hasRewardSource(RewardSourceType.PLAYER)) {
                winnerOutcome = settings.getRewardSource(RewardSourceType.PLAYER).getOutcome(victim.getLocation());
                winnerOutcome.setGain(settings.getGainMultiplier(killer));
            }
            else if (DependencyUtils.hasEconomy() && settings.getRewardSource(RewardSourceType.LEGACY_PVP) instanceof PVPRewardSource) {
                PVPRewardSource reward = (PVPRewardSource) settings.getRewardSource(RewardSourceType.LEGACY_PVP);
                winnerOutcome = reward.getOutcome(victim.getLocation());
                if (reward.isPercentReward()) {
                    winnerOutcome.setCoin(DependencyUtils.getEconomy().getBalance(victim.getName()));
                }
            }

            if (winnerOutcome != null) {
                winnerOutcome.getMessage().addParameter(MessageToken.CREATURE, victim.getName());

                if (winnerOutcome.getExp() > 0) {
                    event.setDroppedExp(0);
                }

                if (settings.isOverrideDrops()) {
                    event.getDrops().clear();
                }

                events.add(new RewardEvent(killer, winnerOutcome));

                if (settings.getRewardSource(RewardSourceType.DEATH_PENALTY) instanceof DeathPenaltySource) {
                    DeathPenaltySource reward = (DeathPenaltySource) settings.getRewardSource(RewardSourceType.DEATH_PENALTY);
                    Reward loserOutcome = reward.getOutcome(victim.getLocation());
                    loserOutcome.setCoin(winnerOutcome.getCoin());
                    loserOutcome.setGain(-winnerOutcome.getGain());

                    events.add(new RewardEvent(victim, loserOutcome));
                }
            }
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(PlayerDeathEvent event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        Player player = event.getEntity();
        RewardSettings settings = plugin.getRewardSettings(player.getWorld());

        if (!DependencyUtils.hasPermission(player, "reward.deathpenalty") && DependencyUtils.hasEconomy()) {
            if (DependencyUtils.hasEconomy() && settings.getRewardSource(RewardSourceType.DEATH_PENALTY) instanceof DeathPenaltySource) {
                DeathPenaltySource reward = (DeathPenaltySource) settings.getRewardSource(RewardSourceType.DEATH_PENALTY);
                Reward outcome = reward.getOutcome(player.getLocation());

                if (reward.isPercentPenalty()) {
                    outcome.setCoin(DependencyUtils.getEconomy().getBalance(player.getName()));
                }

                events.add(new RewardEvent(player, outcome));
            }
        }
        else {
            ECLogger.getInstance().debug("No reward for " + player.getName() + " due to lack of permission for " + RewardSourceType.DEATH_PENALTY.getName());
        }

        return events;
    }
}
