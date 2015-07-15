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

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.DropEvent;

public class StreakEventListener implements Listener
{
    private final ecoCreature plugin;

    public StreakEventListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathStreakEvent(DeathStreakEvent event)
    {
        for (DropEvent dropEvent : plugin.getDropEventFactory().collectDropEvents(event)) {
            Bukkit.getPluginManager().callEvent(dropEvent);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKillStreakEvent(KillStreakEvent event)
    {
        for (DropEvent dropEvent : plugin.getDropEventFactory().collectDropEvents(event)) {
            Bukkit.getPluginManager().callEvent(dropEvent);
        }
    }
}
