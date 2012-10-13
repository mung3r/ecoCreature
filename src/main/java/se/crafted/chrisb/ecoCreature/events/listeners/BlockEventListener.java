package se.crafted.chrisb.ecoCreature.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.events.handlers.PluginEventHandler;

public class BlockEventListener implements Listener
{
    private final PluginEventHandler handler;

    public BlockEventListener(PluginEventHandler handler)
    {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        for (RewardEvent rewardEvent : handler.createRewardEvents(event)) {
            Bukkit.getPluginManager().callEvent(rewardEvent);
        }
    }
}
