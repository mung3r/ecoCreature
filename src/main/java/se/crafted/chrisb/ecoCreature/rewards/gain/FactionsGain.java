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

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

import com.massivecraft.factions.struct.Rel;

public class FactionsGain extends AbstractFactionsGain<Rel>
{
    public FactionsGain(Map<Rel, Double> multipliers)
    {
        super(multipliers);
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null && DependencyUtils.hasFactionsBeta()) {
            Map<Rel, Double> multipliers = new HashMap<Rel, Double>();
            for (String relation : config.getKeys(false)) {
                try {
                    multipliers.put(Rel.valueOf(relation), Double.valueOf(config.getConfigurationSection(relation).getDouble(AMOUNT_KEY, NO_GAIN)));
                }
                catch (IllegalArgumentException e) {
                    LoggerUtil.getInstance().warning("Unrecognized Factions relation: " + relation);
                }
            }
            gain = new HashSet<PlayerGain>();
            gain.add(new FactionsGain(multipliers));
        }

        return gain;
    }
}
