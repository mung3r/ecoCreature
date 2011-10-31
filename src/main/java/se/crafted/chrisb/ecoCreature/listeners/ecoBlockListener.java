package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoBlockListener extends BlockListener
{
    private final ecoCreature plugin;

    public ecoBlockListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        plugin.getRewardManager().registerSpawnerBreak(event.getPlayer(), event.getBlock());
    }
}