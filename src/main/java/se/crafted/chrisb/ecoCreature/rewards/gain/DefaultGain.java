package se.crafted.chrisb.ecoCreature.rewards.gain;

import org.bukkit.entity.Player;

public class DefaultGain implements Gain
{
    double multiplier;

    public DefaultGain()
    {
        multiplier = 1.0;
    }

    @Override
    public double getMultiplier(Player player)
    {
        return multiplier;
    }
}
