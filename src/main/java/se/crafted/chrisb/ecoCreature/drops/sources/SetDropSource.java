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
package se.crafted.chrisb.ecoCreature.drops.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

public class SetDropSource extends AbstractDropSource
{
    public SetDropSource(String section, ConfigurationSection config)
    {
        super(section, config);
    }

    @Override
    protected Location getLocation(Event event)
    {
        Location location = null;

        if (event instanceof EntityKilledEvent) {
            location = ((EntityKilledEvent) event).getEntity().getLocation();
        }
        else if (event instanceof PlayerKilledEvent) {
            location = ((PlayerKilledEvent) event).getEntity().getLocation();
        }
        else if (event instanceof PlayerDeathEvent) {
            location = ((PlayerDeathEvent) event).getEntity().getLocation();
        }
        else if (event instanceof BlockBreakEvent) {
            location = ((BlockBreakEvent) event).getBlock().getLocation();
        }

        return location;
    }
}
