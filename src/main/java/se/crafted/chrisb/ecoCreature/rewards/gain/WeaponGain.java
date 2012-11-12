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

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;

public class WeaponGain extends AbstractPlayerGain
{
    private Map<Material, Double> multipliers;

    public WeaponGain(Map<Material, Double> materialMultipliers)
    {
        this.multipliers = materialMultipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;
        Material material = EntityUtils.getItemTypeInHand(player);

        if (DependencyUtils.hasPermission(player, "gain.weapon") && multipliers.containsKey(material)) {
            multiplier = multipliers.get(material);
            LoggerUtil.getInstance().debug(this.getClass(), "Weapon multiplier: " + multiplier);
        }

        return multiplier;
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            Map<Material, Double> multipliers = new HashMap<Material, Double>();

            for (String material : config.getKeys(false)) {
                multipliers.put(Material.matchMaterial(material), Double.valueOf(config.getConfigurationSection(material).getDouble("Amount", 1.0)));
            }
            gain = new HashSet<PlayerGain>();
            gain.add(new WeaponGain(multipliers));
        }

        return gain;
    }
}
