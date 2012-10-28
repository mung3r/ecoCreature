/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.commons.EntityUtils;

public final class PlayerKilledEvent extends PlayerDeathEvent
{
    public static PlayerKilledEvent createEvent(PlayerDeathEvent event)
    {
        return new PlayerKilledEvent(event.getEntity(), event.getDrops(), event.getDroppedExp(), event.getNewExp(), event.getNewTotalExp(), event.getNewLevel(), event.getDeathMessage());
    }

    private PlayerKilledEvent(Player player, List<ItemStack> drops, int droppedExp, int newExp, int newTotalExp, int newLevel, String deathMessage)
    {
        super(player, drops, droppedExp, newExp, newTotalExp, newLevel, deathMessage);
    }

    public Player getVictim()
    {
        return getEntity();
    }

    public Player getKiller()
    {
        return getEntity().getKiller();
    }

    public String getWeaponName()
    {
        return EntityUtils.getItemNameInHand(getKiller());
    }
}
