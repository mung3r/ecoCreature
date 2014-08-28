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
package se.crafted.chrisb.ecoCreature.drops.parties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

import com.herocraftonline.heroes.characters.Hero;

public class HeroesParty extends AbstractParty
{
    @Override
    public Collection<String> getMembers(Player player)
    {
        Collection<String> party = Collections.emptyList();

        if (DependencyUtils.hasHeroes() && DependencyUtils.getHeroes().getCharacterManager().getHero(player).hasParty()) {
            party = new ArrayList<String>();

            for (Hero hero : DependencyUtils.getHeroes().getCharacterManager().getHero(player).getParty().getMembers()) {
                party.add(hero.getPlayer().getName());
            }
        }
        LoggerUtil.getInstance().debug("Party size: " + party.size());

        return party;
    }

    public static Collection<Party> parseConfig(ConfigurationSection config)
    {
        Collection<Party> parties = Collections.emptyList();

        if (config != null) {
            HeroesParty party = new HeroesParty();
            party.setShared(config.getBoolean("InParty.Share", false));
            parties = new ArrayList<Party>();
            parties.add(party);
        }

        return parties;
    }
}
