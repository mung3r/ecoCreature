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

import java.util.Collections;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;

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

    public Message getMessage()
    {
        return message;
    }

    public void setMessage(Message message)
    {
        this.message = message;
    }

    @Override
    public void enforce(Event event)
    {
        Map<MessageToken, String> parameters = Collections.emptyMap();
        MessageHandler handler = new MessageHandler(message, parameters);
        handler.send(getKiller(event));
    }

    public abstract Player getKiller(Event event);

    public boolean isClearDrops()
    {
        return clearDrops;
    }

    public void setClearDrops(boolean clearDrops)
    {
        this.clearDrops = clearDrops;
    }

    public boolean isClearExpOrbs()
    {
        return clearExpOrbs;
    }

    public void setClearExpOrbs(boolean clearExpOrbs)
    {
        this.clearExpOrbs = clearExpOrbs;
    }
}
