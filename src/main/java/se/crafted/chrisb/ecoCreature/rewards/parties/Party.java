package se.crafted.chrisb.ecoCreature.rewards.parties;

import java.util.Set;

import org.bukkit.entity.Player;

public interface Party
{
    boolean isShared();

    void setShared(boolean shared);

    Set<String> getPlayers(Player player);
}
