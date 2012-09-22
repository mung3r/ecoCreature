package se.crafted.chrisb.ecoCreature.rewards.parties;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;

import com.gmail.nossr50.api.PartyAPI;

public class McMMOParty extends DefaultParty
{
    @Override
    public Set<String> getPlayers(Player player)
    {
        Set<String> party = new HashSet<String>();

        if (DependencyUtils.hasMcMMO() && PartyAPI.inParty(player)) {
            List<Player> members = PartyAPI.getOnlineMembers(player);
            if (members != null) {
                for (Player member : members) {
                    party.add(member.getName());
                }
            }
        }

        return party;
    }

    public static Set<Party> parseConfig(ConfigurationSection config)
    {
        Set<Party> parties = new HashSet<Party>();

        if (config != null) {
            McMMOParty party = new McMMOParty();
            party.setShared(config.getBoolean("Share", false));
            parties.add(party);
        }

        return parties;
    }
}
