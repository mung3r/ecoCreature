package se.crafted.chrisb.ecoCreature.rewards.parties;

import java.util.HashSet;
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
            for (Player member : PartyAPI.getOnlineMembers(player)) {
                party.add(member.getName());
            }
        }

        return party;
    }

    public static Party parseConfig(ConfigurationSection config)
    {
        McMMOParty party = null;

        if (config != null) {
            party = new McMMOParty();
            party.setShared(config.getBoolean("Share", false));
        }

        return party;
    }
}
