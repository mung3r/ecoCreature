package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import couk.Adamki11s.Regios.Regions.Region;

public class RegiosGain extends BasicGain
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

    public static Gain parseConfig(ConfigurationSection config)
    {
        Gain gain = null;

        if (config != null) {
            Map<String, Double> regiosMultipliers = new HashMap<String, Double>();
            for (String regionName : config.getKeys(false)) {
                regiosMultipliers.put(regionName, Double.valueOf(config.getConfigurationSection(regionName).getDouble("Amount", 1.0D)));
            }
            gain = new RegiosGain(regiosMultipliers);
        }

        return gain;
    }
}
