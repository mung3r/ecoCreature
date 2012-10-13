package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.commons.TimePeriod;

public class TimeGain extends AbstractPlayerGain
{
    private Map<TimePeriod, Double> multipliers;

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
            ECLogger.getInstance().debug(this.getClass(), "Time multiplier: " + multiplier);
        }

        return multiplier;
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            Map<TimePeriod, Double> multipliers = new HashMap<TimePeriod, Double>();
            for (String period : config.getKeys(false)) {
                multipliers.put(TimePeriod.fromName(period), Double.valueOf(config.getConfigurationSection(period).getDouble("Amount", 1.0D)));
            }
            gain = new HashSet<PlayerGain>();
            gain.add(new TimeGain(multipliers));
        }

        return gain;
    }
}
