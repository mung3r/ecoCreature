package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

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

        if (settings.hasReward(event)) {
            Reward killerReward = getWinnerReward(event);

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(killer, killerReward));

            PlayerDeathEvent deathEvent = new PlayerDeathEvent(event.getEntity(), event.getDrops(), event.getDroppedExp(), event.getNewExp(),
                    event.getNewTotalExp(), event.getNewLevel(), event.getDeathMessage());
            if (settings.hasReward(deathEvent)) {
                Reward penalty = settings.getReward(deathEvent);
                penalty.setCoin(killerReward.getCoin());
                penalty.setGain(-killerReward.getGain());

                events.add(new RewardEvent(victim, penalty));
            }
        }

        return events;
    }

    private Reward getWinnerReward(PlayerKilledEvent event)
    {
        WorldSettings settings = getSettings(event.getEntity().getWorld());
        Reward reward = settings.getReward(event);

        reward.getMessage().addParameter(MessageToken.CREATURE, event.getVictim().getName());

        if (settings.isOverrideDrops() || (settings.isClearOnNoDrops() && !reward.hasDrops())) {
            event.getDrops().clear();
        }

        if (reward.getEntityDrops().contains(EntityType.EXPERIENCE_ORB)) {
            event.setDroppedExp(0);
        }

        return reward;
    }
}
