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
package se.crafted.chrisb.ecoCreature.drops.rules;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public abstract class AbstractEntityRule extends AbstractRule
{
    @Override
    public boolean isBroken(Event event)
    {
        return event instanceof EntityKilledEvent && isBroken((EntityKilledEvent) event);
    }

    protected abstract boolean isBroken(EntityKilledEvent event);

    @Override
    public void enforce(Event event)
    {
        if (event instanceof EntityKilledEvent) {
            if (isClearDrops()) {
                ((EntityKilledEvent) event).getDrops().clear();
            }
            if (isClearDrops() || isClearExpOrbs()) {
                ((EntityKilledEvent) event).setDroppedExp(0);
            }
        }

        super.enforce(event);
    }

    @Override
    public Player getKiller(Event event)
    {
        Player player = null;

        if (event instanceof EntityKilledEvent) {
            player = ((EntityKilledEvent) event).getKiller();
        }

        return player;
    }
}
