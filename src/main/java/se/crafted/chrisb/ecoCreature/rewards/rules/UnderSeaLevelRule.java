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
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;

public class UnderSeaLevelRule extends AbstractRule
{
    private static final String NO_UNDER_SEA_LEVEL_MESSAGE = "&7You find no rewards on this creature.";

    private boolean huntUnderSeaLevel;

    public UnderSeaLevelRule()
    {
        huntUnderSeaLevel = true;
    }

    public void setHuntUnderSeaLevel(boolean huntUnderSeaLevel)
    {
        this.huntUnderSeaLevel = huntUnderSeaLevel;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = !huntUnderSeaLevel && EntityUtils.isUnderSeaLevel(event.getKiller());

        if (ruleBroken) {
            LoggerUtil.getInstance().debug(this.getClass(), "No reward for " + event.getKiller().getName() + " killing under sea level.");
        }

        return ruleBroken;
    }

    public static Set<Rule> parseConfig(ConfigurationSection config)
    {
        Set<Rule> rules = Collections.emptySet();

        if (config != null) {
            UnderSeaLevelRule rule = new UnderSeaLevelRule();
            rule.setHuntUnderSeaLevel(config.getBoolean("System.Hunting.AllowUnderSeaLVL", true));
            rule.setMessage(new DefaultMessage(config.getString("System.Messages.NoUnderSeaLevel", NO_UNDER_SEA_LEVEL_MESSAGE)));
            rules = new HashSet<Rule>();
            rules.add(rule);
        }

        return rules;
    }
}
