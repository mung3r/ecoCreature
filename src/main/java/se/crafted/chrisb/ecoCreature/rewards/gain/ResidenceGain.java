package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class ResidenceGain extends DefaultGain
{
    private Map<String, Double> multipliers;

    public ResidenceGain(Map<String, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        if (DependencyUtils.hasPermission(player, "gain.residence") && DependencyUtils.hasResidence()) {
            ClaimedResidence residence = Residence.getResidenceManager().getByLoc(player.getLocation());
            if (residence != null && multipliers.containsKey(residence.getName())) {
                multiplier = multipliers.get(residence.getName());
                ECLogger.getInstance().debug("Residence multiplier: " + multiplier);
            }
        }

        return multiplier;
    }

    public static Set<Gain> parseConfig(ConfigurationSection config)
    {
        Set<Gain> gain = new HashSet<Gain>();

        if (config != null) {
            Map<String, Double> multipliers = new HashMap<String, Double>();
            for (String residenceName : config.getKeys(false)) {
                multipliers.put(residenceName, Double.valueOf(config.getConfigurationSection(residenceName).getDouble("Amount", 1.0D)));
            }
            gain.add(new ResidenceGain(multipliers));
        }

        return gain;
    }
}
