package se.crafted.chrisb.ecoCreature.metrics;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.metrics.Metrics.Graph;
import se.crafted.chrisb.ecoCreature.metrics.Metrics.Plotter;

public class RewardMetrics
{
    private Map<String, Integer> typeCount;
    private Metrics metrics;
    private Graph graph;

    public RewardMetrics(Plugin plugin)
    {
        try {
            metrics = new Metrics(plugin);
        }
        catch (IOException e) {
            ECLogger.getInstance().warning("Metrics failed to load.");
        }

        if (metrics != null) {
            typeCount = new Hashtable<String, Integer>();
            graph = metrics.createGraph("Reward Types");
            metrics.start();
        }
    }

    public void addCount(String name)
    {
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
        }
    }
}
