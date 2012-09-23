package se.crafted.chrisb.ecoCreature.rewards.gain;

import org.bukkit.entity.Player;

public class DefaultGain implements Gain
{
    @Override
    public double getMultiplier(Player player)
    {
        return 1.0;
    }
}
