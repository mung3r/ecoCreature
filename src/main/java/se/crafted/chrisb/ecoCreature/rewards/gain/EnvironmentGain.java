package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class EnvironmentGain extends DefaultGain
{
    private Map<Environment, Double> multipliers;

    public EnvironmentGain(Map<Environment, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        if (DependencyUtils.hasPermission(player, "gain.environment") && multipliers.containsKey(player.getWorld().getEnvironment())) {
            multiplier = multipliers.get(player.getWorld().getEnvironment());
            ECLogger.getInstance().debug("Environment multiplier: " + multiplier);
        }

        return multiplier;
    }

    public static Gain parseConfig(ConfigurationSection config)
    {
        Gain gain = null;

        if (config != null) {
            Map<Environment, Double> multipliers = new HashMap<World.Environment, Double>();
            for (String environment : config.getKeys(false)) {
                try {
                    multipliers.put(Environment.valueOf(environment.toUpperCase()), Double.valueOf(config.getConfigurationSection(environment).getDouble("Amount", 1.0D)));
                }
                catch (Exception e) {
                    ECLogger.getInstance().warning("Skipping unknown environment name: " + environment);
                }
            }
            gain = new EnvironmentGain(multipliers);
        }

        return gain;
    }
}
