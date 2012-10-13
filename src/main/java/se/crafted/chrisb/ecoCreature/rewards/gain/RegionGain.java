package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class RegionGain extends AbstractPlayerGain
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
                        ECLogger.getInstance().debug(this.getClass(), "Region multiplier: " + multiplier);
                    }
                }
            }
        }

        return multiplier;
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            Map<String, Double> multipliers = new HashMap<String, Double>();
            for (String regionName : config.getKeys(false)) {
                multipliers.put(regionName, Double.valueOf(config.getConfigurationSection(regionName).getDouble("Amount", 1.0D)));
            }
            gain = new HashSet<PlayerGain>();
            gain.add(new RegionGain(multipliers));
        }

        return gain;
    }
}
