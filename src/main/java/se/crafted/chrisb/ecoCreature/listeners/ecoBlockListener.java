package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoBlockListener implements Listener
{
    private final ecoCreature plugin;

    public ecoBlockListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        plugin.getRewardManager(event.getPlayer().getWorld()).registerSpawnerBreak(event.getPlayer(), event.getBlock());
    }
}