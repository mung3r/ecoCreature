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
package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.RewardSourceFactory;
import se.crafted.chrisb.ecoCreature.settings.types.HeroesRewardType;

public class HeroesRewardSource extends AbstractRewardSource
{
    public HeroesRewardSource(String section, ConfigurationSection config)
    {
        super(section, config);
        setNoCoinRewardMessage(DefaultMessage.NO_MESSAGE);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (DependencyUtils.hasHeroes() && event instanceof HeroChangeLevelEvent) {
            return ((HeroChangeLevelEvent) event).getHero().getPlayer().getLocation();
        }
        else {
            throw new IllegalArgumentException("Unrecognized event");
        }
    }

    @Override
    protected Reward createReward(Event event)
    {
        Reward reward = super.createReward(event);

        if (event instanceof HeroChangeLevelEvent) {
            HeroChangeLevelEvent changeLevelEvent = (HeroChangeLevelEvent) event;
            reward.addParameter(MessageToken.CLASS, changeLevelEvent.getHeroClass().getName());
        }

        return reward;
    }

    public static AbstractRewardSource createRewardSource(String section, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        switch (HeroesRewardType.fromName(RewardSourceFactory.parseRewardName(section))) {
            case HERO_LEVELED:
            case HERO_MASTERED:
                source = new HeroesRewardSource(section, config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + section);
        }
        return source;
    }
}
