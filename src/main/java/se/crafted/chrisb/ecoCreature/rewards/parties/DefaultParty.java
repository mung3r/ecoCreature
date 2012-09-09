package se.crafted.chrisb.ecoCreature.rewards.parties;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class DefaultParty implements Party
{
    protected boolean shared;

    public DefaultParty()
    {
        shared = false;
    }

    @Override
    public boolean isShared()
    {
        return shared;
    }

    @Override
    public void setShared(boolean shared)
    {
        this.shared = shared;
    }

    @Override
    public Set<String> getPlayers(Player player)
    {
        return new HashSet<String>();
    }
}
