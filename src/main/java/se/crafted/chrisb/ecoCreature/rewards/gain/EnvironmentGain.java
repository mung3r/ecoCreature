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

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

public class EnvironmentGain extends AbstractPlayerGain
{
    private Map<Environment, Double> multipliers;

    public EnvironmentGain(Map<Environment, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = NO_GAIN;

        if (DependencyUtils.hasPermission(player, "gain.environment") && multipliers.containsKey(player.getWorld().getEnvironment())) {
            multiplier = multipliers.get(player.getWorld().getEnvironment());
            LoggerUtil.getInstance().debug(this.getClass(), "Environment multiplier: " + multiplier);
        }

        return multiplier;
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            Map<Environment, Double> multipliers = new HashMap<World.Environment, Double>();
            for (String environment : config.getKeys(false)) {
                try {
                    multipliers.put(Environment.valueOf(environment.toUpperCase()), Double.valueOf(config.getConfigurationSection(environment).getDouble("Amount", NO_GAIN)));
                }
                catch (Exception e) {
                    LoggerUtil.getInstance().warning("Skipping unknown environment name: " + environment);
                }
            }
            gain = new HashSet<PlayerGain>();
            gain.add(new EnvironmentGain(multipliers));
        }

        return gain;
    }
}
