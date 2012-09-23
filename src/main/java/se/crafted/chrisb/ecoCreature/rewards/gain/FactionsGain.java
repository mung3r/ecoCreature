package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Relation;

public class FactionsGain extends DefaultGain
{
    private Map<Relation, Double> multipliers;

    public FactionsGain(Map<Relation, Double> multipliers)
    {
        this.multipliers = multipliers;
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = 1.0;

        if (DependencyUtils.hasPermission(player, "gain.factions") && DependencyUtils.hasFactions()) {
            FPlayer fPlayer = FPlayers.i.get(player);
            if (fPlayer != null && multipliers.containsKey(fPlayer.getRelationToLocation())) {
                multiplier = multipliers.get(fPlayer.getRelationToLocation());
                ECLogger.getInstance().debug("Factions multiplier: " + multiplier);
            }
        }

        return multiplier;
    }

    public static Set<Gain> parseConfig(ConfigurationSection config)
    {
        Set<Gain> gain = new HashSet<Gain>();

        if (config != null && DependencyUtils.hasFactions()) {
            Map<Relation, Double> multipliers = new HashMap<Relation, Double>();
            for (String relation : config.getKeys(false)) {
                try {
                    multipliers.put(Relation.valueOf(relation), Double.valueOf(config.getConfigurationSection(relation).getDouble("Amount", 1.0D)));
                }
                catch (IllegalArgumentException e) {
                    ECLogger.getInstance().warning("No Factions Relation: " + relation);
                }
            }
            gain.add(new FactionsGain(multipliers));
        }

        return gain;
    }
}
