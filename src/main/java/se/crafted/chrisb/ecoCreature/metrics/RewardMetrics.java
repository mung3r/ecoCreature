package se.crafted.chrisb.ecoCreature.metrics;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.metrics.Metrics.Graph;
import se.crafted.chrisb.ecoCreature.metrics.Metrics.Plotter;
import se.crafted.chrisb.ecoCreature.rewards.sources.RewardSourceType;

public class RewardMetrics
{
    private Metrics metrics;
    private Map<RewardSourceType, Integer> typeCount;

    @SuppressWarnings("serial")
    public RewardMetrics(Plugin plugin)
    {
        typeCount = new Hashtable<RewardSourceType, Integer>() {
            @Override
            public synchronized Integer get(Object key)
            {
                if (!super.containsKey(key)) {
                    return Integer.valueOf(0);
                }
                return super.get(key);
            };
        };

        try {
            metrics = new Metrics(plugin);
            setupGraphs();
            metrics.start();
        }
        catch (IOException e) {
            ECLogger.getInstance().warning("Metrics failed to load.");
        }
    }

    public void setupGraphs()
    {
        Graph graph = metrics.createGraph("Reward Types");

        for (RewardSourceType rewardType : RewardSourceType.values()) {

            graph.addPlotter(new Plotter(rewardType.getName()) {
                @Override
                public int getValue()
                {
                    Integer count = typeCount.get(RewardSourceType.fromName(getColumnName()));
                    typeCount.put(RewardSourceType.fromName(getColumnName()), Integer.valueOf(0));
                    return count;
                }
            });
        }
    }

    public void addCount(RewardSourceType type)
    {
        typeCount.put(type, typeCount.get(type) + 1);
    }
}
