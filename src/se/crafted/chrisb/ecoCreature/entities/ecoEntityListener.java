package se.crafted.chrisb.ecoCreature.entities;

import com.nijiko.permissions.PermissionHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.utils.ecoConstants;

public class ecoEntityListener extends EntityListener
{
  Server srv = null;
  private Map<Entity, Player> recent = new HashMap();

  public ecoEntityListener(Server paramServer)
  {
    this.srv = paramServer;
  }

  public void onEntityDamage(EntityDamageEvent paramEntityDamageEvent)
  {
    if (paramEntityDamageEvent.isCancelled())
      return;
    if (((paramEntityDamageEvent.getEntity() instanceof LivingEntity)) && (!(paramEntityDamageEvent.getEntity() instanceof Player)))
    {
      LivingEntity localLivingEntity = (LivingEntity)paramEntityDamageEvent.getEntity();
      Player localPlayer = null;
      Object localObject1;
      Object localObject2;
      if ((paramEntityDamageEvent instanceof EntityDamageByEntityEvent))
      {
        localObject1 = (EntityDamageByEntityEvent)paramEntityDamageEvent;
        if ((((EntityDamageByEntityEvent)localObject1).getDamager() instanceof Player))
        {
          if (localLivingEntity.getHealth() - paramEntityDamageEvent.getDamage() <= 0)
          {
            localPlayer = (Player)((EntityDamageByEntityEvent)localObject1).getDamager();
            if (this.recent.get(paramEntityDamageEvent.getEntity()) == null)
              this.recent.put(paramEntityDamageEvent.getEntity(), localPlayer);
          }
        }
        else if (((((EntityDamageByEntityEvent)localObject1).getDamager() instanceof Wolf)) && (localLivingEntity.getHealth() - paramEntityDamageEvent.getDamage() <= 0))
        {
          localObject2 = new ecoUW((Wolf)((EntityDamageByEntityEvent)localObject1).getDamager());
          if (((ecoUW)localObject2).isTame())
          {
            localPlayer = this.srv.getPlayer(((ecoUW)localObject2).getOwner());
            if (this.recent.get(paramEntityDamageEvent.getEntity()) == null)
              this.recent.put(paramEntityDamageEvent.getEntity(), localPlayer);
          }
        }
      }
      else if ((paramEntityDamageEvent instanceof EntityDamageByProjectileEvent))
      {
        localObject1 = (EntityDamageByProjectileEvent)paramEntityDamageEvent;
        if ((((EntityDamageByProjectileEvent)localObject1).getDamager() instanceof Player))
        {
          localPlayer = (Player)((EntityDamageByProjectileEvent)localObject1).getDamager();
          localObject2 = (Player)paramEntityDamageEvent.getEntity();
          ((Player)localObject2).sendMessage(Double.toString(((Player)localObject2).getLocation().toVector().distance(localLivingEntity.getLocation().toVector())));
          if ((localLivingEntity.getHealth() - paramEntityDamageEvent.getDamage() <= 0) && (this.recent.get(paramEntityDamageEvent.getEntity()) == null))
            this.recent.put(paramEntityDamageEvent.getEntity(), localPlayer);
        }
      }
    }
  }

  public void onEntityDeath(EntityDeathEvent paramEntityDeathEvent)
  {
    if ((paramEntityDeathEvent.getEntity() instanceof Player))
      return;
    LivingEntity localLivingEntity = (LivingEntity)paramEntityDeathEvent.getEntity();
    Player localPlayer = (Player)this.recent.get(paramEntityDeathEvent.getEntity());
    if (localPlayer != null)
    {
      if (ecoConstants.OD)
        paramEntityDeathEvent.getDrops().clear();
      if ((!ecoConstants.BR) && (localPlayer.getItemInHand().getTypeId() == 261))
      {
        localPlayer.sendMessage(ecoConstants.MNB);
        this.recent.remove(paramEntityDeathEvent.getEntity());
      }
      else if ((!ecoConstants.AUSL) && (UnderSeaLevel(localPlayer)))
      {
        localPlayer.sendMessage(ecoConstants.MNB);
        this.recent.remove(paramEntityDeathEvent.getEntity());
      }
      else
      {
        if (((!ecoConstants.AC) && (FindSpawnersPlayer(localPlayer)) && (FindSpawnersCreature(localLivingEntity))) || ((!ecoConstants.AC) && (FindSpawnersPlayer(localPlayer))) || ((!ecoConstants.AC) && (FindSpawnersCreature(localLivingEntity))))
        {
          if (ecoConstants.CCD)
            paramEntityDeathEvent.getDrops().clear();
          if (ecoConstants.MS)
            localPlayer.sendMessage(ecoConstants.MNC);
        }
        else if (ecoCreature.Permissions.has(localPlayer, "ecoCreature.Creature." + localLivingEntity.getClass().getSimpleName()))
        {
          ecoCreature.getRewardHandler().CashRegistry(localPlayer, cIndex(localLivingEntity.getClass().getSimpleName()), Material.getMaterial(localPlayer.getItemInHand().getTypeId()).name());
        }
        for (int i = 0; i < ecoConstants.CD[cIndex(localLivingEntity.getClass().getSimpleName())].length; i++)
        {
          double d2 = Math.floor(Math.random() * 100.0D);
          Random localRandom = new Random();
          if (d2 >= ecoConstants.CD[cIndex(localLivingEntity.getClass().getSimpleName())][i][2])
            continue;
          int j = (int)ecoConstants.CD[cIndex(localLivingEntity.getClass().getSimpleName())][i][1];
          int k = 0;
          if (ecoConstants.FD)
            k = j;
          else
            k = localRandom.nextInt(j) + 1;
          ItemStack localItemStack = new ItemStack((int)ecoConstants.CD[cIndex(localLivingEntity.getClass().getSimpleName())][i][0], k, (short)0);
          if (localItemStack == null)
            continue;
          if (localItemStack.getAmount() == 0)
            localPlayer.sendMessage("This message should not appear, report to ecoCreature Dev!");
          else
            paramEntityDeathEvent.getDrops().addAll(Arrays.asList(new ItemStack[] { localItemStack }));
        }
        this.recent.remove(paramEntityDeathEvent.getEntity());
      }
    }
  }

