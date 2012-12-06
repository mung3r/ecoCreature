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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.commons.WeatherType;

public class WeatherGain extends AbstractPlayerGain<WeatherType>
{
    public WeatherGain(Map<WeatherType, Double> multipliers)
    {
        super(multipliers, "gain.weather");
    }

    @Override
    public double getMultiplier(Player player)
    {
        WeatherType weather = WeatherType.fromBoolean(player.getWorld().hasStorm());
        double multiplier = getMultipliers().containsKey(weather) ? getMultipliers().get(weather) : NO_GAIN;
        LoggerUtil.getInstance().debug(this.getClass(), "Weather multiplier: " + multiplier);
        return multiplier;
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            Map<WeatherType, Double> multipliers = new HashMap<WeatherType, Double>();
            for (String weather : config.getKeys(false)) {
                try {
                    multipliers.put(WeatherType.valueOf(weather.toUpperCase()),
                            Double.valueOf(config.getConfigurationSection(weather).getDouble(AMOUNT_KEY, 1.0D)));
                }
                catch (Exception e) {
                    LoggerUtil.getInstance().warning("Skipping unknown weather name: " + weather);
                }
            }
            gain = new HashSet<PlayerGain>();
            gain.add(new WeatherGain(multipliers));
        }

        return gain;
    }
}
