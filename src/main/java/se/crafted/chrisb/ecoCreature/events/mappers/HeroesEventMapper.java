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

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.DropConfigLoader;
import se.crafted.chrisb.ecoCreature.drops.sources.DropConfig;
import se.crafted.chrisb.ecoCreature.events.DropEvent;

import com.google.common.collect.Lists;
import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

public class HeroesEventMapper extends AbstractEventMapper
{
    public HeroesEventMapper(DropConfigLoader dropConfigLoader)
    {
        super(dropConfigLoader);
    }

    @Override
    public boolean canMap(Event event)
    {
        return event instanceof HeroChangeLevelEvent;
    }

    @Override
    public Collection<DropEvent> mapEvent(Event event)
    {
        return canMap(event) ? createDropEvents((HeroChangeLevelEvent) event) : EMPTY_COLLECTION;
    }

    private Collection<DropEvent> createDropEvents(HeroChangeLevelEvent event)
    {
        Player player = event.getHero().getPlayer();
        DropConfig dropConfig = getDropConfig(player.getWorld());

        return Lists.newArrayList(new DropEvent(player, dropConfig.assembleDrops(event)));
    }
}
