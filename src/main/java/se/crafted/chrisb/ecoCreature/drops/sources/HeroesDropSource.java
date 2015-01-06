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
package se.crafted.chrisb.ecoCreature.drops.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.drops.AssembledDrop;
import se.crafted.chrisb.ecoCreature.drops.categories.types.HeroesDropType;

public class HeroesDropSource extends AbstractDropSource
{
    public HeroesDropSource(String section, ConfigurationSection config)
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
    protected AssembledDrop assembleDrop(Event event)
    {
        AssembledDrop drop = super.assembleDrop(event);

        if (event instanceof HeroChangeLevelEvent) {
            HeroChangeLevelEvent changeLevelEvent = (HeroChangeLevelEvent) event;
            drop.addParameter(MessageToken.CLASS, changeLevelEvent.getHeroClass().getName());
        }

        return drop;
    }

    public static AbstractDropSource createDropSource(String section, ConfigurationSection config)
    {
        AbstractDropSource source;

        switch (HeroesDropType.fromName(DropSourceFactory.parseTypeName(section))) {
            case HERO_LEVELED:
            case HERO_MASTERED:
                source = new HeroesDropSource(section, config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + section);
        }
        return source;
    }
}
