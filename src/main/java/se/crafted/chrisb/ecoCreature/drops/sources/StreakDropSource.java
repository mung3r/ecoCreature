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
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.drops.categories.DropSourceFactory;
import se.crafted.chrisb.ecoCreature.drops.categories.types.StreakDropType;

public class StreakDropSource extends AbstractDropSource
{
    public StreakDropSource(String section, ConfigurationSection config)
    {
        super(section, config);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (DependencyUtils.hasDeathTpPlus() && event instanceof DeathStreakEvent) {
            return ((DeathStreakEvent) event).getPlayer().getLocation();
        }
        else if (DependencyUtils.hasDeathTpPlus() && event instanceof KillStreakEvent) {
            return ((KillStreakEvent) event).getPlayer().getLocation();
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    public static AbstractDropSource createDropSource(String section, ConfigurationSection config)
    {
        AbstractDropSource source;

        switch (StreakDropType.fromName(DropSourceFactory.parseTypeName(section))) {
            case DEATH_STREAK:
            case KILL_STREAK:
                source = new StreakDropSource(section, config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + section);
        }
        return source;
    }
}
