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
package se.crafted.chrisb.ecoCreature.drops;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;

public abstract class AbstractDrop
{
    private final Location location;
    private final String worldName;

    private final String name;
    private Message message;
    private final Map<MessageToken, String> parameters;

    public AbstractDrop(String name, Location location)
    {
        this.location = location;
        worldName = location.getWorld().getName();

        this.name = name;
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

    public String getName()
    {
        return name;
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

    public AbstractDrop addParameter(MessageToken key, String value)
    {
        parameters.put(key, value);
        return this;
    }

    public abstract boolean deliver(Player player);
}
