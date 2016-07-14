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

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.DropConfigLoader;
import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.CoinDrop;
import se.crafted.chrisb.ecoCreature.drops.EntityDrop;
import se.crafted.chrisb.ecoCreature.drops.ItemDrop;
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
        Collection<AbstractDrop> drops = new ArrayList<>();
        Collection<AbstractDrop> penalties = new ArrayList<>();

        for (AbstractDrop killerDrop : createKillerDrops(event)) {
            drops.add(killerDrop);

            PlayerDeathEvent deathEvent = new PlayerDeathEvent(event.getEntity(), event.getDrops(), event.getDroppedExp(), event.getNewExp(),
                    event.getNewTotalExp(), event.getNewLevel(), event.getDeathMessage());
            for (AbstractDrop penalty : dropConfig.collectDrops(deathEvent)) {
                if (killerDrop instanceof CoinDrop && penalty instanceof CoinDrop) {
                    ((CoinDrop) penalty).setCoin(((CoinDrop) killerDrop).getCoin());
                    ((CoinDrop) penalty).setGain(-((CoinDrop) killerDrop).getGain());
                }

                penalties.add(penalty);
            }
        }

        return Lists.newArrayList(new DropEvent(killer, drops, event.getClass()), new DropEvent(victim, penalties, event.getClass()));
    }

    private Collection<AbstractDrop> createKillerDrops(final PlayerKilledEvent event)
    {
        final DropConfig dropConfig = getDropConfig(event.getEntity().getWorld());

        Collection<AbstractDrop> drops = Collections2.transform(dropConfig.collectDrops(event), new Function<AbstractDrop, AbstractDrop>() {

            @Override
            public AbstractDrop apply(AbstractDrop drop)
            {
                drop.addParameter(MessageToken.CREATURE, event.getVictim().getName());

                if (drop instanceof ItemDrop) {
                    ItemDrop itemDrop = (ItemDrop) drop;
                    if (dropConfig.isOverrideDrops() && !itemDrop.getItems().isEmpty() || dropConfig.isClearOnNoDrops() && itemDrop.getItems().isEmpty()) {
                        event.getDrops().clear();
                    }

                    addPlayerSkullToEvent(itemDrop, event);
                }

                if (drop instanceof EntityDrop) {
                    EntityDrop entityDrop = (EntityDrop) drop;
                    if (entityDrop.getEntityTypes().contains(EntityType.EXPERIENCE_ORB)) {
                        event.resetDroppedExp();
                    }
                }

                return drop;
            }
        });

        return drops;
    }
}
