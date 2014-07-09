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

import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

public class BiomeGain extends AbstractPlayerGain<Biome>
{
    public BiomeGain(Map<Biome, Double> multipliers)
    {
        super(multipliers, "gain.biome");
    }

    @Override
    public double getGain(Player player)
    {
        return getMultiplier(getBiome(player));
    }

    private static Biome getBiome(Player player)
    {
        return player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockY());
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            Map<Biome, Double> multipliers = new HashMap<Biome, Double>();
            for (String biome : config.getKeys(false)) {
                try {
                    multipliers.put(Biome.valueOf(biome.toUpperCase()), config.getConfigurationSection(biome).getDouble(AMOUNT_KEY, NO_GAIN));
                }
                catch (IllegalArgumentException e) {
                    LoggerUtil.getInstance().warning("Skipping unknown biome name: " + biome);
                }
            }
            gain = new HashSet<PlayerGain>();
            gain.add(new BiomeGain(multipliers));
        }

        return gain;
    }
}
