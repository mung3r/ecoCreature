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
package se.crafted.chrisb.ecoCreature.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;

public class MaterialRewardSettings extends AbstractRewardSettings<Material>
{
    public MaterialRewardSettings(Map<Material, Collection<AbstractRewardSource>> sources)
    {
        super(sources);
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
        return event instanceof BlockBreakEvent && hasRewardSource((BlockBreakEvent) event);
    }

    private boolean hasRewardSource(final BlockBreakEvent event)
    {
        Block block = event.getBlock();

        // TODO: fix this properly for BuildCraft
        if (block == null) {
            return false;
        }

        return hasRewardSource(block.getType())
                && Iterables.any(getRewardSource(block.getType()), new Predicate<AbstractRewardSource>() {

                    @Override
                    public boolean apply(AbstractRewardSource source)
                    {
                        return source.hasPermission(event.getPlayer());
                    }
                });
    }

    @Override
    public Collection<AbstractRewardSource> getRewardSource(Event event)
    {
        if (event instanceof BlockBreakEvent) {
            return getRewardSource(((BlockBreakEvent) event).getBlock().getType());
        }

        return null;
    }

    public static AbstractRewardSettings<Material> parseConfig(ConfigurationSection config)
    {
        Map<Material, Collection<AbstractRewardSource>> sources = new HashMap<Material, Collection<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                Material type = Material.matchMaterial(typeName);

                if (type != null) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource("RewardTable." + typeName, config), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(source);
                    sources.get(type).addAll(getSets("RewardTable." + typeName, config));
                }
            }
        }

        return new MaterialRewardSettings(sources);
    }
}
