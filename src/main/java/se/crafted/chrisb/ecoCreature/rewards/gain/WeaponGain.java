package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;

public class WeaponGain extends DefaultGain
{
    private Map<Material, Double> multipliers;

    public WeaponGain(Map<Material, Double> materialMultipliers)
    {
        this.multipliers = materialMultipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;
        Material material = EntityUtils.getItemTypeInHand(player);

        if (DependencyUtils.hasPermission(player, "gain.weapon") && multipliers.containsKey(material)) {
            multiplier = multipliers.get(material);
            ECLogger.getInstance().debug("Weapon multiplier: " + multiplier);
        }

        return multiplier;
    }

    public static Gain parseConfig(ConfigurationSection config)
    {
        Gain gain = null;

        if (config != null) {
            Map<Material, Double> multipliers = new HashMap<Material, Double>();

            for (String material : config.getKeys(false)) {
                multipliers.put(Material.matchMaterial(material), Double.valueOf(config.getConfigurationSection(material).getDouble("Amount", 1.0)));
            }

            gain = new WeaponGain(multipliers);
        }

        return gain;
    }
}
