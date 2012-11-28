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
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class AbstractPlayerGain<T> implements PlayerGain
{
    protected static final double NO_GAIN = 1.0;
    protected static final String AMOUNT_KEY = "Amount";

    private final Map<T, Double> multipliers;

    public AbstractPlayerGain(Map<T, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    public Map<T, Double> getMultipliers()
    {
        return multipliers;
    }

    @Override
    public abstract double getMultiplier(Player player);

    protected static Map<String, Double> parseMultipliers(ConfigurationSection config)
    {
        Map<String, Double> multipliers = Collections.emptyMap();

        if (config != null) {
            multipliers = new HashMap<String, Double>();
            for (String key : config.getKeys(false)) {
                multipliers.put(key, Double.valueOf(config.getConfigurationSection(key).getDouble("Amount", NO_GAIN)));
            }
        }

        return multipliers;
    }

    protected static Map<String, Double> parseMultiplier(ConfigurationSection config)
    {
        Map<String, Double> multipliers = Collections.emptyMap();

        if (config != null) {
            multipliers = new HashMap<String, Double>();
            multipliers.put(AMOUNT_KEY, config.getDouble("Amount", NO_GAIN));
        }

        return multipliers;
    }
}
