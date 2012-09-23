package se.crafted.chrisb.ecoCreature.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import se.crafted.chrisb.ecoCreature.rewards.Reward;

public class RewardEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private String player;
    private Reward reward;

    private boolean isCancelled;

    public RewardEvent(Player player, Reward reward)
    {
        this(player.getName(), reward);
    }

    public RewardEvent(String player, Reward reward)
    {
        this.player = player;
        this.reward = reward;
    }

    public Player getPlayer()
    {
        return Bukkit.getPlayer(player);
    }

    public void setPlayer(String player)
    {
        this.player = player;
    }

    public Reward getReward()
    {
        return reward;
    }

    public void setReward(Reward reward)
    {
        this.reward = reward;
    }

    @Override
    public boolean isCancelled()
    {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled)
    {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
