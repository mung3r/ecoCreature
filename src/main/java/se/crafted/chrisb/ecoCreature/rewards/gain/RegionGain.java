package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class RegionGain extends DefaultGain
{
    private Map<String, Double> multipliers;

    public RegionGain(Map<String, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        if (DependencyUtils.hasPermission(player, "gain.worldguard") && DependencyUtils.hasWorldGuard()) {
            RegionManager regionManager = DependencyUtils.getRegionManager(player.getWorld());
            if (regionManager != null) {
                Iterator<ProtectedRegion> regions = regionManager.getApplicableRegions(player.getLocation()).iterator();
                while (regions.hasNext()) {
                    String regionName = regions.next().getId();
                    if (multipliers.containsKey(regionName)) {
                        multiplier = multipliers.get(regionName);
                        ECLogger.getInstance().debug("Region multiplier: " + multiplier);
                    }
                }
            }
        }

        return multiplier;
    }

    public static Gain parseConfig(ConfigurationSection config)
    {
        Gain gain = null;

        if (config != null) {
            Map<String, Double> regionMultipliers = new HashMap<String, Double>();
            for (String regionName : config.getKeys(false)) {
                regionMultipliers.put(regionName, Double.valueOf(config.getConfigurationSection(regionName).getDouble("Amount", 1.0D)));
            }
            gain = new RegionGain(regionMultipliers);
        }

        return gain;
    }
}
