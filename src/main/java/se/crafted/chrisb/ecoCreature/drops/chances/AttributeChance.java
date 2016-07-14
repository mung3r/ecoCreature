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
package se.crafted.chrisb.ecoCreature.drops.chances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.AttributeModifier;
import minecraft.spigot.community.michel_0.api.Slot;

public class AttributeChance extends AbstractChance
{
    private final Attribute type;
    private final Slot slot;

    public AttributeChance(Attribute type, Slot slot)
    {
        this.type = type;
        this.slot = slot;
    }

    private Attribute getType()
    {
        return type;
    }

    private Slot getSlot()
    {
        return slot;
    }

    public static List<AttributeModifier> nextAttributes(Collection<AttributeChance> chances)
    {
        List<AttributeModifier> attributes = new ArrayList<>();

        for (AttributeChance chance : chances) {
            attributes.add(new AttributeModifier(chance.getType(), chance.getType().getName(), chance.getSlot(), 0, chance.nextDoubleAmount(), UUID.randomUUID()));
        }

        return attributes;
    }
}
