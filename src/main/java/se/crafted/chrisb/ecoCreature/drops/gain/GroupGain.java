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
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

public class GroupGain extends AbstractPlayerGain<String>
{
    private boolean warnGroupMultiplierSupport;

    public GroupGain(Map<String, Double> multipliers)
    {
        super(multipliers, "gain.group");
        warnGroupMultiplierSupport = true;
    }

    @Override
    public double getGain(Player player)
    {
        double multiplier = NO_GAIN;

        try {
            String group = DependencyUtils.getPermission().getPrimaryGroup(player.getWorld().getName(), player.getName());
            if (group != null) {
                multiplier = getMultiplier(group.toLowerCase());
            }
        }
        catch (UnsupportedOperationException e) {
            if (warnGroupMultiplierSupport) {
                LoggerUtil.getInstance().warning(e.getMessage());
                warnGroupMultiplierSupport = false;
            }
        }

        LoggerUtil.getInstance().debug("Gain: " + multiplier);
        return multiplier;
    }

    public static Collection<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Collection<PlayerGain> gain = Collections.emptyList();

        if (config != null) {
            gain = new ArrayList<>();
            gain.add(new GroupGain(parseMultipliers(config)));
        }

        return gain;
    }
}
