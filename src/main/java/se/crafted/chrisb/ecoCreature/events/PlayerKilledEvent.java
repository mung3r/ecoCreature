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
package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.commons.EntityUtils;

public final class PlayerKilledEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private PlayerDeathEvent event;

    public static PlayerKilledEvent createEvent(PlayerDeathEvent event)
    {
        return new PlayerKilledEvent(event);
    }

    private PlayerKilledEvent(PlayerDeathEvent event)
    {
        this.event = event;
    }

    public Player getVictim()
    {
        return event.getEntity();
    }

    public Player getKiller()
    {
        return event.getEntity().getKiller();
    }

    public String getWeaponName()
    {
        return EntityUtils.getItemNameInHand(getKiller());
    }

    public Player getEntity()
    {
        return event.getEntity();
    }

    public List<ItemStack> getDrops()
    {
        return event.getDrops();
    }

    public int getDroppedExp()
    {
        return event.getDroppedExp();
    }

    public void setDroppedExp(int exp)
    {
        event.setDroppedExp(exp);
    }

    public int getNewExp()
    {
        return event.getNewExp();
    }

    public int getNewTotalExp()
    {
        return event.getNewTotalExp();
    }

    public int getNewLevel()
    {
        return event.getNewLevel();
    }

    public String getDeathMessage()
    {
        return event.getDeathMessage();
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
