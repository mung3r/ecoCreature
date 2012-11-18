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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.HeroesRewardType;

public class HeroesRewardSettings extends AbstractRewardSettings
{
    private Map<HeroesRewardType, List<AbstractRewardSource>> sources;

    public HeroesRewardSettings(Map<HeroesRewardType, List<AbstractRewardSource>> sources)
    {
        this.sources = sources;
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
        return DependencyUtils.hasHeroes() && event instanceof HeroChangeLevelEvent &&
        		hasRewardSource((HeroChangeLevelEvent) event);
    }

    private boolean hasRewardSource(HeroChangeLevelEvent event)
    {
        Player player = event.getHero().getPlayer();

        if (DependencyUtils.hasPermission(player, "reward.heromastered") && event.getHero().getLevel() == event.getHeroClass().getMaxLevel()) {
            return hasRewardSource(HeroesRewardType.HERO_MASTERED);
        }
        else if (DependencyUtils.hasPermission(player, "reward.heroleveled")) {
            return hasRewardSource(HeroesRewardType.HERO_LEVELED);
        }
        else {
            LoggerUtil.getInstance().debug(this.getClass(), "No reward for " + player.getName() + " due to lack of permission for " + HeroesRewardType.HERO_MASTERED.getName());
        }

        return false;
    }

    private boolean hasRewardSource(HeroesRewardType type)
    {
        return type != null && sources.containsKey(type) && !sources.get(type).isEmpty();
    }

    @Override
    public AbstractRewardSource getRewardSource(Event event)
    {
        if (DependencyUtils.hasHeroes() && event instanceof HeroChangeLevelEvent) {
            HeroChangeLevelEvent changeLevelEvent = (HeroChangeLevelEvent) event;

            if (changeLevelEvent.getHero().getLevel() == changeLevelEvent.getHeroClass().getMaxLevel()) {
                return getRewardSource(HeroesRewardType.HERO_MASTERED);
            }
            else {
                return getRewardSource(HeroesRewardType.HERO_LEVELED);
            }
        }

        return null;
    }

    private AbstractRewardSource getRewardSource(HeroesRewardType type)
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
        Map<HeroesRewardType, List<AbstractRewardSource>> sources = new HashMap<HeroesRewardType, List<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                HeroesRewardType type = HeroesRewardType.fromName(typeName);

                if (type != HeroesRewardType.INVALID) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(typeName, rewardTable.getConfigurationSection(typeName)), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(mergeSets(source, rewardTable, config.getConfigurationSection("RewardSets")));
                }
            }
        }

        return new HeroesRewardSettings(sources);
    }
}
