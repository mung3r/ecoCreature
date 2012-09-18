package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyGain extends DefaultGain
{
    private Map<String, Double> multipliers;

    public TownyGain(Map<String, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        if (DependencyUtils.hasPermission(player, "gain.towny") && DependencyUtils.hasTowny()) {
            String townName = TownyUniverse.getTownName(player.getLocation());
            if (townName != null && multipliers.containsKey(townName)) {
                multiplier = multipliers.get(townName);
                ECLogger.getInstance().debug("Towny multiplier: " + multiplier);
            }
        }

        return multiplier;
    }

    public static Set<Gain> parseConfig(ConfigurationSection config)
    {
        Set<Gain> gain = new HashSet<Gain>();

        if (config != null) {
            Map<String, Double> multipliers = new HashMap<String, Double>();
            for (String townName : config.getKeys(false)) {
                multipliers.put(townName, Double.valueOf(config.getConfigurationSection(townName).getDouble("Amount", 1.0D)));
            }
            gain.add(new TownyGain(multipliers));
        }

        return gain;
    }
}
