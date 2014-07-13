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
import org.bukkit.event.Event;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

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

    private boolean hasRewardSource(final DeathStreakEvent event)
    {
        return hasRewardSource(StreakRewardType.DEATH_STREAK)
                && Iterables.any(getRewardSource(StreakRewardType.DEATH_STREAK), new Predicate<AbstractRewardSource>() {

                    @Override
                    public boolean apply(AbstractRewardSource source)
                    {
                        return source.hasPermission(event.getPlayer());
                    }
                });
    }

    private boolean hasRewardSource(final KillStreakEvent event)
    {
        return hasRewardSource(StreakRewardType.KILL_STREAK)
                && Iterables.any(getRewardSource(StreakRewardType.KILL_STREAK), new Predicate<AbstractRewardSource>() {
        
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
        if (DependencyUtils.hasDeathTpPlus() && event instanceof DeathStreakEvent) {
            return getRewardSource(StreakRewardType.DEATH_STREAK);
        }
        else if (DependencyUtils.hasDeathTpPlus() && event instanceof KillStreakEvent) {
            return getRewardSource(StreakRewardType.KILL_STREAK);
        }

        return null;
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
