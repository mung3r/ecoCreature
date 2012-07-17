package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerKilledByPlayerEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private PlayerDeathEvent event;

    public PlayerKilledByPlayerEvent(PlayerDeathEvent event)
    {
        this.event = event;
    }

    public List<ItemStack> getDrops()
    {
        return event.getDrops();
    }

    public void setDroppedExp(int exp)
    {
        event.setDroppedExp(exp);
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
