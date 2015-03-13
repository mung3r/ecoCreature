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
package se.crafted.chrisb.ecoCreature.drops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomEntityType;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;

public class AssembledDrop
{
    private static final double IDENTITY = 1.0;
    private static final double ZERO = 0.0;

    private Location location;
    private String worldName;
    private double gain;
    private Collection<String> party;
    private boolean integerCurrency;
    private boolean addToInventory;

    private String name;
    private double coin;
    private Collection<ItemStack> items;
    private Collection<CustomEntityType> customEntityTypes;
    private Collection<EntityType> entityTypes;
    private Collection<EntityType> jockeyTypes;
    private Message message;
    private Map<MessageToken, String> parameters;

    public AssembledDrop(String name, Location location)
    {
        this.location = location;
        worldName = location.getWorld().getName();
        gain = IDENTITY;
        party = Collections.emptyList();
        integerCurrency = false;

        this.name = name;
        coin = ZERO;
        items = new ArrayList<>();
        customEntityTypes = new ArrayList<>();
        entityTypes = new ArrayList<>();
        jockeyTypes = new ArrayList<>();
        message = DefaultMessage.EMPTY_MESSAGE;
        parameters = new HashMap<>();
    }

    public World getWorld()
    {
        return Bukkit.getWorld(worldName);
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public double getGain()
    {
        return gain;
    }

    public void setGain(double gain)
    {
        this.gain = gain;
    }

    public boolean hasParty()
    {
        return party.size() > 0;
    }

    public Collection<String> getParty()
    {
        return party;
    }

    public void setParty(Collection<String> party)
    {
        this.party = party;
    }

    public boolean isIntegerCurrency()
    {
        return integerCurrency;
    }

    public void setIntegerCurrency(boolean integerCurrency)
    {
        this.integerCurrency = integerCurrency;
    }

    public boolean isAddToInventory()
    {
        return addToInventory;
    }

    public void setAddToInventory(boolean addToInventory)
    {
        this.addToInventory = addToInventory;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getCoin()
    {
        return coin;
    }

    public void setCoin(double coin)
    {
        this.coin = coin;
    }

    public boolean hasDrops()
    {
        return !items.isEmpty() || !customEntityTypes.isEmpty() || !entityTypes.isEmpty() || !jockeyTypes.isEmpty();
    }

    public Collection<ItemStack> getItems()
    {
        return items;
    }

    public void setItems(Collection<ItemStack> items)
    {
        this.items = items;
    }

    public Collection<CustomEntityType> getCustomEntityTypes()
    {
        return customEntityTypes;
    }

    public void setCustomEntityTypes(Collection<CustomEntityType> customEntityTypes)
    {
        this.customEntityTypes = customEntityTypes;
    }

    public Collection<EntityType> getEntityTypes()
    {
        return entityTypes;
    }

    public void setEntityTypes(Collection<EntityType> entityTypes)
    {
        this.entityTypes = entityTypes;
    }

    public Collection<EntityType> getJockeyTypes()
    {
        return jockeyTypes;
    }

    public void setJockeyTypes(Collection<EntityType> jockeyTypes)
    {
        this.jockeyTypes = jockeyTypes;
    }

    public Message getMessage()
    {
        return message;
    }

    public void setMessage(Message message)
    {
        this.message = message;
    }

    public Map<MessageToken, String> getParameters()
    {
        return parameters;
    }

    public AssembledDrop addParameter(MessageToken key, String value)
    {
        parameters.put(key, value);
        return this;
    }
}
