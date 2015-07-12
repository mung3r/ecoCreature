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
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCampMessageDecorator;
import se.crafted.chrisb.ecoCreature.drops.SpawnerMobTag;

public class SpawnerMobRule extends AbstractEntityRule
{
    private static final String NO_CAMP_MESSAGE = "&7You find no rewards camping monster spawners.";

    private final boolean canCampSpawner;
    private final boolean campByEntity;

    public SpawnerMobRule(boolean canCampSpawner, boolean campByEntity)
    {
        this.canCampSpawner = canCampSpawner;
        this.campByEntity = campByEntity;
    }

    @Override
    protected boolean isBroken(EntityKilledEvent event)
    {
        SpawnerMobTag tracker = event.getSpawnerMobTracker();
        boolean ruleBroken = !canCampSpawner && campByEntity && tracker.isSpawnerMob(event.getEntity());
        LoggerUtil.getInstance().debugTrue("No reward for " + event.getKiller().getName() + " spawner camping.", ruleBroken);

        return ruleBroken;
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection system)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();

        if (system != null && system.isConfigurationSection("Hunting")) {
            SpawnerMobRule rule = new SpawnerMobRule(system.getBoolean("Hunting.AllowCamping"),
                    system.getBoolean("Hunting.CampingByEntity"));
            rule.setClearDrops(system.getBoolean("Hunting.ClearCampDrops", true));
            rule.setClearExpOrbs(system.getBoolean("Hunting.ClearCampExpOrbs", true));
            rule.setMessage(getNoCampMessage(system));
            rules = new HashMap<>();
            rules.put(SpawnerMobRule.class, rule);
        }

        return rules;
    }

    private static Message getNoCampMessage(ConfigurationSection system)
    {
        NoCampMessageDecorator message = new NoCampMessageDecorator(new DefaultMessage(system.getString("Messages.NoCampMessage", NO_CAMP_MESSAGE), system.getBoolean("Messages.Output")));
        message.setSpawnerCampMessageEnabled(system.getBoolean("Messages.Spawner"));
        return message;
    }
}
