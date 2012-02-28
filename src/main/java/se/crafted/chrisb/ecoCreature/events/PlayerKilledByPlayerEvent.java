package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerKilledByPlayerEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private EntityDeathEvent event;

    public PlayerKilledByPlayerEvent(EntityDeathEvent event)
    {
        this.event = event;
    }

    public Player getVictim()
    {
        return (Player) event.getEntity();
    }

    public Player getKiller()
    {
        return (Player) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
    }

    public List<ItemStack> getDrops()
    {
        return event.getDrops();
    }

    public int getDroppedExp()
    {
        return event.getDroppedExp();
    }

    public void setDroppedExp(int exp)
    {
        event.setDroppedExp(exp);
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
