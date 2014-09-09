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

import java.util.Collection;

import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.DropEvent;
import se.crafted.chrisb.ecoCreature.events.EntityFarmedEvent;
import se.crafted.chrisb.ecoCreature.drops.DropConfig;

public class EntityFarmedEventMapper extends AbstractEventMapper
{
    public EntityFarmedEventMapper(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean canMap(Event event)
    {
        return event instanceof EntityFarmedEvent;
    }

    @Override
    public Collection<DropEvent> mapEvent(Event event)
    {
        if (event instanceof EntityFarmedEvent) {
            handleNoFarm((EntityFarmedEvent) event);
        }

        return EMPTY_COLLECTION;
    }

    private void handleNoFarm(EntityFarmedEvent event)
    {
        DropConfig dropConfig = getDropConfig(event.getEntity().getWorld());

        if (dropConfig.isNoFarm() && event.isFarmed()) {
            LoggerUtil.getInstance().debug("Mob farming detected");
            deleteDrops(event);
        }

        if (dropConfig.isNoFarmFire() && event.isFireFarmed()) {
            LoggerUtil.getInstance().debug("Mob farming by fire detected");
            deleteDrops(event);
        }
    }

    private void deleteDrops(EntityFarmedEvent event)
    {
        event.getDrops().clear();
        event.setDroppedExp(0);
    }
}
