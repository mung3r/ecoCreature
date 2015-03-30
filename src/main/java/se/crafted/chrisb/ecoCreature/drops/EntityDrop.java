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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

public class EntityDrop extends AbstractDrop
{
    private Collection<EntityType> entityTypes;

    public EntityDrop(String name, Location location)
    {
        super(name, location);

        entityTypes = new ArrayList<>();
    }

    public Collection<EntityType> getEntityTypes()
    {
        return entityTypes;
    }

    public void setEntityTypes(Collection<EntityType> entityTypes)
    {
        this.entityTypes = entityTypes;
    }

    @Override
    public boolean deliver(Player player)
    {
        boolean success = false;

        for (EntityType type : getEntityTypes()) {
            Entity entity = getWorld().spawn(getLocation(), type.getEntityClass());
            if (entity instanceof ExperienceOrb) {
                ((ExperienceOrb) entity).setExperience(1);
            }
            success = true;
        }

        return success;
    }
}
