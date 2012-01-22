package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoBlockListener implements Listener
{
    public ecoBlockListener()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        ecoCreature.getRewardManager(event.getPlayer()).registerSpawnerBreak(event.getPlayer(), event.getBlock());
    }
}