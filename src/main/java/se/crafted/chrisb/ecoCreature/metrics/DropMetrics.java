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
package se.crafted.chrisb.ecoCreature.metrics;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;
import org.mcstats.Metrics.Plotter;

public class DropMetrics
{
    private Map<String, Integer> typeCount;
    private Graph graph;

    public DropMetrics(Plugin plugin)
    {
        Metrics metrics;

        try {
            metrics = new Metrics(plugin);
            typeCount = new Hashtable<>();
            graph = metrics.createGraph("Reward Types");
            metrics.start();
        }
        catch (IOException e) {
            LoggerUtil.getInstance().warning("Metrics failed to load.");
        }
    }

    public void addCount(String name)
    {
        if (name == null) {
            LoggerUtil.getInstance().warning("Null type name passed into metrics.");
            return;
        }

        if (graph != null) {
            if (!typeCount.containsKey(name)) {
                typeCount.put(name, 0);
                graph.addPlotter(new Plotter(name) {
                    @Override
                    public int getValue()
                    {
                        return typeCount.get(getColumnName());
                    }
                });
            }

            typeCount.put(name, typeCount.get(name) + 1);
            LoggerUtil.getInstance().debug("Added metrics count for " + name);
        }
    }
}
