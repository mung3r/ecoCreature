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
package se.crafted.chrisb.ecoCreature.drops;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomEntityType;

public class CustomEntityDrop extends AbstractDrop
{
    private Collection<CustomEntityType> customEntityTypes;

    public CustomEntityDrop(String name, Location location)
    {
        super(name, location);

        customEntityTypes = new ArrayList<>();
    }

    public Collection<CustomEntityType> getCustomEntityTypes()
    {
        return customEntityTypes;
    }

    public void setCustomEntityTypes(Collection<CustomEntityType> customEntityTypes)
    {
        this.customEntityTypes = customEntityTypes;
    }

    @Override
    public void deliver(Player player)
    {
        for (CustomEntityType customType : getCustomEntityTypes()) {
            Entity entity = getWorld().spawn(getLocation(), customType.getType().getEntityClass());
            switch (customType) {
                case ANGRY_WOLF:
                    ((Wolf) entity).setAngry(true);
                    break;
                case KILLER_RABBIT:
                    ((Rabbit) entity).setRabbitType(Type.THE_KILLER_BUNNY);
                    break;
                case POWERED_CREEPER:
                    ((Creeper) entity).setPowered(true);
                    break;
                case ZOMBIE_BABY:
                    ((Zombie) entity).setBaby(true);
                    break;
                case ZOMBIE_VILLAGER:
                    ((Zombie) entity).setVillager(true);
                    break;
                default:
            }
        }
    }
}
