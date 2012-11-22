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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.StreakRewardType;

public class StreakRewardSettings extends AbstractRewardSettings
{
    private Map<StreakRewardType, List<AbstractRewardSource>> sources;

    public StreakRewardSettings(Map<StreakRewardType, List<AbstractRewardSource>> sources)
    {
        this.sources = sources;
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
    	if (DependencyUtils.hasDeathTpPlus()) {
	        if (event instanceof DeathStreakEvent) {
	            return hasRewardSource((DeathStreakEvent) event);
	        }
	        else if (event instanceof KillStreakEvent) {
	            return hasRewardSource((KillStreakEvent) event);
	        }
    	}

        return false;
    }

    private boolean hasRewardSource(DeathStreakEvent event)
    {
        if (DependencyUtils.hasPermission(event.getPlayer(), "reward.deathstreak")) {
            return hasRewardSource(StreakRewardType.DEATH_STREAK);
        }
        else {
            LoggerUtil.getInstance().debug(this.getClass(), "No reward for " + event.getPlayer().getName() + " due to lack of permission for " + StreakRewardType.DEATH_STREAK);
        }

        return false;
    }

    private boolean hasRewardSource(KillStreakEvent event)
    {
        if (DependencyUtils.hasPermission(event.getPlayer(), "reward.killstreak")) {
            return hasRewardSource(StreakRewardType.KILL_STREAK);
        }
        else {
            LoggerUtil.getInstance().debug(this.getClass(), "No reward for " + event.getPlayer().getName() + " due to lack of permission for " + StreakRewardType.KILL_STREAK);
        }

        return false;
    }

    private boolean hasRewardSource(StreakRewardType type)
    {
        return type != null && sources.containsKey(type) && !sources.get(type).isEmpty();
    }

    @Override
    public AbstractRewardSource getRewardSource(Event event)
    {
        if (DependencyUtils.hasDeathTpPlus() && event instanceof DeathStreakEvent) {
            return getRewardSource(StreakRewardType.DEATH_STREAK);
        }
        else if (DependencyUtils.hasDeathTpPlus() && event instanceof KillStreakEvent) {
            return getRewardSource(StreakRewardType.KILL_STREAK);
        }

        return null;
    }

    private AbstractRewardSource getRewardSource(StreakRewardType type)
    {
        AbstractRewardSource source = null;

        if (hasRewardSource(type)) {
            source = sources.get(type).get(nextInt(sources.get(type).size()));
        }
        else {
            LoggerUtil.getInstance().debug(this.getClass(), "No reward defined for custom type: " + type.name());
        }

        return source;
    }

    public static AbstractRewardSettings parseConfig(ConfigurationSection config)
    {
        Map<StreakRewardType, List<AbstractRewardSource>> sources = new HashMap<StreakRewardType, List<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                StreakRewardType type = StreakRewardType.fromName(typeName);

                if (type != StreakRewardType.INVALID) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(typeName, rewardTable.getConfigurationSection(typeName)), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(mergeSets(source, rewardTable, config.getConfigurationSection("RewardSets")));
                }
            }
        }

        return new StreakRewardSettings(sources);
    }
}
