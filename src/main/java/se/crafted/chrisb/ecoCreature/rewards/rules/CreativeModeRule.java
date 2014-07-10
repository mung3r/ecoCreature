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
package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class CreativeModeRule extends AbstractEntityRule
{
    private boolean creativeModeRewards;

    public CreativeModeRule()
    {
        creativeModeRewards = false;
    }

    public void setCreativeModeRewards(boolean creativeModeRewards)
    {
        this.creativeModeRewards = creativeModeRewards;
    }

    @Override
    protected boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = !creativeModeRewards && event.getKiller().getGameMode() == GameMode.CREATIVE;
        LoggerUtil.getInstance().debugTrue("No reward for " + event.getKiller().getName() + " in creative mode.", ruleBroken);

        return ruleBroken;
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection system)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();

        if (system != null && system.getConfigurationSection("Hunting") != null) {
            CreativeModeRule rule = new CreativeModeRule();
            rule.setCreativeModeRewards(system.getBoolean("Hunting.CreativeModeRewards", false));
            rules = new HashMap<Class<? extends AbstractRule>, Rule>();
            rules.put(CreativeModeRule.class, rule);
        }

        return rules;
    }
}
