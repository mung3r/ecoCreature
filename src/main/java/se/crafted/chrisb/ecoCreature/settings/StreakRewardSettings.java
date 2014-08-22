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
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.StreakRewardType;

public class StreakRewardSettings extends AbstractRewardSettings<StreakRewardType>
{
    public StreakRewardSettings(Map<StreakRewardType, Collection<AbstractRewardSource>> sources)
    {
        super(sources);
    }

    @Override
    protected boolean isValidEvent(Event event)
    {
        return DependencyUtils.hasDeathTpPlus() && (event instanceof DeathStreakEvent || event instanceof KillStreakEvent);
    }

    @Override
    protected StreakRewardType extractType(Event event)
    {
        StreakRewardType type = StreakRewardType.INVALID;

        if (event instanceof DeathStreakEvent) {
            type = StreakRewardType.DEATH_STREAK; 
        }
        else if (event instanceof KillStreakEvent) {
            type = StreakRewardType.KILL_STREAK;
        }

        return type;
    }

    @Override
    protected Player extractPlayer(Event event)
    {
        Player player = null;

        if (event instanceof DeathStreakEvent) {
            player = ((DeathStreakEvent) event).getPlayer();
        }
        else if (event instanceof KillStreakEvent) {
            player = ((KillStreakEvent) event).getPlayer();
        }

        return player;
    }

    public static AbstractRewardSettings<StreakRewardType> parseConfig(ConfigurationSection config)
    {
        Map<StreakRewardType, Collection<AbstractRewardSource>> sources = new HashMap<StreakRewardType, Collection<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                StreakRewardType type = StreakRewardType.fromName(typeName);

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

        return new StreakRewardSettings(sources);
    }
}
