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
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class MobArenaRule extends AbstractRule
{
    private boolean mobArenaRewards;

    public MobArenaRule()
    {
        mobArenaRewards = false;
    }

    public void setMobArenaRewards(boolean mobArenaRewards)
    {
        this.mobArenaRewards = mobArenaRewards;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = !mobArenaRewards && DependencyUtils.hasMobArena() && DependencyUtils.getMobArenaHandler().isPlayerInArena(event.getKiller());

        if (ruleBroken) {
            LoggerUtil.getInstance().debug(this.getClass(), "No reward for " + event.getKiller().getName() + " in Mob Arena.");
        }

        return ruleBroken;
    }

    public static Set<Rule> parseConfig(ConfigurationSection config)
    {
        Set<Rule> rules = Collections.emptySet();

        if (config != null) {
            MobArenaRule rule = new MobArenaRule();
            rule.setMobArenaRewards(config.getBoolean("System.Hunting.MobArenaRewards"));
            rules = new HashSet<Rule>();
            rules.add(rule);
        }

        return rules;
    }
}
