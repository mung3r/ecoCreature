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
package se.crafted.chrisb.ecoCreature.events.mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.DropEvent;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class DropEventFactory
{
    private List<EventMapper> mappers;

    public DropEventFactory()
    {
        mappers = new ArrayList<EventMapper>();
    }

    public void addMapper(EventMapper mapper)
    {
        mappers.add(mapper);
    }

    public Collection<DropEvent> createEvents(final Event event)
    {
        Collection<DropEvent> emptySet = Collections.emptySet();

        EventMapper mapper = Iterables.find(mappers, new Predicate<EventMapper>() {
            @Override
            public boolean apply(EventMapper mapper) {
                return mapper.canMap(event);
            }
        });

        return mapper != null ? mapper.mapEvent(event) : emptySet;
    }
}
