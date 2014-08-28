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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.commons.TimePeriod;

public class TimeGain extends AbstractPlayerGain<TimePeriod>
{
    public TimeGain(Map<TimePeriod, Double> multipliers)
    {
        super(multipliers, "gain.time");
    }

    @Override
    public double getGain(Player player)
    {
        double multiplier = getMultiplier(TimePeriod.fromEntity(player));
        LoggerUtil.getInstance().debug("Gain: " + multiplier);
        return multiplier;
    }

    public static Collection<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Collection<PlayerGain> gain = Collections.emptyList();

        if (config != null) {
            Map<TimePeriod, Double> multipliers = new HashMap<TimePeriod, Double>();
            for (String period : config.getKeys(false)) {
                try {
                    multipliers.put(TimePeriod.valueOf(period.toUpperCase()),
                            config.getConfigurationSection(period).getDouble(AMOUNT_KEY, NO_GAIN));
                }
                catch (IllegalArgumentException e) {
                    LoggerUtil.getInstance().warning("Skipping unknown time period name: " + period);
                }
            }
            gain = new ArrayList<PlayerGain>();
            gain.add(new TimeGain(multipliers));
        }

        return gain;
    }
}
