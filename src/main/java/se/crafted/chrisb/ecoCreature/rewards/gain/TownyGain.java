package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyGain extends BasicGain
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

    public static Gain parseConfig(ConfigurationSection config)
    {
        Gain gain = null;

        if (config != null) {
            Map<String, Double> townyMultipliers = new HashMap<String, Double>();
            for (String townName : config.getKeys(false)) {
                townyMultipliers.put(townName, Double.valueOf(config.getConfigurationSection(townName).getDouble("Amount", 1.0D)));
            }
            gain = new TownyGain(townyMultipliers);
        }

        return gain;
    }
}
