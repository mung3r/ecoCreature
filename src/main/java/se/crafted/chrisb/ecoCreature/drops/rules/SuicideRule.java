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

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;

public class SuicideRule extends AbstractPlayerRule
{
    private static final String NO_SUICIDE_REWARD_MESSAGE = "&7You find no reward taking your own life.";

    private final boolean suicideRewards;

    public SuicideRule(boolean suicideRewards)
    {
        this.suicideRewards = suicideRewards;
    }

    @Override
    protected boolean isBroken(PlayerKilledEvent event)
    {
        boolean ruleBroken = !suicideRewards && event.isSuicide();
        LoggerUtil.getInstance().debugTrue("No reward for " + event.getKiller().getName() + " taking their life.", ruleBroken);

        return ruleBroken;
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection system)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();

        if (system != null && system.isConfigurationSection("Hunting")) {
            SuicideRule rule = new SuicideRule(system.getBoolean("Hunting.SuicideReward", false));
            rule.setMessage(new DefaultMessage(system.getString("Messages.NoSuicideMessage", NO_SUICIDE_REWARD_MESSAGE), system.getBoolean("Messages.Output")));
            rules = new HashMap<>();
            rules.put(SuicideRule.class, rule);
        }

        return rules;
    }
}
