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
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import se.crafted.chrisb.ecoCreature.DropConfigLoader;
import se.crafted.chrisb.ecoCreature.drops.AssembledDrop;
import se.crafted.chrisb.ecoCreature.drops.sources.DropConfig;
import se.crafted.chrisb.ecoCreature.events.DropEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public abstract class AbstractEventMapper implements EventMapper
{
    protected static final Collection<DropEvent> EMPTY_COLLECTION = Collections.emptyList();

    private DropConfigLoader dropConfigLoader;

    public AbstractEventMapper(DropConfigLoader dropConfigLoader)
    {
        this.dropConfigLoader = dropConfigLoader;
    }

    @Override
    public abstract boolean canMap(Event event);

    @Override
    public abstract Collection<DropEvent> mapEvent(Event event);

    @Override
    public DropConfig getDropConfig(World world)
    {
        return getDropConfigLoader().getDropConfig(world);
    }

    protected DropConfigLoader getDropConfigLoader()
    {
        return dropConfigLoader;
    }

    protected static void addPlayerSkullToEvent(AssembledDrop drop, Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            PlayerKilledEvent playerKilledEvent = (PlayerKilledEvent) event;

            boolean hasSkull = Iterables.removeIf(drop.getItemDrops(), new Predicate<ItemStack>() {

                @Override
                public boolean apply(ItemStack itemStack)
                {
                    return itemStack != null && itemStack.getType() == Material.SKULL_ITEM;
                }
            });

            if (hasSkull) {
                playerKilledEvent.getDrops().add(createSkullItem(playerKilledEvent.getVictim().getName()));
            }
        }
    }

    private static ItemStack createSkullItem(String playerName)
    {
        ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwner(playerName);
        skullItem.setItemMeta(skullMeta);
        return skullItem;
    }
}
