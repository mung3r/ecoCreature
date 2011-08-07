package se.crafted.chrisb.ecoCreature.entities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftWolf;
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

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.utils.ecoConstants;

public class ecoEntityListener extends EntityListener
{
    private final ecoCreature plugin;
    private Map<Entity, Player> recent = new HashMap<Entity, Player>();

    public ecoEntityListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent entityDamageEvent)
    {
        if (entityDamageEvent.isCancelled())
            return;

        if (entityDamageEvent.getEntity() instanceof Player)
            return;

        if (entityDamageEvent.getEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entityDamageEvent.getEntity();
            if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) entityDamageEvent;
                if (damageByEntityEvent.getDamager() instanceof Player) {
                    if (livingEntity.getHealth() - entityDamageEvent.getDamage() <= 0) {
                        if (this.recent.get(entityDamageEvent.getEntity()) == null)
                            this.recent.put(entityDamageEvent.getEntity(), (Player) damageByEntityEvent.getDamager());
                    }
                }
                else if (((damageByEntityEvent.getDamager() instanceof Wolf)) && (livingEntity.getHealth() - entityDamageEvent.getDamage() <= 0)) {
                    CraftWolf wolf = (CraftWolf) damageByEntityEvent.getDamager();
                    if (wolf.isTamed()) {
                        if (this.recent.get(entityDamageEvent.getEntity()) == null)
                            this.recent.put(entityDamageEvent.getEntity(), plugin.getServer().getPlayer(wolf.getHandle().getOwnerName()));
                    }
                }
            }
            else if ((entityDamageEvent instanceof EntityDamageByProjectileEvent)) {
                EntityDamageByProjectileEvent damageByProjectileEvent = (EntityDamageByProjectileEvent) entityDamageEvent;
                if (damageByProjectileEvent.getDamager() instanceof Player) {
                    Player player = (Player) entityDamageEvent.getEntity();
                    player.sendMessage(Double.toString(player.getLocation().toVector().distance(livingEntity.getLocation().toVector())));
                    if ((livingEntity.getHealth() - entityDamageEvent.getDamage() <= 0) && (this.recent.get(entityDamageEvent.getEntity()) == null))
                        this.recent.put(entityDamageEvent.getEntity(), (Player) damageByProjectileEvent.getDamager());
                }
            }
        }
    }

    @Override
    public void onEntityDeath(EntityDeathEvent entityDeathEvent)
    {
        if (entityDeathEvent.getEntity() instanceof Player)
            return;

        LivingEntity livingEntity = (LivingEntity) entityDeathEvent.getEntity();
        Player player = (Player) this.recent.get(entityDeathEvent.getEntity());

        if (player != null) {
            if (ecoConstants.shouldOverrideDrops)
                entityDeathEvent.getDrops().clear();
            if ((!ecoConstants.hasBowRewards) && (player.getItemInHand().getTypeId() == Material.BOW.getId())) {
                player.sendMessage(ecoConstants.shouldOuputNoBowMessage);
                this.recent.remove(entityDeathEvent.getEntity());
            }
            else if ((!ecoConstants.shouldAllowUnderSeaLVL) && (isUnderSeaLevel(player))) {
                player.sendMessage(ecoConstants.shouldOuputNoBowMessage);
                this.recent.remove(entityDeathEvent.getEntity());
            }
            else {
                if (((!ecoConstants.shouldAllowCamping) && (isNearSpawner(player)) && (isNearSpawner(livingEntity)))
                        || ((!ecoConstants.shouldAllowCamping) && (isNearSpawner(player)))
                        || ((!ecoConstants.shouldAllowCamping) && (isNearSpawner(livingEntity)))) {
                    if (ecoConstants.shouldClearCampDrops)
                        entityDeathEvent.getDrops().clear();
                    if (ecoConstants.shouldOutputSpawnerMessage)
                        player.sendMessage(ecoConstants.noCampMessage);
                }
                else if (ecoCreature.permissionsHandler.has(player, "ecoCreature.Creature." + livingEntity.getClass().getSimpleName())) {
                    plugin.getRewardHandler().registerReward(player, cIndex(livingEntity.getClass().getSimpleName()), Material.getMaterial(player.getItemInHand().getTypeId()).name());
                }
                for (int i = 0; i < ecoConstants.CreatureDrop[cIndex(livingEntity.getClass().getSimpleName())].length; i++) {
                    double d2 = Math.floor(Math.random() * 100.0D);
                    Random random = new Random();
                    if (d2 >= ecoConstants.CreatureDrop[cIndex(livingEntity.getClass().getSimpleName())][i][2])
                        continue;
                    int j = (int) ecoConstants.CreatureDrop[cIndex(livingEntity.getClass().getSimpleName())][i][1];
                    int k = 0;
                    if (ecoConstants.isFixedDrops)
                        k = j;
                    else
                        k = random.nextInt(j) + 1;
                    ItemStack itemStack = new ItemStack((int) ecoConstants.CreatureDrop[cIndex(livingEntity.getClass().getSimpleName())][i][0], k, (short) 0);
                    if (itemStack != null) {
                        if (itemStack.getAmount() == 0)
                            ecoCreature.logger.log(Level.SEVERE, "[ecoCreature] Item stack amount is zero.");
                        else
                            entityDeathEvent.getDrops().addAll(Arrays.asList(new ItemStack[] { itemStack }));
                    }
                }
                this.recent.remove(entityDeathEvent.getEntity());
            }
        }
    }

    private static int cIndex(String name)
    {
        if (name.equals("CraftCreeper"))
            return 0;
        if (name.equals("CraftSkeleton"))
            return 1;
        if (name.equals("CraftZombie"))
            return 2;
        if (name.equals("CraftSpider"))
            return 3;
        if (name.equals("CraftPigZombie"))
            return 4;
        if (name.equals("CraftGhast"))
            return 5;
        if (name.equals("CraftSlime"))
            return 6;
        if (name.equals("CraftGiant"))
            return 7;
        if (name.equals("CraftChicken"))
            return 8;
        if (name.equals("CraftCow"))
            return 9;
        if (name.equals("CraftPig"))
            return 10;
        if (name.equals("CraftSheep"))
            return 11;
        if (name.equals("CraftSquid"))
            return 12;
        if (name.equals("CraftWolf"))
            return 13;
        if (name.equals("CraftMonster"))
            return 14;
        ecoCreature.logger.log(Level.SEVERE, "Unknown cIndex: " + name);
        return -1;
    }

    private static boolean isUnderSeaLevel(Player player)
    {
        Location location = player.getLocation();
        return (int) location.getY() < 63;
    }

    private static boolean isNearSpawner(LivingEntity livingEntity)
    {
        Location location = livingEntity.getLocation();
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();
        int r = ecoConstants.campRadius;
        World world = livingEntity.getWorld();

        for (int i = 0 - r; i <= r; i++) {
            for (int j = 0 - r; j <= r; j++) {
                for (int k = 0 - r; k <= r; k++) {
                    Block block = world.getBlockAt(x + i, y + j, z + k);
                    if ((block.getTypeId() == Material.MOB_SPAWNER.getId()) && (block.getLocation().distance(location) < r))
                        return true;
                }
            }
        }
        return false;
    }
}