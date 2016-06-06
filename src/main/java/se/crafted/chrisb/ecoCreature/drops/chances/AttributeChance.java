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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.AttributeModifier;
import minecraft.spigot.community.michel_0.api.Slot;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;

public class AttributeChance extends AbstractChance
{

    private static final String ATTACK_DAMAGE_MESSAGE = "&6<amt>&7 Damage";
    private static final String FOLLOW_RANGE_MESSAGE = "&6<amt>&7 Follow Range";
    private static final String JUMP_STRENGTH_MESSAGE = "&6<amt>&7 Jump";
    private static final String KNOCKBACK_RESISTANCE_MESSAGE = "&6<amt>&7 Knockback Resist";
    private static final String MAX_HEALTH_MESSAGE = "&6<amt>&7 Health";
    private static final String MOVEMENT_SPEED_MESSAGE = "&6<amt>&7 Movement Speed";
    private static final String SPAWN_REINFORCEMENTS_MESSAGE = "&6<amt>&7 Reinforcements";

    private static final String MAIN_HAND = "&7When in hand";
    private static final String OFF_HAND = "&7When in off hand";
    private static final String FEET = "&7When on feet";
    private static final String LEGS = "&7When on legs";
    private static final String CHEST = "&When on chest";
    private static final String HEAD = "&When on head";

    protected static final Map<Attribute, Message> ATTRIBUTE_LORE = new HashMap<>();
    protected static final Map<Slot, Message> SLOT_LORE = new HashMap<>();

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

    public static Collection<AttributeChance> parseConfig(String section, ConfigurationSection config)
    {
        Collection<AttributeChance> chances = Collections.emptyList();

        ATTRIBUTE_LORE.put(Attribute.ATTACK_DAMAGE, new DefaultMessage(config.getString("System.Messages.Attributes.ATTACK_DAMAGE", ATTACK_DAMAGE_MESSAGE)));
        ATTRIBUTE_LORE.put(Attribute.FOLLOW_RANGE, new DefaultMessage(config.getString("System.Messages.Attributes.FOLLOW_RANGE", FOLLOW_RANGE_MESSAGE)));
        ATTRIBUTE_LORE.put(Attribute.JUMP_STRENGTH, new DefaultMessage(config.getString("System.Messages.Attributes.JUMP_STRENGTH", JUMP_STRENGTH_MESSAGE)));
        ATTRIBUTE_LORE.put(Attribute.KNOCKBACK_RESISTANCE, new DefaultMessage(config.getString("System.Messages.Attributes.KNOCKBACK_RESISTANCE", KNOCKBACK_RESISTANCE_MESSAGE)));
        ATTRIBUTE_LORE.put(Attribute.MAX_HEALTH, new DefaultMessage(config.getString("System.Messages.Attributes.MAX_HEALTH", MAX_HEALTH_MESSAGE)));
        ATTRIBUTE_LORE.put(Attribute.MOVEMENT_SPEED, new DefaultMessage(config.getString("System.Messages.Attributes.MOVEMENT_SPEED", MOVEMENT_SPEED_MESSAGE)));
        ATTRIBUTE_LORE.put(Attribute.SPAWN_REINFORCEMENTS, new DefaultMessage(config.getString("System.Messages.Attributes.SPAWN_REINFORCEMENTS", SPAWN_REINFORCEMENTS_MESSAGE)));

        SLOT_LORE.put(Slot.MAIN_HAND, new DefaultMessage(config.getString("System.Messages.Slots.MAIN_HAND", MAIN_HAND)));
        SLOT_LORE.put(Slot.OFF_HAND, new DefaultMessage(config.getString("System.Messages.Slots.OFF_HAND", OFF_HAND)));
        SLOT_LORE.put(Slot.FEET, new DefaultMessage(config.getString("System.Messages.Slots.FEET", FEET)));
        SLOT_LORE.put(Slot.LEGS, new DefaultMessage(config.getString("System.Messages.Slots.LEGS", LEGS)));
        SLOT_LORE.put(Slot.CHEST, new DefaultMessage(config.getString("System.Messages.Slots.CHEST", CHEST)));
        SLOT_LORE.put(Slot.HEAD, new DefaultMessage(config.getString("System.Messages.Slots.HEAD", HEAD)));

        return chances;
    }
}
