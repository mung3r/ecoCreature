package se.crafted.chrisb.ecoCreature.rewards.parties;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;

import com.herocraftonline.heroes.characters.Hero;

public class HeroesParty extends DefaultParty
{
    @Override
    public Set<String> getPlayers(Player player)
    {
        Set<String> party = new HashSet<String>();

        if (DependencyUtils.hasHeroes() && DependencyUtils.getHeroes().getCharacterManager().getHero(player).hasParty()) {
            for (Hero hero : DependencyUtils.getHeroes().getCharacterManager().getHero(player).getParty().getMembers()) {
                party.add(hero.getPlayer().getName());
            }
        }
        return party;
    }

    public static Party parseConfig(ConfigurationSection config)
    {
        HeroesParty party = null;

        if (config != null) {
            party = new HeroesParty();
            party.setShared(config.getBoolean("Share", false));
        }

        return party;
    }
}
