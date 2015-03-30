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
package se.crafted.chrisb.ecoCreature.events;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;

public class DropEvent extends Event implements Cancellable
{
    private static final HandlerList HANDLERS = new HandlerList();

    private UUID playerId;
    private Collection<AbstractDrop> drops;

    private boolean isCancelled;

    public DropEvent(Player player, Collection<AbstractDrop> drops)
    {
        this(player.getUniqueId(), drops);
    }

    public DropEvent(UUID player, Collection<AbstractDrop> drops)
    {
        this.playerId = player;
        this.drops = drops;
    }

    public Player getPlayer()
    {
        return Bukkit.getPlayer(playerId);
    }

    public void setPlayer(UUID player)
    {
        this.playerId = player;
    }

    public Collection<AbstractDrop> getDrops()
    {
        return drops;
    }

    public void setDrops(Collection<AbstractDrop> drop)
    {
        this.drops = drop;
    }

    public boolean isEmpty()
    {
        return drops.isEmpty();
    }

    @Override
    public boolean isCancelled()
    {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled)
    {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }
}
