package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class HeroesGain extends DefaultGain
{
    private double multiplier;

    public HeroesGain(double multiplier)
    {
        this.multiplier = multiplier;
    }

    @Override
    public double getMultiplier(Player player)
    {
        if (DependencyUtils.hasPermission(player, "gain.heroes") && DependencyUtils.hasHeroes() && DependencyUtils.getHeroes().getCharacterManager().getHero(player).hasParty()) {
            ECLogger.getInstance().debug("Heroes multiplier: " + multiplier);
            return multiplier;
        }

        return super.getMultiplier(player);
    }

    public static Set<Gain> parseConfig(ConfigurationSection config)
    {
        Set<Gain> gain = new HashSet<Gain>();

        if (config != null) {
            gain.add(new HeroesGain(config.getDouble("Amount", 1.0D)));
        }

        return gain;
    }
}
