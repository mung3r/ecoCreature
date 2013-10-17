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
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.HeroesRewardType;

public class HeroesRewardSettings extends AbstractRewardSettings<HeroesRewardType>
{
    public HeroesRewardSettings(Map<HeroesRewardType, List<AbstractRewardSource>> sources)
    {
        super(sources);
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

        if (event.isMastering()) {
            return hasRewardSource(HeroesRewardType.HERO_MASTERED) && getRewardSource(HeroesRewardType.HERO_MASTERED).hasPermission(player);
        }

        return hasRewardSource(HeroesRewardType.HERO_LEVELED) && getRewardSource(HeroesRewardType.HERO_LEVELED).hasPermission(player);
    }

    @Override
    public AbstractRewardSource getRewardSource(Event event)
    {
        if (DependencyUtils.hasHeroes() && event instanceof HeroChangeLevelEvent) {
            HeroChangeLevelEvent changeLevelEvent = (HeroChangeLevelEvent) event;

            if (changeLevelEvent.isMastering()) {
                return getRewardSource(HeroesRewardType.HERO_MASTERED);
            }
            else {
                return getRewardSource(HeroesRewardType.HERO_LEVELED);
            }
        }

        return null;
    }

    public static AbstractRewardSettings<HeroesRewardType> parseConfig(ConfigurationSection config)
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
