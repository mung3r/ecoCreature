package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public class BlockEventHandler extends AbstractEventHandler
{
    public BlockEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean canCreateRewardEvents(Event event)
    {
        return event instanceof BlockBreakEvent;
    }

    @Override
    public Set<RewardEvent> createRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        if (event instanceof BlockBreakEvent) {
            events = new HashSet<RewardEvent>();
            events.addAll(getRewardEvents((BlockBreakEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(BlockBreakEvent event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        Player player = event.getPlayer();
        WorldSettings settings = getSettings(player.getWorld());

        if (settings.hasReward(event)) {
            Reward reward = settings.getReward(event);
            reward.setGain(settings.getGainMultiplier(player));
            reward.getMessage().addParameter(MessageToken.ITEM, EntityUtils.getItemNameInHand(player));
            reward.getMessage().addParameter(MessageToken.CREATURE, reward.getName());

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(player, reward));
        }

        return events;
    }
}
