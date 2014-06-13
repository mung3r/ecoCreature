/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2014, R. Ramos <http://github.com/mung3r/>
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

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.metadata.MetadataValue;

import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public final class EntityUtils
{
    private EntityUtils()
    {
    }

    public static boolean isNearSpawner(Entity entity, int radius)
    {
        Location loc = entity.getLocation();
        int r = radius * radius;
        List<MetadataValue> metaDataValues = entity.getMetadata(WorldSettings.SPAWNERLOC_TAG_MDID);

        if (!metaDataValues.isEmpty()) {
            MetadataValue metaDataValue = metaDataValues.get(0);

            if (metaDataValue.value() instanceof Location) {
                Location spawnerLoc = (Location) metaDataValue.value();

                if (spawnerLoc.distanceSquared(loc) <= r) {
                    return true;
                }
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
