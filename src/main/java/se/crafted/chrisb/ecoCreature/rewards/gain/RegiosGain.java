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
import couk.Adamki11s.Regios.Regions.Region;

public class RegiosGain extends AbstractGain
{
    private Map<String, Double> multipliers;

    public RegiosGain(Map<String, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        if (DependencyUtils.hasPermission(player, "gain.regios") && DependencyUtils.hasRegios()) {
            Region region = DependencyUtils.getRegiosAPI().getRegion(player.getLocation());
            if (region != null && multipliers.containsKey(region.getName())) {
                multiplier = multipliers.get(region.getName());
                ECLogger.getInstance().debug("Regios multiplier: " + multiplier);
            }
        }

        return multiplier;
    }

    public static Set<Gain> parseConfig(ConfigurationSection config)
    {
        Set<Gain> gain = Collections.emptySet();

        if (config != null) {
            Map<String, Double> multipliers = new HashMap<String, Double>();
            for (String regionName : config.getKeys(false)) {
                multipliers.put(regionName, Double.valueOf(config.getConfigurationSection(regionName).getDouble("Amount", 1.0D)));
            }
            gain = new HashSet<Gain>();
            gain.add(new RegiosGain(multipliers));
        }

        return gain;
    }
}
