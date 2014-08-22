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
import se.crafted.chrisb.ecoCreature.settings.types.HeroesRewardType;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

public class HeroesRewardSettings extends AbstractRewardSettings<HeroesRewardType>
{
    public HeroesRewardSettings(Map<HeroesRewardType, Collection<AbstractRewardSource>> sources)
    {
        super(sources);
    }

    @Override
    protected boolean isValidEvent(Event event)
    {
        return DependencyUtils.hasHeroes() && event instanceof HeroChangeLevelEvent;
    }

    @Override
    protected HeroesRewardType extractType(Event event)
    {
        HeroesRewardType type = HeroesRewardType.INVALID;

        if (((HeroChangeLevelEvent) event).isMastering()) {
            type = HeroesRewardType.HERO_MASTERED;
        }
        else if (((HeroChangeLevelEvent) event).getTo() > ((HeroChangeLevelEvent) event).getFrom()) {
            type = HeroesRewardType.HERO_LEVELED;
        }

        return type;
    }

    @Override
    protected Player extractPlayer(Event event)
    {
        return ((HeroChangeLevelEvent) event).getHero().getPlayer();
    }

    public static AbstractRewardSettings<HeroesRewardType> parseConfig(ConfigurationSection config)
    {
        Map<HeroesRewardType, Collection<AbstractRewardSource>> sources = new HashMap<HeroesRewardType, Collection<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                HeroesRewardType type = HeroesRewardType.fromName(typeName);

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

        return new HeroesRewardSettings(sources);
    }
}
