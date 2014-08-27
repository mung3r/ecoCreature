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
package se.crafted.chrisb.ecoCreature.drops.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

public class HeroesRule extends AbstractPlayerRule
{
    private List<String> classNames;

    public HeroesRule()
    {
        classNames = new ArrayList<String>();
        setClearDrops(true);
        setClearExpOrbs(true);
    }

    @Override
    protected boolean isBroken(PlayerKilledEvent event)
    {
        boolean ruleBroken = false;

        if (DependencyUtils.hasHeroes()) {
            for (String className : classNames) {
                if (DependencyUtils.getHeroes().getCharacterManager().getHero(event.getVictim()).getHeroClass().getName().equals(className)) {
                    ruleBroken = true;
                    LoggerUtil.getInstance().debug("No reward for " + event.getKiller().getName() + " of hero class " + className);
                    break;
                }
            }
        }

        return ruleBroken;
    }

    public void setClassNames(List<String> classNames)
    {
        this.classNames = classNames;
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection system)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();

        if (system != null && system.isConfigurationSection("Hunting")) {
            List<String> classNames = system.getStringList("Hunting.Heroes.ClearClassDropsList");
            HeroesRule rule = new HeroesRule();
            rule.setClassNames(classNames);
            rules = new HashMap<Class<? extends AbstractRule>, Rule>();
            rules.put(HeroesRule.class, rule);
        }

        return rules;
    }
}
