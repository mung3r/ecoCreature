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
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;

public class BlockEventHandler extends AbstractEventHandler
{
    public BlockEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public Set<RewardEvent> getRewardEvents(Event event)
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
        WorldSettings settings = plugin.getWorldSettings(player.getWorld());

        if (settings.hasRewardSource(event)) {
            Reward outcome = settings.getRewardSource(event).getOutcome(event);
            outcome.setGain(settings.getGainMultiplier(player));
            outcome.getMessage().addParameter(MessageToken.ITEM, EntityUtils.getItemNameInHand(player));
            outcome.getMessage().addParameter(MessageToken.CREATURE, outcome.getName());

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(player, outcome));
        }

        return events;
    }
}