  private static int cIndex(String paramString)
  {
    if (paramString.equals("CraftCreeper"))
      return 0;
    if (paramString.equals("CraftSkeleton"))
      return 1;
    if (paramString.equals("CraftZombie"))
      return 2;
    if (paramString.equals("CraftSpider"))
      return 3;
    if (paramString.equals("CraftPigZombie"))
      return 4;
    if (paramString.equals("CraftGhast"))
      return 5;
    if (paramString.equals("CraftSlime"))
      return 6;
    if (paramString.equals("CraftGiant"))
      return 7;
    if (paramString.equals("CraftChicken"))
      return 8;
    if (paramString.equals("CraftCow"))
      return 9;
    if (paramString.equals("CraftPig"))
      return 10;
    if (paramString.equals("CraftSheep"))
      return 11;
    if (paramString.equals("CraftSquid"))
      return 12;
    if (paramString.equals("CraftWolf"))
      return 13;
    if (paramString.equals("CraftMonster"))
      return 14;
    ecoCreature.logger.log(Level.INFO, "unknown cIndex " + paramString);
    return -1;
  }

  public boolean UnderSeaLevel(Player paramPlayer)
  {
    Location localLocation = paramPlayer.getLocation();
    return (int)localLocation.getY() < 63;
  }

  private static boolean isWithinRadius(Location paramLocation1, Location paramLocation2, double paramDouble)
  {
    return (paramLocation1.getX() - paramLocation2.getX()) * (paramLocation1.getX() - paramLocation2.getX()) + (paramLocation1.getY() - paramLocation2.getY()) * (paramLocation1.getY() - paramLocation2.getY()) + (paramLocation1.getZ() - paramLocation2.getZ()) * (paramLocation1.getZ() - paramLocation2.getZ()) <= paramDouble * paramDouble;
  }

  private static boolean isWithinRange(Location paramLocation1, Location paramLocation2, double paramDouble)
  {
    return true;
  }

  public double getDistance(Player paramPlayer, LivingEntity paramLivingEntity)
  {
    return paramPlayer.getLocation().toVector().distance(paramLivingEntity.getLocation().toVector());
  }

  public boolean FindSpawnersPlayer(Player paramPlayer)
  {
    Location localLocation = paramPlayer.getLocation();
    int i = (int)localLocation.getX();
    int j = (int)localLocation.getY();
    int k = (int)localLocation.getZ();
    World localWorld = paramPlayer.getWorld();
    int m = ecoConstants.CR;
    for (int n = 0 - m; n <= m; n++)
      for (int i1 = 0 - m; i1 <= m; i1++)
        for (int i2 = 0 - m; i2 <= m; i2++)
        {
          Block localBlock = localWorld.getBlockAt(i + n, j + i1, k + i2);
          if (localBlock.getTypeId() == 52)
            return true;
        }
    return false;
  }

  public boolean FindSpawnersCreature(LivingEntity paramLivingEntity)
  {
    Location localLocation = paramLivingEntity.getLocation();
    int i = (int)localLocation.getX();
    int j = (int)localLocation.getY();
    int k = (int)localLocation.getZ();
    World localWorld = paramLivingEntity.getWorld();
    int m = ecoConstants.CR;
    for (int n = 0 - m; n <= m; n++)
      for (int i1 = 0 - m; i1 <= m; i1++)
        for (int i2 = 0 - m; i2 <= m; i2++)
        {
          Block localBlock = localWorld.getBlockAt(i + n, j + i1, k + i2);
          if (localBlock.getTypeId() == 52)
            return true;
        }
    return false;
  }
}