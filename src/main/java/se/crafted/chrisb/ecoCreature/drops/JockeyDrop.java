/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
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

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class JockeyDrop extends EntityDrop
{
    public JockeyDrop(String name, Location location)
    {
        super(name, location);
    }

    @Override
    public boolean deliver(Player player)
    {
        boolean success = false;

        Iterator<EntityType> typeIterator = getEntityTypes().iterator();
        while (typeIterator.hasNext()) {
            EntityType vehicleType = typeIterator.next();
            Entity vehicle = getWorld().spawn(getLocation(), vehicleType.getEntityClass());
            EntityType passengerType = typeIterator.next();
            Entity passenger = getWorld().spawn(getLocation(), passengerType.getEntityClass());
            vehicle.setPassenger(passenger);
            success = true;
        }

        return success;
    }
}
