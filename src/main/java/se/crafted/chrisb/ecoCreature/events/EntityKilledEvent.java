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

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.commons.EventUtils;
import se.crafted.chrisb.ecoCreature.drops.SpawnerMobTracker;

public final class EntityKilledEvent extends Event
{
    private static final HandlerList HANDLERS = new HandlerList();

    private EntityDeathEvent event;
    private SpawnerMobTracker spawnerMobTracker;

    public static EntityKilledEvent createEvent(EntityDeathEvent event)
    {
        return new EntityKilledEvent(event);
    }

    private EntityKilledEvent(EntityDeathEvent event)
    {
        this.event = event;
    }

    public Player getKiller()
    {
        return EventUtils.getKillerFromDeathEvent(event);
    }

    public String getWeaponName()
    {
        return isTamedCreatureKill() ? getTamedCreature().getType().getName() : EntityUtils.getItemNameInHand(getKiller());
    }

    public boolean isTamedCreatureKill()
    {
        return getTamedCreature() != null;
    }

    private LivingEntity getTamedCreature()
    {
        return EventUtils.getTamedKillerFromDeathEvent(event);
    }

    public boolean isProjectileKill()
    {
        return EventUtils.isProjectileKill(event);
    }

    public SpawnerMobTracker getSpawnerMobTracker()
    {
        return spawnerMobTracker;
    }

    public void setSpawnerMobTracker(SpawnerMobTracker spawnerMobTracker)
    {
        this.spawnerMobTracker = spawnerMobTracker;
    }

    public LivingEntity getEntity()
    {
        return event.getEntity();
    }

    public List<ItemStack> getDrops()
    {
        return event.getDrops();
    }

    public void setDroppedExp(int exp)
    {
        event.setDroppedExp(exp);
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }
}
