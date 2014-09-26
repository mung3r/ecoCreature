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
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;

public abstract class AbstractPlayerGain<T> implements PlayerGain
{
    protected static final String AMOUNT_KEY = "Amount";
    protected static final double NO_GAIN = 1.0;

    private final Map<T, Double> multipliers;
    private final String permission;

    public AbstractPlayerGain(Map<T, Double> multipliers, String permission)
    {
        this.multipliers = multipliers;
        this.permission = permission;
    }

    @Override
    public boolean hasPermission(Player player)
    {
        return DependencyUtils.hasPermission(player, permission);
    }

    @Override
    public abstract double getGain(Player player);

    protected double getMultiplier(T type)
    {
        return type != null && getMultipliers().containsKey(type) ? getMultipliers().get(type) : NO_GAIN;
    }

    protected Map<T, Double> getMultipliers()
    {
        return multipliers;
    }

    protected static Map<String, Double> parseMultipliers(ConfigurationSection config)
    {
        Map<String, Double> multipliers = Collections.emptyMap();

        if (config != null) {
            multipliers = new HashMap<>();
            for (String key : config.getKeys(false)) {
                multipliers.put(key, config.getConfigurationSection(key).getDouble(AMOUNT_KEY, NO_GAIN));
            }
        }

        return multipliers;
    }

    protected static Map<String, Double> parseMultiplier(ConfigurationSection config)
    {
        Map<String, Double> multipliers = Collections.emptyMap();

        if (config != null) {
            multipliers = new HashMap<>();
            multipliers.put(AMOUNT_KEY, config.getDouble(AMOUNT_KEY, NO_GAIN));
        }

        return multipliers;
    }
}
