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

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import se.crafted.chrisb.ecoCreature.commons.PluginUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;

public class SimpleClansRule extends AbstractPlayerRule
{
    public SimpleClansRule(boolean clearNonRivalDrops)
    {
        setClearDrops(clearNonRivalDrops);
        setClearExpOrbs(clearNonRivalDrops);
    }

    @Override
    protected boolean isBroken(PlayerKilledEvent event)
    {
        boolean ruleBroken = false;

        if (PluginUtils.hasSimpleClans()) {
            ClanManager clanManager = SimpleClans.getInstance().getClanManager();
            ClanPlayer killer = clanManager.getClanPlayer(event.getKiller());
            ruleBroken = killer != null && !killer.isRival(event.getVictim());
            LoggerUtil.getInstance().debug(event.getVictim().getName() + " is a clan rival of " + event.getKiller().getName() + ": " + ruleBroken);
        }

        return ruleBroken;
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection system)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();

        if (system != null && system.isConfigurationSection("Hunting")) {
            SimpleClansRule rule = new SimpleClansRule(system.getBoolean("Hunting.SimpleClans.ClearNonRivalDrops"));
            rules = new HashMap<>();
            rules.put(SimpleClansRule.class, rule);
        }

        return rules;
    }
}
