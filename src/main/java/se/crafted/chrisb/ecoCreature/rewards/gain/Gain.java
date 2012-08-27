package se.crafted.chrisb.ecoCreature.rewards.gain;

import org.bukkit.entity.Player;

public interface Gain
{
    public double getMultiplier(Player player);
    public boolean isShared();
}
