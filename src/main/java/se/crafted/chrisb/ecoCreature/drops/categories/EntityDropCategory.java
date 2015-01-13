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
package se.crafted.chrisb.ecoCreature.drops.categories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.drops.rules.AbstractRule;
import se.crafted.chrisb.ecoCreature.drops.rules.Rule;
import se.crafted.chrisb.ecoCreature.drops.sources.AbstractDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.DropSourceFactory;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class EntityDropCategory extends AbstractDropCategory<EntityType>
{
    public EntityDropCategory(Map<EntityType, Collection<AbstractDropSource>> sources)
    {
        super(sources);
    }

    @Override
    protected boolean isValidEvent(Event event)
    {
        return event instanceof EntityKilledEvent;
    }

    @Override
    protected EntityType extractType(Event event)
    {
        return ((EntityKilledEvent) event).getEntity().getType();
    }

    @Override
    protected Player extractPlayer(Event event)
    {
        return ((EntityKilledEvent) event).getKiller();
    }

    public static AbstractDropCategory<EntityType> parseConfig(ConfigurationSection config)
    {
        Map<EntityType, Collection<AbstractDropSource>> sources = new HashMap<>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                EntityType type = EntityType.fromName(typeName);

                if (type != null) {
                    Map<Class<? extends AbstractRule>, Rule> huntingRules = loadHuntingRules(config.getConfigurationSection("System"));
                    huntingRules.putAll(loadHuntingRules(config.getConfigurationSection("RewardTable." + typeName)));
                    huntingRules.putAll(loadGainRules(config.getConfigurationSection("Gain")));

                    for (AbstractDropSource source : configureDropSources(DropSourceFactory.createSources("RewardTable." + typeName, config), config)) {
                        source.setHuntingRules(huntingRules);

                        if (!sources.containsKey(type)) {
                            sources.put(type, new ArrayList<AbstractDropSource>());
                        }

                        sources.get(type).add(source);
                        sources.get(type).addAll(parseSets("RewardTable." + typeName, config));
                    }
                }
            }
        }

        return new EntityDropCategory(sources);
    }
}
