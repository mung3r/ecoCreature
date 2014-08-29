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

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import se.crafted.chrisb.ecoCreature.drops.DropSourceFactory;
import se.crafted.chrisb.ecoCreature.drops.sources.AbstractDropSource;

public class MaterialDropCategory extends AbstractDropCategory<Material>
{
    public MaterialDropCategory(Map<Material, Collection<AbstractDropSource>> sources)
    {
        super(sources);
    }

    @Override
    protected boolean isValidEvent(Event event)
    {
        return event instanceof BlockBreakEvent && ((BlockBreakEvent) event).getBlock() != null;
    }

    @Override
    protected Material extractType(Event event)
    {
        return ((BlockBreakEvent) event).getBlock().getType();
    }

    @Override
    protected Player extractPlayer(Event event)
    {
        return ((BlockBreakEvent) event).getPlayer();
    }

    public static AbstractDropCategory<Material> parseConfig(ConfigurationSection config)
    {
        Map<Material, Collection<AbstractDropSource>> sources = new HashMap<Material, Collection<AbstractDropSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                Material type = Material.matchMaterial(typeName);

                if (type != null) {
                    for (AbstractDropSource source : configureDropSources(DropSourceFactory.createSources("RewardTable." + typeName, config), config)) {

                        if (!sources.containsKey(type)) {
                            sources.put(type, new ArrayList<AbstractDropSource>());
                        }

                        sources.get(type).add(source);
                        sources.get(type).addAll(getSets("RewardTable." + typeName, config));
                    }
                }
            }
        }

        return new MaterialDropCategory(sources);
    }
}
