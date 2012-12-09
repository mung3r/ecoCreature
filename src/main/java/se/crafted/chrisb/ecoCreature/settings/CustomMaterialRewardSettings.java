/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.CustomMaterialRewardType;

public class CustomMaterialRewardSettings extends AbstractRewardSettings<CustomMaterialRewardType>
{
    public CustomMaterialRewardSettings(Map<CustomMaterialRewardType, List<AbstractRewardSource>> sources)
    {
        super(sources);
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
        return event instanceof BlockBreakEvent && hasRewardSource((BlockBreakEvent) event);
    }

    private boolean hasRewardSource(BlockBreakEvent event)
    {
        CustomMaterialRewardType type = CustomMaterialRewardType.fromMaterial(event.getBlock().getType());
        return hasRewardSource(type) && getRewardSource(type).hasPermission(event.getPlayer());
    }

    @Override
    public AbstractRewardSource getRewardSource(Event event)
    {
        if (event instanceof BlockBreakEvent) {
            return getRewardSource(((BlockBreakEvent) event).getBlock());
        }

        return null;
    }

    private AbstractRewardSource getRewardSource(Block block)
    {
        return getRewardSource(CustomMaterialRewardType.fromMaterial(block.getType()));
    }

    public static AbstractRewardSettings<CustomMaterialRewardType> parseConfig(ConfigurationSection config)
    {
        Map<CustomMaterialRewardType, List<AbstractRewardSource>> sources = new HashMap<CustomMaterialRewardType, List<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                CustomMaterialRewardType type = CustomMaterialRewardType.fromName(typeName);

                if (type != CustomMaterialRewardType.INVALID) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(typeName, rewardTable.getConfigurationSection(typeName)), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(mergeSets(source, rewardTable, config.getConfigurationSection("RewardSets")));
                }
            }
        }

        return new CustomMaterialRewardSettings(sources);
    }
}
