package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;

public class WeaponGain extends AbstractGain
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

    public static Set<Gain> parseConfig(ConfigurationSection config)
    {
        Set<Gain> gain = Collections.emptySet();

        if (config != null) {
            Map<Material, Double> multipliers = new HashMap<Material, Double>();

            for (String material : config.getKeys(false)) {
                multipliers.put(Material.matchMaterial(material), Double.valueOf(config.getConfigurationSection(material).getDouble("Amount", 1.0)));
            }
            gain = new HashSet<Gain>();
            gain.add(new WeaponGain(multipliers));
        }

        return gain;
    }
}
