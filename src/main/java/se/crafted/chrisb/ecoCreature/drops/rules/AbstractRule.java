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
package se.crafted.chrisb.ecoCreature.drops.rules;

import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;

public abstract class AbstractRule implements Rule
{
    private boolean clearDrops;
    private boolean clearExpOrbs;
    private Message message;

    public AbstractRule()
    {
        clearDrops = false;
        clearExpOrbs = false;
        message = DefaultMessage.EMPTY_MESSAGE;
    }

    @Override
    public Message getMessage()
    {
        return message;
    }

    @Override
    public void setMessage(Message message)
    {
        this.message = message;
    }

    @Override
    public abstract void handleDrops(Event event);

    @Override
    public boolean isClearDrops()
    {
        return clearDrops;
    }

    @Override
    public void setClearDrops(boolean clearDrops)
    {
        this.clearDrops = clearDrops;
    }

    @Override
    public boolean isClearExpOrbs()
    {
        return clearExpOrbs;
    }

    @Override
    public void setClearExpOrbs(boolean clearExpOrbs)
    {
        this.clearExpOrbs = clearExpOrbs;
    }
}
