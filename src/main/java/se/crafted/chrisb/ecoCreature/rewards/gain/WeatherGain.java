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

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

public class WeatherGain extends AbstractPlayerGain
{
    public enum WEATHER
    {
        STORMY, SUNNY;

        public static WEATHER fromBoolean(boolean hasStorm)
        {
            return hasStorm ? STORMY : SUNNY;
        }

        public static WEATHER fromName(String name)
        {
            for (WEATHER weather : WEATHER.values()) {
                if (weather.toString().equalsIgnoreCase(name)) {
                    return weather;
                }
            }

            return null;
        }
    }

    private Map<WEATHER, Double> multipliers;

    public WeatherGain(Map<WEATHER, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;
        WEATHER weather = WEATHER.fromBoolean(player.getWorld().hasStorm());

        if (DependencyUtils.hasPermission(player, "gain.weather") && multipliers.containsKey(weather)) {
            multiplier = multipliers.get(weather);
            LoggerUtil.getInstance().debug(this.getClass(), "Weather multiplier: " + multiplier);
        }

        return multiplier;
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            Map<WEATHER, Double> multipliers = new HashMap<WEATHER, Double>();
            for (String weather : config.getKeys(false)) {
                try {
                    multipliers.put(WEATHER.valueOf(weather.toUpperCase()), Double.valueOf(config.getConfigurationSection(weather).getDouble("Amount", 1.0D)));
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
