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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.CustomRewardType;

public class CustomRewardSettings extends AbstractRewardSettings<CustomRewardType>
{
    public CustomRewardSettings(Map<CustomRewardType, Collection<AbstractRewardSource>> sources)
    {
        super(sources);
    }

    @Override
    protected boolean isValidEvent(Event event)
    {
        return event instanceof PlayerKilledEvent || event instanceof PlayerDeathEvent;
    }

    @Override
    protected CustomRewardType extractType(Event event)
    {
        CustomRewardType type = CustomRewardType.INVALID;

        if (event instanceof PlayerKilledEvent) {
            type = CustomRewardType.LEGACY_PVP;
        }
        else if (event instanceof PlayerDeathEvent) {
            type = CustomRewardType.DEATH_PENALTY;
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

    public static AbstractRewardSettings<CustomRewardType> parseConfig(ConfigurationSection config)
    {
        Map<CustomRewardType, Collection<AbstractRewardSource>> sources = new HashMap<CustomRewardType, Collection<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                CustomRewardType type = CustomRewardType.fromName(typeName);

                if (type.isValid()) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource("RewardTable." + typeName, config), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(source);
                    sources.get(type).addAll(getSets("RewardTable." + typeName, config));
                }
            }

            if (config.getBoolean("System.Hunting.PenalizeDeath", false)) {
                AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(CustomRewardType.DEATH_PENALTY.toString(), config), config);
                if (!sources.containsKey(CustomRewardType.DEATH_PENALTY)) {
                    sources.put(CustomRewardType.DEATH_PENALTY, new ArrayList<AbstractRewardSource>());
                }

                sources.get(CustomRewardType.DEATH_PENALTY).add(source);
            }

            if (config.getBoolean("System.Hunting.PVPReward", false)) {
                AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(CustomRewardType.LEGACY_PVP.toString(), config), config);
                if (!sources.containsKey(CustomRewardType.LEGACY_PVP)) {
                    sources.put(CustomRewardType.LEGACY_PVP, new ArrayList<AbstractRewardSource>());
                }

                sources.get(CustomRewardType.LEGACY_PVP).add(source);
            }
        }

        return new CustomRewardSettings(sources);
    }
}
