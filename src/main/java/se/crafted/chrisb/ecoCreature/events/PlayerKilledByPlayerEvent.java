package se.crafted.chrisb.ecoCreature.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKilledByPlayerEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private PlayerDeathEvent event;

    public PlayerKilledByPlayerEvent(PlayerDeathEvent event)
    {
        this.event = event;
    }

    public PlayerDeathEvent getEvent()
    {
        return event;
    }

    public void setEvent(PlayerDeathEvent event)
    {
        this.event = event;
    }

    public Player getVictim()
    {
        return event.getEntity();
    }

    public Player getKiller()
    {
        return (Player) event.getEntity().getLastDamageCause().getEntity();
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
