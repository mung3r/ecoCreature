package se.crafted.chrisb.ecoCreature.commons;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

public class EntityUtils
{
    public static boolean isNearSpawner(Entity entity, int radius)
    {
        Location loc = entity.getLocation();
        BlockState[] tileEntities = entity.getLocation().getChunk().getTileEntities();
        int r = radius;
        r *= r;
        for (BlockState state : tileEntities) {
            if (state instanceof CreatureSpawner && state.getBlock().getLocation().distanceSquared(loc) <= r) {
                return true;
            }
        }
        return false;
    }

    public static boolean isUnderSeaLevel(Entity entity)
    {
        return entity != null && (entity.getLocation().getBlockY() < entity.getWorld().getSeaLevel());
    }

    public static boolean isOwner(Player player, Entity entity)
    {
        if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;
            if (tameable.isTamed() && tameable.getOwner() instanceof Player) {
                Player owner = (Player) tameable.getOwner();
                if (owner.getName().equals(player.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
