/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.RewardEvent;

public class PluginEventHandler
{
    private List<RewardEventCreator> handlers;

    public PluginEventHandler()
    {
        handlers = new ArrayList<RewardEventCreator>();
    }

    public void add(RewardEventCreator handler)
    {
        handlers.add(handler);
    }

    public Set<RewardEvent> createRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        for (RewardEventCreator handler : handlers) {
            if (handler.canCreateRewardEvents(event)) {
                events = new HashSet<RewardEvent>();
                events.addAll(handler.createRewardEvents(event));
                break;
            }
        }

        return events;
    }
}
