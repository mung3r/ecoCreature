package se.crafted.chrisb.ecoCreature.rewards.parties;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;

public class MobArenaParty extends DefaultParty
{
    @Override
    public Set<String> getPlayers(Player player)
    {
        Set<String> party = new HashSet<String>();

        if (DependencyUtils.hasMobArena() && DependencyUtils.getMobArenaHandler().isPlayerInArena(player)) {
            for (Player member : DependencyUtils.getMobArenaHandler().getArenaWithPlayer(player).getAllPlayers()) {
                party.add(member.getName());
            }
        }

        return party;
    }

    public static Party parseConfig(ConfigurationSection config)
    {
        MobArenaParty party = null;

        if (config != null) {
            party = new MobArenaParty();
            party.setShared(config.getBoolean("Share", false));
        }

        return party;
    }
}
