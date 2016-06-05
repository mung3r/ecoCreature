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
package se.crafted.chrisb.ecoCreature.drops.gain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.PluginUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

public class RegionGain extends AbstractPlayerGain<String>
{
    public RegionGain(Map<String, Double> multipliers)
    {
        super(multipliers, "gain.worldguard");
    }

    @Override
    public boolean hasPermission(Player player)
    {
        return super.hasPermission(player) && PluginUtils.hasWorldGuard();
    }

    @Override
    public double getGain(Player player)
    {
        double multiplier = NO_GAIN;
        try {
            Object regionManager = PluginUtils.getRegionManager(player.getWorld());
            if (regionManager != null) {
                Method applicableRegionsMethod = regionManager.getClass().getMethod("getApplicableRegions", Location.class);
                Object applicableRegionSet = applicableRegionsMethod.invoke(regionManager, player.getLocation());
                if (applicableRegionSet instanceof Iterable<?>) {
                    for (Object protectedRegion : (Iterable<?>) applicableRegionSet) {
                        Method idMethod = protectedRegion.getClass().getMethod("getId");
                        Object id = idMethod.invoke(protectedRegion);
                        if (id instanceof String) {
                            multiplier = getMultiplier((String) id);
                        }
                    }
                }
            }
        }
        catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
            LoggerUtil.getInstance().warning("Incompatible version of WorldGuard");
        }

        LoggerUtil.getInstance().debug("Gain: " + multiplier);
        return multiplier;
    }

    public static Collection<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Collection<PlayerGain> gain = Collections.emptyList();

        if (config != null) {
            gain = new ArrayList<>();
            gain.add(new RegionGain(parseMultipliers(config)));
        }

        return gain;
    }
}
