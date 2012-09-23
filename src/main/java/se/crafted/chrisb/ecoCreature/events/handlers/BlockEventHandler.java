package se.crafted.chrisb.ecoCreature.events.handlers;

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
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public class BlockEventHandler extends DefaultEventHandler
{
    public BlockEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public Set<RewardEvent> getRewardEvents(Event event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        if (event instanceof BlockBreakEvent) {
            events.addAll(getRewardEvents((BlockBreakEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> getRewardEvents(BlockBreakEvent event)
    {
        Set<RewardEvent> events = new HashSet<RewardEvent>();

        Player player = event.getPlayer();
        WorldSettings settings = plugin.getWorldSettings(player.getWorld());

        if (settings.hasRewardSource(event)) {
            Reward outcome = settings.getRewardSource(event).getOutcome(event);
            outcome.setGain(settings.getGainMultiplier(player));
            outcome.getMessage().addParameter(MessageToken.ITEM, EntityUtils.getItemNameInHand(player));
            outcome.getMessage().addParameter(MessageToken.CREATURE, outcome.getName());

            events.add(new RewardEvent(player, outcome));
        }

        return events;
    }
}
