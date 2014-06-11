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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class HeroClassNoDropRule extends AbstractRule
{
    private List<String> classNames;

    public HeroClassNoDropRule()
    {
        classNames = new ArrayList<String>();
        setClearDrops(true);
        setClearExpOrbs(true);
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = false;

        if (DependencyUtils.hasHeroes()) {
            for (String className : classNames) {
                if (DependencyUtils.getHeroes().getCharacterManager().getHero(event.getKiller()).getHeroClass().getName().equals(className)) {
                    ruleBroken = true;
                    LoggerUtil.getInstance().debugTrue("No reward for " + event.getKiller().getName() + " of hero class " + className, ruleBroken);
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
        List<String> classNames = system.getStringList("Hunting.ClearDropsOnHeroClass");
        HeroClassNoDropRule rule = new HeroClassNoDropRule();
        rule.setClassNames(classNames);
        rules = new HashMap<Class<? extends AbstractRule>, Rule>();
        rules.put(HeroClassNoDropRule.class, rule);
        return rules;
    }
}
