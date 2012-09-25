package se.crafted.chrisb.ecoCreature.rewards.parties;

import java.util.Collections;
import java.util.Set;

import org.bukkit.entity.Player;

public abstract class AbstractParty implements Party
{
    private boolean shared;

    public AbstractParty()
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
        return Collections.emptySet();
    }
}
