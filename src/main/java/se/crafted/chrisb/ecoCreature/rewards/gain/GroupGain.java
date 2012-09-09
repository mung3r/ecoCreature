package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class GroupGain extends DefaultGain
{
    private boolean warnGroupMultiplierSupport;
    private Map<String, Double> multipliers;

    public GroupGain(Map<String, Double> multipliers)
    {
        warnGroupMultiplierSupport = true;
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        try {
            if (DependencyUtils.hasPermission() && DependencyUtils.getPermission().getPrimaryGroup(player.getWorld().getName(), player.getName()) != null) {
                String group = DependencyUtils.getPermission().getPrimaryGroup(player.getWorld().getName(), player.getName()).toLowerCase();
                if (DependencyUtils.hasPermission(player, "gain.group") && multipliers.containsKey(group)) {
                    multiplier = multipliers.get(group);
                    ECLogger.getInstance().debug("Group multiplier: " + multiplier);
                }
            }
        }
        catch (UnsupportedOperationException e) {
            if (warnGroupMultiplierSupport) {
                ECLogger.getInstance().warning(e.getMessage());
                warnGroupMultiplierSupport = false;
            }
        }

        return multiplier;
    }

    public static Gain parseConfig(ConfigurationSection config)
    {
        Gain gain = null; 

        if (config != null) {
            Map<String, Double> multipliers = new HashMap<String, Double>();
            for (String group : config.getKeys(false)) {
                multipliers.put(group.toLowerCase(), Double.valueOf(config.getConfigurationSection(group).getDouble("Amount", 0.0D)));
            }
            gain = new GroupGain(multipliers);
        }

        return gain;
    }
}
