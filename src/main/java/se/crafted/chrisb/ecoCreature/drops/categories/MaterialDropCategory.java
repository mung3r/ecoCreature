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

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;

import se.crafted.chrisb.ecoCreature.drops.sources.AbstractDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.DropSourceFactory;

public class MaterialDropCategory extends AbstractDropCategory<MaterialData>
{
    public MaterialDropCategory(Map<MaterialData, Collection<AbstractDropSource>> dropSourceMap)
    {
        super(dropSourceMap);
    }

    @Override
    protected boolean isValidEvent(Event event)
    {
        return event instanceof BlockBreakEvent && ((BlockBreakEvent) event).getBlock() != null;
    }

    @Override
    protected MaterialData extractType(Event event)
    {
        return ((BlockBreakEvent) event).getBlock().getState().getData();
    }

    @Override
    protected Player extractPlayer(Event event)
    {
        return ((BlockBreakEvent) event).getPlayer();
    }

    public static AbstractDropCategory<MaterialData> parseConfig(ConfigurationSection config)
    {
        Map<MaterialData, Collection<AbstractDropSource>> dropSourceMap = new HashMap<>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                Material type = Material.matchMaterial(typeName);

                if (type != null) {
                    for (AbstractDropSource dropSource : DropSourceFactory.createSources("RewardTable." + typeName, config)) {

                        MaterialData materialData = new MaterialData(type, parseData(config.getConfigurationSection("RewardTable." + typeName)));
                        if (!dropSourceMap.containsKey(materialData)) {
                            dropSourceMap.put(materialData, new ArrayList<AbstractDropSource>());
                        }

                        dropSourceMap.get(materialData).add(dropSource);
                        dropSourceMap.get(materialData).addAll(parseSets("RewardTable." + typeName, config));
                    }
                }
            }
        }

        return new MaterialDropCategory(dropSourceMap);
    }

    private static byte parseData(ConfigurationSection config)
    {
        return (byte) config.getInt("Data", 0);
    }
}
