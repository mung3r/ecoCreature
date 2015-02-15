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

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.DropConfigLoader;
import se.crafted.chrisb.ecoCreature.drops.AssembledDrop;
import se.crafted.chrisb.ecoCreature.drops.sources.DropConfig;
import se.crafted.chrisb.ecoCreature.events.DropEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class PlayerKilledEventMapper extends AbstractEventMapper
{
    public PlayerKilledEventMapper(DropConfigLoader dropConfigLoader)
    {
        super(dropConfigLoader);
    }

    @Override
    public boolean canMap(Event event)
    {
        return event instanceof PlayerKilledEvent;
    }

    @Override
    public Collection<DropEvent> mapEvent(Event event)
    {
        return canMap(event) ? createDropEvents((PlayerKilledEvent) event) : EMPTY_COLLECTION;
    }

    private Collection<DropEvent> createDropEvents(PlayerKilledEvent event)
    {
        Player killer = event.getKiller();
        Player victim = event.getVictim();
        DropConfig dropConfig = getDropConfig(killer.getWorld());
        Collection<AssembledDrop> drops = new ArrayList<>();
        Collection<AssembledDrop> penalties = new ArrayList<>();

        for (AssembledDrop killerDrop : createKillerDrops(event)) {
            drops.add(killerDrop);

            PlayerDeathEvent deathEvent = new PlayerDeathEvent(event.getEntity(), event.getDrops(), event.getDroppedExp(), event.getNewExp(),
                    event.getNewTotalExp(), event.getNewLevel(), event.getDeathMessage());
            for (AssembledDrop penalty : dropConfig.assembleDrops(deathEvent)) {
                penalty.setCoin(killerDrop.getCoin());
                penalty.setGain(-killerDrop.getGain());

                penalties.add(penalty);
            }
        }

        return Lists.newArrayList(new DropEvent(killer, drops), new DropEvent(victim, penalties));
    }

    private Collection<AssembledDrop> createKillerDrops(final PlayerKilledEvent event)
    {
        final DropConfig dropConfig = getDropConfig(event.getEntity().getWorld());

        Collection<AssembledDrop> drops = Collections2.transform(dropConfig.assembleDrops(event), new Function<AssembledDrop, AssembledDrop>() {

            @Override
            public AssembledDrop apply(AssembledDrop drop)
            {
                drop.addParameter(MessageToken.CREATURE, event.getVictim().getName());

                /*if ((dropConfig.isOverrideDrops() && drop.hasDrops()) || (dropConfig.isClearOnNoDrops() && !drop.hasDrops())) {
                    event.getDrops().clear();
                }*/

                if (drop.getEntityTypes().contains(EntityType.EXPERIENCE_ORB)) {
                    event.setDroppedExp(0);
                }

                addPlayerSkullToEvent(drop, event);

                return drop;
            }
        });

        return drops;
    }
}
