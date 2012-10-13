package se.crafted.chrisb.ecoCreature.rewards.parties;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;

import com.gmail.nossr50.api.PartyAPI;

public class McMMOParty extends AbstractParty
{
    @Override
    public Set<String> getPlayers(Player player)
    {
        Set<String> party = Collections.emptySet();

        if (DependencyUtils.hasMcMMO() && PartyAPI.inParty(player)) {
            List<Player> members = PartyAPI.getOnlineMembers(player);
            if (members != null) {
                party = new HashSet<String>();

                for (Player member : members) {
                    party.add(member.getName());
                }
            }
        }
        ECLogger.getInstance().debug(this.getClass(), "Heroes party size: " + party.size());

        return party;
    }

    public static Set<Party> parseConfig(ConfigurationSection config)
    {
        Set<Party> parties = Collections.emptySet();

        if (config != null) {
            McMMOParty party = new McMMOParty();
            party.setShared(config.getBoolean("Share", false));
            parties = new HashSet<Party>();
            parties.add(party);
        }

        return parties;
    }
}
