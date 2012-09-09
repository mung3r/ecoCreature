package se.crafted.chrisb.ecoCreature.rewards.gain;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class McMMOGain extends DefaultGain
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
            ECLogger.getInstance().debug("mcMMO multiplier applied");
            return multiplier;
        }

        return super.getMultiplier(player);
    }

    public static Gain parseConfig(ConfigurationSection config)
    {
        Gain gain = null;

        if (config != null) {
            gain = new McMMOGain(config.getDouble("Amount", 1.0D));
        }

        return gain;
    }
}
