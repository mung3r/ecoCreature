package se.crafted.chrisb.ecoCreature.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import se.crafted.chrisb.ecoCreature.managers.ecoRewardManager;

public class ecoEntityUtil
{
    private static final double SEA_LEVEL = 63;

    public static Boolean isUnderSeaLevel(Entity entity)
    {
        return entity.getLocation().getY() < SEA_LEVEL;
    }

    public static boolean isNearSpawner(Entity entity)
    {
        Location location = entity.getLocation();
        int r = ecoRewardManager.campRadius;

        for (int i = 0 - r; i <= r; i++) {
            for (int j = 0 - r; j <= r; j++) {
                for (int k = 0 - r; k <= r; k++) {
                    if (location.getBlock().getRelative(i, j, k).getType().equals(Material.MOB_SPAWNER))
                        return true;
                }
            }
        }
        return false;
    }

    public static CreatureType getCreatureType(Entity entity)
    {
        if (entity instanceof Chicken)
            return CreatureType.CHICKEN;
        if (entity instanceof Cow)
            return CreatureType.COW;
        if (entity instanceof Creeper)
            return CreatureType.CREEPER;
        if (entity instanceof Ghast)
            return CreatureType.GHAST;
        if (entity instanceof Giant)
            return CreatureType.GIANT;
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
        if (entity instanceof Spider)
            return CreatureType.SPIDER;
        if (entity instanceof Squid)
            return CreatureType.SQUID;
        if (entity instanceof Zombie)
            return CreatureType.ZOMBIE;
        if (entity instanceof Wolf)
            return CreatureType.WOLF;

        // Monster is a parent class and needs to be last
        if (entity instanceof Monster)
            return CreatureType.MONSTER;
        return null;
    }
}
