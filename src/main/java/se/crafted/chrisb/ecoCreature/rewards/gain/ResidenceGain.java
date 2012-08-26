package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class ResidenceGain extends BasicGain
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

    public static Gain parseConfig(ConfigurationSection config)
    {
        Gain gain = null;

        if (config != null) {
            Map<String, Double> residenceMultipliers = new HashMap<String, Double>();
            for (String residenceName : config.getKeys(false)) {
                residenceMultipliers.put(residenceName, Double.valueOf(config.getConfigurationSection(residenceName).getDouble("Amount", 1.0D)));
            }
            gain = new ResidenceGain(residenceMultipliers);
        }

        return gain;
    }
}
