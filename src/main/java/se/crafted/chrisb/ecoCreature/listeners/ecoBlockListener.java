package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.utils.ecoLogger;

public class ecoBlockListener extends BlockListener
{
    private final ecoCreature plugin;
    private final ecoLogger log;

    public ecoBlockListener(ecoCreature plugin)
    {
        this.plugin = plugin;
        log = this.plugin.getLogger();
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