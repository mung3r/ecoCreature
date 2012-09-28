package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.CustomType;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.PVPRewardSource;

public class PlayerKilledEventHandler extends AbstractEventHandler
{
    public PlayerKilledEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean canCreateRewardEvents(Event event)
    {
        return event instanceof PlayerKilledEvent;
    }

    @Override
    public Set<RewardEvent> createRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        if (event instanceof PlayerKilledEvent) {
            events = new HashSet<RewardEvent>();
            events.addAll(getRewardEvents((PlayerKilledEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(PlayerKilledEvent event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        Player killer = event.getKiller();
        Player victim = event.getVictim();
        WorldSettings settings = getSettings(killer.getWorld());

        if (settings.hasRewardSource(event)) {
            AbstractRewardSource winnerSource = settings.getRewardSource(event);
            Reward winnerOutcome = winnerSource.getOutcome(event);

            if (winnerSource instanceof PVPRewardSource && ((PVPRewardSource) winnerSource).isPercentReward()) {
                winnerOutcome.setCoin(DependencyUtils.getEconomy().getBalance(victim.getName()));
            }
            else {
                winnerOutcome.setGain(settings.getGainMultiplier(killer));
            }

            winnerOutcome.getMessage().addParameter(MessageToken.CREATURE, victim.getName());

            if (settings.isOverrideDrops() || (settings.isClearOnNoDrops() && !winnerOutcome.hasDrops())) {
                event.getDrops().clear();
            }

            if (winnerOutcome.getEntityDrops().contains(EntityType.EXPERIENCE_ORB)) {
                event.setDroppedExp(0);
            }

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(killer, winnerOutcome));

            if (settings.hasRewardSource(CustomType.DEATH_PENALTY)) {
                AbstractRewardSource loserSource = settings.getRewardSource(CustomType.DEATH_PENALTY);
                Reward loserOutcome = loserSource.getOutcome(event);
                loserOutcome.setCoin(winnerOutcome.getCoin());
                loserOutcome.setGain(-winnerOutcome.getGain());

                events.add(new RewardEvent(victim, loserOutcome));
            }
        }

        return events;
    }
}
