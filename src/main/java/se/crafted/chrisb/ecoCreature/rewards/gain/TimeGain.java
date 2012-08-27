package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.commons.TimePeriod;

public class TimeGain extends BasicGain
{
    Map<TimePeriod, Double> multipliers;

    public TimeGain(Map<TimePeriod, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        if (DependencyUtils.hasPermission(player, "gain.time") && multipliers.containsKey(TimePeriod.fromEntity(player))) {
            multiplier = multipliers.get(TimePeriod.fromEntity(player));
            ECLogger.getInstance().debug("Time multiplier: " + multiplier);
        }

        return multiplier;
    }

    public static Gain parseConfig(ConfigurationSection config)
    {
        Gain gain = null;

        if (config != null) {
            Map<TimePeriod, Double> timeMultipliers = new HashMap<TimePeriod, Double>();
            for (String period : config.getKeys(false)) {
                timeMultipliers.put(TimePeriod.fromName(period), Double.valueOf(config.getConfigurationSection(period).getDouble("Amount", 1.0D)));
            }
            gain = new TimeGain(timeMultipliers);
        }

        return gain;
    }
}
