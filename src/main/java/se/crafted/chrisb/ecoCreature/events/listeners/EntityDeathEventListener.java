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
package se.crafted.chrisb.ecoCreature.events.listeners;

import java.util.Collection;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.EventUtils;
import se.crafted.chrisb.ecoCreature.events.DropEvent;
import se.crafted.chrisb.ecoCreature.events.EntityFarmedEvent;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class EntityDeathEventListener implements Listener
{
    private final ecoCreature plugin;

    public EntityDeathEventListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event instanceof PlayerDeathEvent) {
            return;
        }

        Collection<DropEvent> events = Collections.emptyList();

        if (EventUtils.isEntityKilledEvent(event)) {
            events = plugin.getDropEventFactory().collectDropEvents(EntityKilledEvent.createEvent(event));
        }
        else if (EventUtils.isEntityFarmed(event) || EventUtils.isEntityFireFarmed(event)) {
            events = plugin.getDropEventFactory().collectDropEvents(EntityFarmedEvent.createEvent(event));
        }

        for (DropEvent dropEvent : events) {
            Bukkit.getPluginManager().callEvent(dropEvent);
        }
    }
}
