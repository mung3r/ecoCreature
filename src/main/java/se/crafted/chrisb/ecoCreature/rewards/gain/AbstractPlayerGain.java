package se.crafted.chrisb.ecoCreature.rewards.gain;

import org.bukkit.entity.Player;

public abstract class AbstractPlayerGain implements PlayerGain
{
    protected static final double NO_GAIN = 1.0;
    
    @Override
    public abstract double getMultiplier(Player player);
}
