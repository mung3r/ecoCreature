package se.crafted.chrisb.ecoCreature.rewards.parties;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

import com.herocraftonline.heroes.characters.Hero;

public class HeroesParty extends AbstractParty
{
    @Override
    public Set<String> getPlayers(Player player)
    {
        Set<String> party = Collections.emptySet();

        if (DependencyUtils.hasHeroes() && DependencyUtils.getHeroes().getCharacterManager().getHero(player).hasParty()) {
            party = new HashSet<String>();

            for (Hero hero : DependencyUtils.getHeroes().getCharacterManager().getHero(player).getParty().getMembers()) {
                party.add(hero.getPlayer().getName());
            }
        }
        ECLogger.getInstance().debug(this.getClass(), "Heroes party size: " + party.size());

        return party;
    }

    public static Set<Party> parseConfig(ConfigurationSection config)
    {
        Set<Party> parties = Collections.emptySet();

        if (config != null) {
            HeroesParty party = new HeroesParty();
            party.setShared(config.getBoolean("Share", false));
            parties = new HashSet<Party>();
            parties.add(party);
        }

        return parties;
    }
}
