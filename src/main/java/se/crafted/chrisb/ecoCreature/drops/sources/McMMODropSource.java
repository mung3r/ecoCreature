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

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.drops.categories.types.McMMODropType;

public class McMMODropSource extends AbstractDropSource
{
    public McMMODropSource(String section, ConfigurationSection config)
    {
        super(section, config);
        setNoCoinRewardMessage(DefaultMessage.EMPTY_MESSAGE);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (DependencyUtils.hasMcMMO() && event instanceof McMMOPlayerLevelUpEvent) {
            return ((McMMOPlayerLevelUpEvent) event).getPlayer().getLocation();
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    public static AbstractDropSource createDropSource(String section, ConfigurationSection config)
    {
        AbstractDropSource source;

        switch (McMMODropType.fromName(DropSourceFactory.parseTypeName(section))) {
            case MCMMO_LEVELED:
                source = new McMMODropSource(section, config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + section);
        }
        return source;
    }
}
