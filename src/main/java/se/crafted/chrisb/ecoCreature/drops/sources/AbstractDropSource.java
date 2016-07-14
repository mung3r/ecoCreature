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
package se.crafted.chrisb.ecoCreature.drops.sources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.PluginUtils;
import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.chances.AbstractChance;
import se.crafted.chrisb.ecoCreature.drops.chances.BookChance;
import se.crafted.chrisb.ecoCreature.drops.chances.Chance;
import se.crafted.chrisb.ecoCreature.drops.chances.CoinChance;
import se.crafted.chrisb.ecoCreature.drops.chances.CustomEntityChance;
import se.crafted.chrisb.ecoCreature.drops.chances.DropChance;
import se.crafted.chrisb.ecoCreature.drops.chances.EntityChance;
import se.crafted.chrisb.ecoCreature.drops.chances.ItemChance;
import se.crafted.chrisb.ecoCreature.drops.chances.JockeyChance;
import se.crafted.chrisb.ecoCreature.drops.chances.LoreChance;
import se.crafted.chrisb.ecoCreature.drops.rules.AbstractRule;
import se.crafted.chrisb.ecoCreature.drops.rules.Rule;

public abstract class AbstractDropSource extends AbstractChance
{
    private String name;
    private Collection<Chance> chances;
    private Map<Class<? extends AbstractRule>, Rule> huntingRules;

    public AbstractDropSource()
    {
        huntingRules = Collections.emptyMap();
    }

    public AbstractDropSource(String section, ConfigurationSection config)
    {
        this();

        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        ConfigurationSection dropConfig = config.getConfigurationSection(section);
        name = dropConfig.getName();

        chances = new ArrayList<>();
        chances.addAll(ItemChance.parseConfig(section, config));
        chances.addAll(BookChance.parseConfig(section, config));
        chances.addAll(LoreChance.parseConfig(section, config));
        chances.addAll(CustomEntityChance.parseConfig(dropConfig));
        chances.addAll(EntityChance.parseConfig(dropConfig));
        chances.addAll(JockeyChance.parseConfig(dropConfig));
        chances.addAll(CoinChance.parseConfig(section, config));
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean hasPermission(Player player)
    {
        return PluginUtils.hasPermission(player, "reward." + name);
    }

    public Map<Class<? extends AbstractRule>, Rule> getHuntingRules()
    {
        return huntingRules;
    }

    public void setHuntingRules(Map<Class<? extends AbstractRule>, Rule> huntingRules)
    {
        this.huntingRules = huntingRules;
    }

    public Collection<AbstractDrop> collectDrops(Event event)
    {
        Collection<AbstractDrop> drops = new ArrayList<>();
        int amount = nextIntAmount();

        for (int i = 0; i < amount; i++) {
            drops.addAll(collectDrop(event));
        }

        return drops;
    }

    protected Collection<AbstractDrop> collectDrop(Event event)
    {
        Collection<AbstractDrop> drops = new ArrayList<>();

        for (Chance chance : chances) {
            if (chance instanceof DropChance) {
                DropChance dropChance = (DropChance) chance;
                drops.add(dropChance.nextDrop(name, getLocation(event), getLootLevel(event)));
            }
        }

        return drops;
    }

    protected abstract Location getLocation(Event event);

    protected int getLootLevel(Event event)
    {
        return 0;
    }
}
