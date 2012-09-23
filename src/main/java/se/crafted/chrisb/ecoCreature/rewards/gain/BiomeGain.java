package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class BiomeGain extends DefaultGain
{
    private Map<Biome, Double> multipliers;

    public BiomeGain(Map<Biome, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        if (DependencyUtils.hasPermission(player, "gain.biome") && multipliers.containsKey(getBiome(player))) {
            multiplier = multipliers.get(getBiome(player));
            ECLogger.getInstance().debug("Biome multiplier: " + multiplier);
        }

        return multiplier;
    }

    private static Biome getBiome(Player player)
    {
        return player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockY());
    }

    public static Set<Gain> parseConfig(ConfigurationSection config)
    {
        Set<Gain> gain = new HashSet<Gain>();

        if (config != null) {
            Map<Biome, Double> multipliers = new HashMap<Biome, Double>();
            for (String biome : config.getKeys(false)) {
                try {
                    multipliers.put(Biome.valueOf(biome.toUpperCase()), Double.valueOf(config.getConfigurationSection(biome).getDouble("Amount", 1.0D)));
                }
                catch (Exception e) {
                    ECLogger.getInstance().warning("Skipping unknown biome name: " + biome);
                }
            }
            gain.add(new BiomeGain(multipliers));
        }

        return gain;
    }
}
