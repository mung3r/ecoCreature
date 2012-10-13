package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

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

        if (event instanceof PlayerDeathEvent) {
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

        if (settings.hasReward(event)) {
            Reward reward = settings.getReward(event);

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(player, reward));
        }

        return events;
    }
}
