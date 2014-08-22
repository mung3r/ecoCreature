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

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.McMMORewardType;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;

public class McMMORewardSettings extends AbstractRewardSettings<McMMORewardType>
{
    public McMMORewardSettings(Map<McMMORewardType, Collection<AbstractRewardSource>> sources)
    {
        super(sources);
    }

    @Override
    protected boolean isValidEvent(Event event)
    {
        return DependencyUtils.hasMcMMO() && event instanceof McMMOPlayerLevelUpEvent;
    }

    @Override
    protected McMMORewardType extractType(Event event)
    {
        return event instanceof McMMOPlayerLevelUpEvent ? McMMORewardType.MCMMO_LEVELED : McMMORewardType.INVALID;
    }

    @Override
    protected Player extractPlayer(Event event)
    {
        return ((McMMOPlayerLevelUpEvent) event).getPlayer();
    }

    public static AbstractRewardSettings<McMMORewardType> parseConfig(ConfigurationSection config)
    {
        Map<McMMORewardType, Collection<AbstractRewardSource>> sources = new HashMap<McMMORewardType, Collection<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                McMMORewardType type = McMMORewardType.fromName(typeName);

                if (type.isValid()) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource("RewardTable." + typeName, config), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(source);
                    sources.get(type).addAll(getSets("RewardTable." + typeName, config));
                }
            }
        }

        return new McMMORewardSettings(sources);
    }
}
