package se.crafted.chrisb.ecoCreature.entities;

import com.nijiko.permissions.PermissionHandler;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;
import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.utils.ecoConstants;

public class ecoBlockListener extends BlockListener
{
  public void onBlockBreak(BlockBreakEvent paramBlockBreakEvent)
  {
    Player localPlayer = paramBlockBreakEvent.getPlayer();
    Block localBlock = paramBlockBreakEvent.getBlock();
    if ((localBlock.getType().equals(Material.MOB_SPAWNER)) && (localPlayer != null) && (ecoCreature.Permissions.has(localPlayer, "ecoCreature.Creature.Spawner")))
    {
      ecoCreature.getRewardHandler().CashRegistry(localPlayer, 14, Material.getMaterial(localPlayer.getItemInHand().getTypeId()).name());
      for (int i = 0; i < ecoConstants.CD[14].length; i++)
      {
        double d = Math.floor(Math.random() * 100.0D);
        Random localRandom = new Random();
        if (d >= ecoConstants.CD[14][i][2])
          continue;
        int j = (int)ecoConstants.CD[14][i][1];
        int k = 0;
        if (ecoConstants.FD)
          k = j;
        else
          k = localRandom.nextInt(j) + 1;
        ItemStack localItemStack = new ItemStack((int)ecoConstants.CD[14][i][0], k, 0);
        if (localItemStack == null)
          continue;
        if (localItemStack.getAmount() == 0)
          localPlayer.sendMessage("This message should not appear, report to ecoCreature Dev!");
        else
          localBlock.getWorld().dropItemNaturally(localBlock.getLocation(), localItemStack);
      }
    }
  }
}