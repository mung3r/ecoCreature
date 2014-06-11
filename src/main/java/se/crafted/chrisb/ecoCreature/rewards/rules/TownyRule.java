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
package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.palmergames.bukkit.towny.object.TownyUniverse;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class TownyRule extends AbstractRule
{
    private String townName;

    public TownyRule()
    {
        setClearExpOrbs(false);
    }

    public void setTownName(String townName)
    {
        this.townName = townName;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        return DependencyUtils.hasTowny() && townName.equals(TownyUniverse.getTownName(event.getKiller().getLocation())) && isClearExpOrbs();
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection gain)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();
        ConfigurationSection townyConfig = gain.getConfigurationSection("Towny");

        if (townyConfig != null) {
            boolean defaultClearExpOrbs = gain.getBoolean("Towny.InTown.ClearExpOrbs", false);
            rules = new HashMap<Class<? extends AbstractRule>, Rule>();

            for (String townName : townyConfig.getKeys(false)) {
                if ("InTown".equals(townName)) {
                    continue;
                }

                ConfigurationSection townConfig = townyConfig.getConfigurationSection(townName);

                if (townConfig != null) {
                    TownyRule rule = new TownyRule();
                    rule.setTownName(townName);
                    rule.setClearExpOrbs(townConfig.getBoolean("ClearExpOrbs", defaultClearExpOrbs));
                    rules.put(TownyRule.class, rule);
                }
            }
        }

        return rules;
    }
}
