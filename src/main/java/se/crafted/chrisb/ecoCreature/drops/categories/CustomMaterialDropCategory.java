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
package se.crafted.chrisb.ecoCreature.drops.categories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import se.crafted.chrisb.ecoCreature.drops.sources.AbstractDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.DropSourceFactory;
import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomMaterialType;

public class CustomMaterialDropCategory extends AbstractDropCategory<CustomMaterialType>
{
    public CustomMaterialDropCategory(Map<CustomMaterialType, Collection<AbstractDropSource>> dropSourceMap)
    {
        super(dropSourceMap);
    }

    @Override
    protected boolean isValidEvent(Event event)
    {
        return event instanceof BlockBreakEvent;
    }

    @Override
    protected CustomMaterialType extractType(Event event)
    {
        return CustomMaterialType.fromMaterial(((BlockBreakEvent) event).getBlock().getType());
    }

    @Override
    protected Player extractPlayer(Event event)
    {
        return ((BlockBreakEvent) event).getPlayer();
    }

    public static AbstractDropCategory<CustomMaterialType> parseConfig(ConfigurationSection config)
    {
        Map<CustomMaterialType, Collection<AbstractDropSource>> dropSourceMap = new HashMap<>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                CustomMaterialType type = CustomMaterialType.fromName(typeName);

                if (type.isValid()) {
                    for (AbstractDropSource dropSource : configureDropSources(DropSourceFactory.createSources("RewardTable." + typeName, config), config)) {

                        if (!dropSourceMap.containsKey(type)) {
                            dropSourceMap.put(type, new ArrayList<AbstractDropSource>());
                        }

                        dropSourceMap.get(type).add(dropSource);
                        dropSourceMap.get(type).addAll(parseSets("RewardTable." + typeName, config));
                    }
                }
            }
        }

        return new CustomMaterialDropCategory(dropSourceMap);
    }
}
