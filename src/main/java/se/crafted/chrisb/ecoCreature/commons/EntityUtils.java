/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
 * ecoCreature is licensed under the GNU Lesser General Public License.
 *
 * ecoCreature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ecoCreature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.crafted.chrisb.ecoCreature.commons;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

public final class EntityUtils
{
    private EntityUtils()
    {
    }

    public static boolean isNearSpawner(Entity entity, int radius)
    {
        Location loc = entity.getLocation();
        BlockState[] tileEntities = entity.getLocation().getChunk().getTileEntities();
        int r = radius * radius;

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

    public static String getItemNameInHand(Player player)
    {
        return getItemTypeInHand(player).name();
    }

    public static Material getItemTypeInHand(Player player)
    {
        return player.getItemInHand().getType();
    }
}
