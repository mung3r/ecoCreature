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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.events.DropEvent;
import se.crafted.chrisb.ecoCreature.metrics.DropMetrics;

public class DropEventListener implements Listener
{
    private final DropMetrics dropMetrics;

    public DropEventListener(DropMetrics dropMetrics)
    {
        this.dropMetrics = dropMetrics;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDropEvent(DropEvent event)
    {
        if (!event.isCancelled()) {
            Collection<AbstractDrop> drops = event.getDrops();
            Player player = event.getPlayer();

            for (AbstractDrop drop : drops) {
                if (drop.deliver(player)) {
                    dropMetrics.addCount(drop.getName());
                }
            }
        }
    }
}
