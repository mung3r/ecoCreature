package se.crafted.chrisb.ecoCreature.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoEntityUtil
{
    private static final long DAY_START = 0;
    private static final long SUNSET_START = 13000;
    private static final long DUSK_START = 13500;
    private static final long NIGHT_START = 14000;
    private static final long DAWN_START = 22000;
    private static final long SUNRISE_START = 23000;

    private static Set<Integer> spawnerMobs = new HashSet<Integer>();

    public static enum TimePeriod {
        DAY, SUNSET, DUSK, NIGHT, DAWN, SUNRISE, IDENTITY;

        private static final Map<String, TimePeriod> NAME_MAP = new HashMap<String, TimePeriod>();

        static {
            for (TimePeriod type : TimePeriod.values()) {
                NAME_MAP.put(type.toString(), type);
            }
        }

        public static TimePeriod fromName(String period)
        {
            return NAME_MAP.get(period.toUpperCase());
        }
    };

    public static boolean isUnderSeaLevel(Entity entity)
    {
        return entity.getLocation().getBlockY() < entity.getWorld().getSeaLevel();
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

    public static boolean isSpawnerMob(Entity entity)
    {
        return spawnerMobs.remove(Integer.valueOf(entity.getEntityId()));
    }

    public static void setSpawnerMob(Entity entity)
    {
        // Only add to the array if we're tracking by entity. Avoids a memory leak.
        if (!ecoCreature.getRewardManager(entity).canCampSpawner && ecoCreature.getRewardManager(entity).campByEntity) {
            spawnerMobs.add(Integer.valueOf(entity.getEntityId()));
        }
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

    public static boolean isPVPDeath(EntityDeathEvent event)
    {
        if (event != null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            return ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Player;
        }

        return false;
    }

    public static boolean isOwner(Player player, Entity entity)
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
