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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCampMessageDecorator;
import se.crafted.chrisb.ecoCreature.drops.categories.SpawnerMobTracker;

public class SpawnerDistanceRule extends AbstractEntityRule
{
    private static final String NO_CAMP_MESSAGE = "&7You find no rewards camping monster spawners.";
    private static final int CAMP_RADIUS = 16;

    private boolean canCampSpawner;
    private boolean campByDistance;
    private int campRadius;

    public SpawnerDistanceRule()
    {
        canCampSpawner = false;
        campByDistance = true;
        campRadius = CAMP_RADIUS;
    }

    public void setCanCampSpawner(boolean canCampSpawner)
    {
        this.canCampSpawner = canCampSpawner;
    }

    public void setCampByDistance(boolean campByDistance)
    {
        this.campByDistance = campByDistance;
    }

    public void setCampRadius(int campRadius)
    {
        this.campRadius = campRadius;
    }

    @Override
    protected boolean isBroken(EntityKilledEvent event)
    {
        SpawnerMobTracker tracker = event.getSpawnerMobTracker();
        boolean ruleBroken = !canCampSpawner && campByDistance && tracker.isSpawnerMob(event.getEntity()) && isEntityKilledEventNearSpawner(event);
        LoggerUtil.getInstance().debugTrue("No reward for " + event.getKiller().getName() + " spawner camping.", ruleBroken);

        return ruleBroken;
    }

    private boolean isEntityKilledEventNearSpawner(EntityKilledEvent event)
    {
        return EntityUtils.isNearSpawner(event.getKiller(), campRadius) || EntityUtils.isNearSpawner(event.getEntity(), campRadius);
    }

    public static Map<Class<? extends AbstractRule>, Rule> parseConfig(ConfigurationSection system)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = Collections.emptyMap();

        if (system != null && system.isConfigurationSection("Hunting")) {
            SpawnerDistanceRule rule = new SpawnerDistanceRule();
            rule.setCanCampSpawner(system.getBoolean("Hunting.AllowCamping", false));
            rule.setClearDrops(system.getBoolean("Hunting.ClearCampDrops", true));
            rule.setClearExpOrbs(system.getBoolean("Hunting.ClearCampExpOrbs", true));
            rule.setCampByDistance(system.getBoolean("Hunting.CampingByDistance", true));
            rule.setCampRadius(system.getInt("Hunting.CampRadius", CAMP_RADIUS));
            rule.setMessage(getNoCampMessage(system));
            rules = new HashMap<Class<? extends AbstractRule>, Rule>();
            rules.put(SpawnerDistanceRule.class, rule);
        }

        return rules;
    }

    private static Message getNoCampMessage(ConfigurationSection config)
    {
        NoCampMessageDecorator message = new NoCampMessageDecorator(new DefaultMessage(config.getString("Messages.NoCampMessage", NO_CAMP_MESSAGE)));
        message.setSpawnerCampMessageEnabled(config.getBoolean("Messages.Spawner", false));
        return message;
    }
}
