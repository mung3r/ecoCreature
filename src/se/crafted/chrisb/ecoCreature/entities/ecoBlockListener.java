package se.crafted.chrisb.ecoCreature.entities;

import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.utils.ecoConstants;

public class ecoBlockListener extends BlockListener
{
    private final ecoCreature plugin;

    public ecoBlockListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent blockBreakEvent)
    {
        if (blockBreakEvent.isCancelled())
            return;

        Player player = blockBreakEvent.getPlayer();
        if (player == null)
            return;

        Block block = blockBreakEvent.getBlock();
        if (block.getType().equals(Material.MOB_SPAWNER) && ecoCreature.permissionsHandler.has(player, "ecoCreature.Creature.Spawner")) {
            plugin.getRewardHandler().registerReward(player, 15, Material.getMaterial(player.getItemInHand().getTypeId()).name());
            for (int i = 0; i < ecoConstants.CreatureDrop[15].length; i++) {
                double d = Math.floor(Math.random() * 100.0D);
                Random random = new Random();
                if (d >= ecoConstants.CreatureDrop[15][i][2])
                    continue;
                int j = (int) ecoConstants.CreatureDrop[15][i][1];
                int k = 0;
                if (ecoConstants.isFixedDrops)
                    k = j;
                else
                    k = random.nextInt(j) + 1;
                ItemStack itemStack = new ItemStack((int) ecoConstants.CreatureDrop[15][i][0], k, (short) 0);
                if (itemStack != null) {
                    if (itemStack.getAmount() == 0)
                        ecoCreature.logger.log(Level.SEVERE, "[ecoCreature] Item stack amount is zero.");
                    else
                        block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
                }
            }
        }
    }
}