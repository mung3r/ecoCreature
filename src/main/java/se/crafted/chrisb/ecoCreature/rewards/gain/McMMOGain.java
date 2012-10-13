package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class McMMOGain extends AbstractPlayerGain
{
    private double multiplier;

    public McMMOGain(double multiplier)
    {
        this.multiplier = multiplier;
    }

    @Override
    public double getMultiplier(Player player)
    {
        if (DependencyUtils.hasPermission(player, "gain.mcmmo") && DependencyUtils.hasMcMMO()) {
            ECLogger.getInstance().debug(this.getClass(), "mcMMO multiplier applied");
            return multiplier;
        }

        return NO_GAIN;
    }

    public static Set<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Set<PlayerGain> gain = Collections.emptySet();

        if (config != null) {
            gain = new HashSet<PlayerGain>();
            gain.add(new McMMOGain(config.getDouble("Amount", 1.0D)));
        }

        return gain;
    }
}
