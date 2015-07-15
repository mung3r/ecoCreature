/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature.drops.rules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.palmergames.bukkit.towny.object.TownyUniverse;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class TownyRule extends AbstractEntityRule
{
    private static final String IN_TOWN = "InTown";

    private Map<String, Boolean> townMap;

    public TownyRule()
    {
        townMap = new HashMap<>();
        setClearExpOrbs(true);
    }

    public void addTown(String townName, Boolean clearExpOrbs)
    {
        townMap.put(townName, clearExpOrbs);
    }

    @Override
    protected boolean isBroken(EntityKilledEvent event)
    {
        boolean inTown = false;

        if (DependencyUtils.hasTowny()) {
            String townName = TownyUniverse.getTownName(event.getKiller().getLocation());

            if (townMap.containsKey(townName)) {
                inTown = townMap.get(townName);
            }
            else if (townName != null && townMap.containsKey(IN_TOWN)) {
                inTown = townMap.get(IN_TOWN);
            }
        }

        return inTown && isClearExpOrbs();
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection gain)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();
        ConfigurationSection townyConfig = gain.getConfigurationSection("Towny");

        if (townyConfig != null) {
            TownyRule rule = new TownyRule();

            for (String townName : townyConfig.getKeys(false)) {
                ConfigurationSection townConfig = townyConfig.getConfigurationSection(townName);

                if (townConfig != null) {
                    rule.addTown(townName, townConfig.getBoolean("ClearExpOrbs"));
                }
            }

            rules = new HashMap<>();
            rules.put(TownyRule.class, rule);
        }

        return rules;
    }
}
