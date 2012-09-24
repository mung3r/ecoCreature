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

public class WeatherGain extends AbstractGain
{
    public enum WEATHER
    {
        STORMY, SUNNY;

        public static WEATHER fromBoolean(boolean hasStorm)
        {
            return hasStorm ? STORMY : SUNNY;
        }

        public static WEATHER fromName(String name)
        {
            for (WEATHER weather : WEATHER.values()) {
                if (weather.toString().equalsIgnoreCase(name)) {
                    return weather;
                }
            }

            return null;
        }
    }

    private Map<WEATHER, Double> multipliers;

    public WeatherGain(Map<WEATHER, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;
        WEATHER weather = WEATHER.fromBoolean(player.getWorld().hasStorm());

        if (DependencyUtils.hasPermission(player, "gain.weather") && multipliers.containsKey(weather)) {
            multiplier = multipliers.get(weather);
            ECLogger.getInstance().debug("Weather multiplier: " + multiplier);
        }

        return multiplier;
    }

    public static Set<Gain> parseConfig(ConfigurationSection config)
    {
        Set<Gain> gain = Collections.emptySet();

        if (config != null) {
            Map<WEATHER, Double> multipliers = new HashMap<WEATHER, Double>();
            for (String weather : config.getKeys(false)) {
                try {
                    multipliers.put(WEATHER.valueOf(weather.toUpperCase()), Double.valueOf(config.getConfigurationSection(weather).getDouble("Amount", 1.0D)));
                }
                catch (Exception e) {
                    ECLogger.getInstance().warning("Skipping unknown weather name: " + weather);
                }
            }
            gain = new HashSet<Gain>();
            gain.add(new WeatherGain(multipliers));
        }

        return gain;
    }
}
