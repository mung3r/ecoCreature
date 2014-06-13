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
package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

public class EntityRewardSource extends AbstractRewardSource
{
    public EntityRewardSource(String section, ConfigurationSection config)
    {
        super(section, config);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            return ((PlayerKilledEvent) event).getEntity().getLocation();
        }
        else if (event instanceof EntityKilledEvent) {
            return ((EntityKilledEvent) event).getEntity().getLocation();
        }
        else {
            throw new IllegalArgumentException("Unrecognized event");
        }
    }
}
