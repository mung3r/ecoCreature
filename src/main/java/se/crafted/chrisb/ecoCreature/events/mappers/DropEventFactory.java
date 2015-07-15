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
package se.crafted.chrisb.ecoCreature.events.mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.DropConfigLoader;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.events.DropEvent;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class DropEventFactory
{
    private final List<EventMapper> mappers;

    public DropEventFactory(DropConfigLoader dropConfigLoader)
    {
        mappers = new ArrayList<>();

        mappers.add(new BlockEventMapper(dropConfigLoader));
        mappers.add(new PlayerKilledEventMapper(dropConfigLoader));
        mappers.add(new PlayerDeathEventMapper(dropConfigLoader));
        mappers.add(new EntityKilledEventMapper(dropConfigLoader));
        mappers.add(new EntityFarmedEventMapper(dropConfigLoader));

        if (DependencyUtils.hasDeathTpPlus()) {
            mappers.add(new KillStreakEventMapper(dropConfigLoader));
            mappers.add(new DeathStreakEventMapper(dropConfigLoader));
        }

        if (DependencyUtils.hasHeroes()) {
            mappers.add(new HeroesEventMapper(dropConfigLoader));
        }

        if (DependencyUtils.hasMcMMO()) {
            mappers.add(new McMMOEventMapper(dropConfigLoader));
        }
    }

    public Collection<DropEvent> collectDropEvents(final Event event)
    {
        Collection<DropEvent> emptyList = Collections.emptyList();

        EventMapper mapper = Iterables.find(mappers, new Predicate<EventMapper>() {
            @Override
            public boolean apply(EventMapper mapper) {
                return mapper.canMap(event);
            }
        });

        return mapper != null ? mapper.mapEvent(event) : emptyList;
    }
}
