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
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.drops.sources.AbstractDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.DropSourceFactory;
import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomDropType;

public class CustomDropCategory extends AbstractDropCategory<CustomDropType>
{
    public CustomDropCategory(Map<CustomDropType, Collection<AbstractDropSource>> dropSourceMap)
    {
        super(dropSourceMap);
    }

    @Override
    protected boolean isValidEvent(Event event)
    {
        return event instanceof PlayerKilledEvent || event instanceof PlayerDeathEvent;
    }

    @Override
    protected CustomDropType extractType(Event event)
    {
        CustomDropType type = CustomDropType.INVALID;

        if (event instanceof PlayerKilledEvent) {
            type = CustomDropType.LEGACY_PVP;
        }
        else if (event instanceof PlayerDeathEvent) {
            type = CustomDropType.DEATH_PENALTY;
        }

        return type;
    }

    @Override
    protected Player extractPlayer(Event event)
    {
        Player player = null;

        if (event instanceof PlayerKilledEvent) {
            player = ((PlayerKilledEvent) event).getKiller();
        }
        else if (event instanceof PlayerDeathEvent) {
            player = ((PlayerDeathEvent) event).getEntity();
        }

        return player;
    }

    public static AbstractDropCategory<CustomDropType> parseConfig(ConfigurationSection config)
    {
        Map<CustomDropType, Collection<AbstractDropSource>> dropSourceMap = new HashMap<>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                CustomDropType type = CustomDropType.fromName(typeName);

                if (type.isValid()) {
                    for (AbstractDropSource dropSource : DropSourceFactory.createSources("RewardTable." + typeName, config)) {

                        if (!dropSourceMap.containsKey(type)) {
                            dropSourceMap.put(type, new ArrayList<AbstractDropSource>());
                        }

                        dropSourceMap.get(type).add(dropSource);
                        dropSourceMap.get(type).addAll(parseSets("RewardTable." + typeName, config));
                    }
                }
            }

            if (config.getBoolean("System.Hunting.PenalizeDeath")) {
                for (AbstractDropSource dropSource : DropSourceFactory.createSources(CustomDropType.DEATH_PENALTY.toString(), config)) {
                    if (!dropSourceMap.containsKey(CustomDropType.DEATH_PENALTY)) {
                        dropSourceMap.put(CustomDropType.DEATH_PENALTY, new ArrayList<AbstractDropSource>());
                    }

                    dropSourceMap.get(CustomDropType.DEATH_PENALTY).add(dropSource);
                }
            }

            if (config.getBoolean("System.Hunting.PVPReward")) {
                for (AbstractDropSource dropSource : DropSourceFactory.createSources(CustomDropType.LEGACY_PVP.toString(), config)) {
                    if (!dropSourceMap.containsKey(CustomDropType.LEGACY_PVP)) {
                        dropSourceMap.put(CustomDropType.LEGACY_PVP, new ArrayList<AbstractDropSource>());
                    }

                    dropSourceMap.get(CustomDropType.LEGACY_PVP).add(dropSource);
                }
            }
        }

        return new CustomDropCategory(dropSourceMap);
    }
}
