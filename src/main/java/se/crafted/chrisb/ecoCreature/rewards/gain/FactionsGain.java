package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

public class FactionsGain extends BasicGain
{
    private Map<String, Double> multipliers;

    public FactionsGain(Map<String, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        if (DependencyUtils.hasPermission(player, "gain.factions") && DependencyUtils.hasFactions()) {
            Faction faction = Board.getFactionAt(new FLocation(player.getLocation()));
            if (faction != null && multipliers.containsKey(faction.getTag())) {
                multiplier = multipliers.get(faction.getTag());
                ECLogger.getInstance().debug("Factions multiplier: " + multiplier);
            }
        }

        return multiplier;
    }

    public static Gain parseConfig(ConfigurationSection config)
    {
        Gain gain = null;

        if (config != null) {
            Map<String, Double> factionsMultipliers = new HashMap<String, Double>();
            for (String factionsTag : config.getKeys(false)) {
                factionsMultipliers.put(factionsTag, Double.valueOf(config.getConfigurationSection(factionsTag).getDouble("Amount", 1.0D)));
            }
            gain = new FactionsGain(factionsMultipliers);
        }

        return gain;
    }
}
