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

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;

public class ProjectileRule extends AbstractRule
{
    private static final String NO_BOW_REWARD_MESSAGE = "&7You find no rewards on this creature.";

    private boolean bowRewards;

    public ProjectileRule()
    {
        bowRewards = true;
    }

    public void setBowRewards(boolean bowRewards)
    {
        this.bowRewards = bowRewards;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = !bowRewards && event.isProjectileKill();

        if (ruleBroken) {
            LoggerUtil.getInstance().debug("No reward for " + event.getKiller().getName() + " using projectiles.");
        }

        return ruleBroken;
    }

    public static Set<Rule> parseConfig(ConfigurationSection config)
    {
        Set<Rule> rules = Collections.emptySet();

        if (config != null) {
            ProjectileRule rule = new ProjectileRule();
            rule.setBowRewards(config.getBoolean("System.Hunting.BowRewards", true));
            rule.setMessage(new DefaultMessage(config.getString("System.Messages.NoBowMessage", NO_BOW_REWARD_MESSAGE)));
            rules = new HashSet<Rule>();
            rules.add(rule);
        }

        return rules;
    }
}
