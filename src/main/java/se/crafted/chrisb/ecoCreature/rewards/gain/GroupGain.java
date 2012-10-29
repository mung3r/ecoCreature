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
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class GroupGain extends AbstractPlayerGain
{
    private boolean warnGroupMultiplierSupport;
    private Map<String, Double> multipliers;

    public GroupGain(Map<String, Double> multipliers)
    {
        warnGroupMultiplierSupport = true;
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        try {
            if (DependencyUtils.hasPermission() && DependencyUtils.getPermission().getPrimaryGroup(player.getWorld().getName(), player.getName()) != null) {
                String group = DependencyUtils.getPermission().getPrimaryGroup(player.getWorld().getName(), player.getName()).toLowerCase();
                if (DependencyUtils.hasPermission(player, "gain.group") && multipliers.containsKey(group)) {
                    multiplier = multipliers.get(group);
                    ECLogger.getInstance().debug(this.getClass(), "Group multiplier: " + multiplier);
                }
            }
        }
        catch (UnsupportedOperationException e) {
            if (warnGroupMultiplierSupport) {
                ECLogger.getInstance().warning(e.getMessage());
                warnGroupMultiplierSupport = false;
            }
        }

        return multiplier;
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            Map<String, Double> multipliers = new HashMap<String, Double>();
            for (String group : config.getKeys(false)) {
                multipliers.put(group.toLowerCase(), Double.valueOf(config.getConfigurationSection(group).getDouble("Amount", 0.0D)));
            }
            gain = new HashSet<PlayerGain>();
            gain.add(new GroupGain(multipliers));
        }

        return gain;
    }
}
