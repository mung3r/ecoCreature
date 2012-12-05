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
package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

public class HeroesGain extends AbstractPlayerGain<String>
{
    public HeroesGain(Map<String, Double> multipliers)
    {
        super(multipliers);
    }

    @Override
    public boolean hasPermission(Player player)
    {
        return DependencyUtils.hasPermission(player, "gain.heroes") && DependencyUtils.hasHeroes();
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = DependencyUtils.getHeroes().getCharacterManager().getHero(player).hasParty() ? getMultipliers().get(AMOUNT_KEY) : NO_GAIN;
        LoggerUtil.getInstance().debug(this.getClass(), "Heroes multiplier: " + multiplier);
        return multiplier;
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            gain = new HashSet<PlayerGain>();
            gain.add(new HeroesGain(parseMultiplier(config.getConfigurationSection("InParty"))));
        }

        return gain;
    }
}
