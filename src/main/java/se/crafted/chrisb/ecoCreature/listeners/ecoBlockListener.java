package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoBlockListener extends BlockListener
{
    public ecoBlockListener()
    {
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        ecoCreature.getRewardManager(event.getPlayer()).registerSpawnerBreak(event.getPlayer(), event.getBlock());
    }
}