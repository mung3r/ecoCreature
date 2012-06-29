package se.crafted.chrisb.ecoCreature.utils;

import java.io.IOException;
import java.util.Hashtable;

import org.bukkit.plugin.Plugin;

import se.crafted.chrisb.ecoCreature.Metrics;
import se.crafted.chrisb.ecoCreature.models.ecoReward.RewardType;

public class ecoMetrics extends Metrics
{
    private Hashtable<RewardType, Integer> rewardTypeCount;

    public ecoMetrics(Plugin plugin) throws IOException
    {
        super(plugin);
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
    }

    public void setupGraphs()
    {
        Graph graph = createGraph("Reward Types");

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
