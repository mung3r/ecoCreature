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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.configuration.ConfigurationSection;

import com.herocraftonline.heroes.characters.Hero;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.commons.PluginUtils;
import se.crafted.chrisb.ecoCreature.drops.chances.AbstractChance;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

public class HeroesRule extends AbstractPlayerRule
{
    private final List<String> classNames;
    private final Map<Integer, NumberRange> tiers;

    public HeroesRule(List<String> classNames, Map<Integer, NumberRange> tiers)
    {
        this.classNames = classNames;
        this.tiers = tiers;
        setClearDrops(true);
        setClearExpOrbs(true);
    }

    @Override
    protected boolean isBroken(PlayerKilledEvent event)
    {
        boolean ruleBroken = false;

        if (PluginUtils.hasHeroes()) {
            Hero victim = PluginUtils.getHeroes().getCharacterManager().getHero(event.getVictim());

            for (String className : classNames) {
                if (victim.getHeroClass().getName().equals(className)) {
                    ruleBroken |= true;
                    LoggerUtil.getInstance().debug("No reward for " + event.getKiller().getName() + " who killed a " + className);
                    break;
                }
            }

            Integer tier = victim.getHeroClass().getTier();
            Integer level = victim.getLevel(victim.getHeroClass());
            if (tiers.containsKey(tier) && tiers.get(tier).containsInteger(level)) {
                ruleBroken |= true;
                LoggerUtil.getInstance().debug("No reward for " + event.getKiller().getName() + " who killed a tier " + tier + " level " + level);
            }
        }

        return ruleBroken;
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection system)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();

        if (system != null && system.isConfigurationSection("Hunting")) {
            List<String> classNames = system.getStringList("Hunting.Heroes.ClearDrops.Classes");
            Map<Integer, NumberRange> tiers = new HashMap<>();

            if (system.getList("Hunting.Heroes.ClearDrops.Tiers") != null) {
                for (Object obj : system.getList("Hunting.Heroes.ClearDrops.Tiers")) {
                    if (obj instanceof LinkedHashMap) {
                        try {
                            ConfigurationSection tiersConfig = AbstractChance.createMemoryConfig(obj);
                            Integer tier = tiersConfig.getInt("Tier");
                            String levels = tiersConfig.getString("Levels");
                            String[] range = levels.split("-");
                            NumberRange levelRange = new NumberRange(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
                            tiers.put(tier, levelRange);
                        }
                        catch (Exception e) {
                            LoggerUtil.getInstance().severe("Syntax error parsing Hunting.Heroes.ClearDrops.Tiers");
                        }
                    }
                }
            }

            if (!classNames.isEmpty() || !tiers.isEmpty()) {
                HeroesRule rule = new HeroesRule(classNames, tiers);
                rules = new HashMap<>();
                rules.put(HeroesRule.class, rule);
            }
        }

        return rules;
    }
}
