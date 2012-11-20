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
import se.crafted.chrisb.ecoCreature.settings.SpawnerMobTracking;

public class SpawnerDistanceRule extends AbstractRule
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
    public boolean isBroken(EntityKilledEvent event)
    {
        SpawnerMobTracking tracking = event.getSpawnerMobTracking();
        boolean ruleBroken = !canCampSpawner && campByDistance && tracking.isSpawnerMob(event.getEntity()) && isEntityKilledEventNearSpawner(event);

        if (ruleBroken) {
            LoggerUtil.getInstance().debug(this.getClass(), "No reward for " + event.getKiller().getName() + " spawner camping.");
        }

        return ruleBroken;
    }

    private boolean isEntityKilledEventNearSpawner(EntityKilledEvent event)
    {
        return EntityUtils.isNearSpawner(event.getKiller(), campRadius) || EntityUtils.isNearSpawner(event.getEntity(), campRadius);
    }

    public static Set<Rule> parseConfig(ConfigurationSection config)
    {
        Set<Rule> rules = Collections.emptySet();

        if (config != null) {
            SpawnerDistanceRule rule = new SpawnerDistanceRule();
            rule.setCanCampSpawner(config.getBoolean("System.Hunting.AllowCamping", false));
            rule.setClearDrops(config.getBoolean("System.Hunting.ClearCampDrops", true));
            rule.setCampByDistance(config.getBoolean("System.Hunting.CampingByDistance", true));
            rule.setCampRadius(config.getInt("System.Hunting.CampRadius", CAMP_RADIUS));
            rule.setMessage(new DefaultMessage(config.getString("System.Messages.NoCampMessage", NO_CAMP_MESSAGE)));
            rules = new HashSet<Rule>();
            rules.add(rule);
        }

        return rules;
    }
}
