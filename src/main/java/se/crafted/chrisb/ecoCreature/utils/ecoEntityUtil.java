package se.crafted.chrisb.ecoCreature.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Monster;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoEntityUtil
{
    private static final double SEA_LEVEL = 63;

    private static final long DAY_START = 0;
    private static final long SUNSET_START = 13000;
    private static final long DUSK_START = 13500;
    private static final long NIGHT_START = 14000;
    private static final long DAWN_START = 22000;
    private static final long SUNRISE_START = 23000;

    public static enum TimePeriod {
        DAY, SUNSET, DUSK, NIGHT, DAWN, SUNRISE, IDENTITY;

        private static final Map<String, TimePeriod> nameMap = new HashMap<String, TimePeriod>();

        static {
            for (TimePeriod type : TimePeriod.values()) {
                nameMap.put(type.toString(), type);
            }
        }

        public static TimePeriod fromName(String period)
        {
            return nameMap.get(period.toUpperCase());
        }
    };

    public static Boolean isUnderSeaLevel(Entity entity)
    {
        return entity.getLocation().getY() < SEA_LEVEL;
    }

    public static boolean isNearSpawner(Entity entity)
    {
        Location loc = entity.getLocation();
        BlockState[] tileEntities = entity.getLocation().getChunk().getTileEntities();
        int r = ecoCreature.getRewardManager(entity).campRadius;
        r *= r;
        for (BlockState state : tileEntities) {
            if (state instanceof CreatureSpawner && state.getBlock().getLocation().distanceSquared(loc) <= r) {
                return true;
            }
        }
        return false;
    }

    public static CreatureType getCreatureType(Entity entity)
    {
        if (entity instanceof Blaze)
            return CreatureType.BLAZE;
        if (entity instanceof CaveSpider)
            return CreatureType.CAVE_SPIDER;
        if (entity instanceof Chicken)
            return CreatureType.CHICKEN;
        if (entity instanceof Cow)
            return CreatureType.COW;
        if (entity instanceof Creeper)
            return CreatureType.CREEPER;
        if (entity instanceof EnderDragon)
            return CreatureType.ENDER_DRAGON;
        if (entity instanceof Enderman)
            return CreatureType.ENDERMAN;
        if (entity instanceof Ghast)
            return CreatureType.GHAST;
        if (entity instanceof Giant)
            return CreatureType.GIANT;
        if (entity instanceof MagmaCube)
            return CreatureType.MAGMA_CUBE;
        if (entity instanceof MushroomCow)
            return CreatureType.MUSHROOM_COW;
        if (entity instanceof Pig)
            return CreatureType.PIG;
        if (entity instanceof PigZombie)
            return CreatureType.PIG_ZOMBIE;
        if (entity instanceof Sheep)
            return CreatureType.SHEEP;
        if (entity instanceof Skeleton)
            return CreatureType.SKELETON;
        if (entity instanceof Slime)
            return CreatureType.SLIME;
        if (entity instanceof Snowman)
            return CreatureType.SNOWMAN;
        if (entity instanceof Silverfish)
            return CreatureType.SILVERFISH;
        if (entity instanceof Spider)
            return CreatureType.SPIDER;
        if (entity instanceof Squid)
            return CreatureType.SQUID;
        if (entity instanceof Villager)
            return CreatureType.VILLAGER;
        if (entity instanceof Zombie)
            return CreatureType.ZOMBIE;
        if (entity instanceof Wolf)
            return CreatureType.WOLF;

        // Monster is a parent class and needs to be last
        if (entity instanceof Monster)
            return CreatureType.MONSTER;
        return null;
    }

    public static TimePeriod getTimePeriod(Entity entity)
    {
        long time = entity.getWorld().getTime();

        if (time >= DAY_START && time < SUNSET_START)
            return TimePeriod.DAY;
        else if (time >= SUNSET_START && time < DUSK_START)
            return TimePeriod.SUNSET;
        else if (time >= DUSK_START && time < NIGHT_START)
            return TimePeriod.DUSK;
        else if (time >= NIGHT_START && time < DAWN_START)
            return TimePeriod.NIGHT;
        else if (time >= DAWN_START && time < SUNRISE_START)
            return TimePeriod.DAWN;
        else if (time >= SUNRISE_START && time < DAY_START)
            return TimePeriod.SUNRISE;

        return TimePeriod.IDENTITY;
    }

    public static Player getKillerFromDeathEvent(EntityDeathEvent event)
    {
        if (event != null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

            Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();

            if (damager instanceof Player) {
                return (Player) damager;
            }
            else if (damager instanceof Tameable) {
                if (((Tameable) damager).isTamed() && ((Tameable) damager).getOwner() instanceof Player) {
                    return (Player) ((Tameable) damager).getOwner();
                }
            }
            else if (damager instanceof Projectile) {
                if (((Projectile) damager).getShooter() instanceof Player) {
                    return (Player) ((Projectile) damager).getShooter();
                }
            }
        }

        return null;
    }

    public static LivingEntity getTamedKillerFromDeathEvent(EntityDeathEvent event)
    {
        if (event != null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

            Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();

            if (damager instanceof Tameable) {
                if (((Tameable) damager).isTamed() && ((Tameable) damager).getOwner() instanceof Player) {
                    return (LivingEntity) damager;
                }
            }
        }

        return null;
    }

    public static Boolean isPVPDeath(EntityDeathEvent event)
    {
        if (event != null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
            return damager instanceof Player;
        }

        return false;
    }

    public static Boolean isOwner(Player player, Entity entity)
    {
        if (entity instanceof Tameable) {
            if (((Tameable) entity).isTamed() && ((Tameable) entity).getOwner() instanceof Player) {
                Player owner = (Player) ((Tameable) entity).getOwner();
                if (owner.getName().equals(player.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
