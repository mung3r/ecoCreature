package se.crafted.chrisb.ecoCreature.metrics;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.metrics.Metrics.Graph;
import se.crafted.chrisb.ecoCreature.metrics.Metrics.Plotter;
import se.crafted.chrisb.ecoCreature.rewards.RewardType;

public class MetricsManager
{
    private Metrics metrics;
    private Map<RewardType, Integer> rewardTypeCount;

    @SuppressWarnings("serial")
    public MetricsManager(Plugin plugin)
    {
        rewardTypeCount = new Hashtable<RewardType, Integer>() {
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

        for (RewardType rewardType : RewardType.values()) {

            graph.addPlotter(new Plotter(rewardType.getName()) {
                @Override
                public int getValue()
                {
                    Integer count = rewardTypeCount.get(RewardType.fromName(getColumnName()));
                    rewardTypeCount.put(RewardType.fromName(getColumnName()), Integer.valueOf(0));
                    return count;
                }
            });
        }
    }

    public void addCount(RewardType rewardType)
    {
        rewardTypeCount.put(rewardType, rewardTypeCount.get(rewardType) + 1);
    }
}
