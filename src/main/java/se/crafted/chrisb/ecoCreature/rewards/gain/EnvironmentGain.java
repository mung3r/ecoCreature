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

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

public class EnvironmentGain extends AbstractPlayerGain<Environment>
{
    public EnvironmentGain(Map<Environment, Double> multipliers)
    {
        super(multipliers, "gain.environment");
    }

    @Override
    public double getGain(Player player)
    {
        return getMultiplier(player.getWorld().getEnvironment());
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            Map<Environment, Double> multipliers = new HashMap<World.Environment, Double>();
            for (String environment : config.getKeys(false)) {
                try {
                    multipliers.put(Environment.valueOf(environment.toUpperCase()),
                            config.getConfigurationSection(environment).getDouble(AMOUNT_KEY, NO_GAIN));
                }
                catch (IllegalArgumentException e) {
                    LoggerUtil.getInstance().warning("Skipping unknown environment name: " + environment);
                }
            }
            gain = new HashSet<PlayerGain>();
            gain.add(new EnvironmentGain(multipliers));
        }

        return gain;
    }
}
