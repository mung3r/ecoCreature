package se.crafted.chrisb.ecoCreature.rewards.gain;

import org.bukkit.entity.Player;

public class BasicGain implements Gain
{
    protected boolean isShared;

    public BasicGain()
    {
        isShared = false;
    }

    @Override
    public double getMultiplier(Player player)
    {
        return 1.0;
    }

    @Override
    public boolean isShared()
    {
        return isShared;
    }
}
