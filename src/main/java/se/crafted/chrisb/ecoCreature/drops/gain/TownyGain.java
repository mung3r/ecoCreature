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
package se.crafted.chrisb.ecoCreature.drops.gain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyGain extends AbstractPlayerGain<String>
{
    private static final String IN_TOWN = "InTown";

    public TownyGain(Map<String, Double> multipliers)
    {
        super(multipliers, "gain.towny");
    }

    @Override
    public boolean hasPermission(Player player)
    {
        return super.hasPermission(player) && DependencyUtils.hasTowny();
    }

    @Override
    public double getGain(Player player)
    {
        double multiplier = getMultiplier(TownyUniverse.getTownName(player.getLocation()));
        LoggerUtil.getInstance().debug("Gain: " + multiplier);
        return multiplier;
    }

    @Override
    protected double getMultiplier(String townName)
    {
        double multiplier = NO_GAIN;

        if (StringUtils.isNotEmpty(townName)) {
            if (getMultipliers().containsKey(townName)) {
                multiplier = getMultipliers().get(townName);
            } else if (getMultipliers().containsKey(IN_TOWN)) {
                multiplier = getMultipliers().get(IN_TOWN);
            }
        }

        return multiplier;
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            gain = new HashSet<PlayerGain>();
            gain.add(new TownyGain(parseMultipliers(config)));
        }

        return gain;
    }
}
