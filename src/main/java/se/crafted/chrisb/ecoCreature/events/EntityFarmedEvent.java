package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.commons.EventUtils;

public final class EntityFarmedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private EntityDeathEvent event;

    public static EntityFarmedEvent createEvent(EntityDeathEvent event)
    {
        return new EntityFarmedEvent(event);
    }

    private EntityFarmedEvent(EntityDeathEvent event)
    {
        this.event = event;
    }

    public LivingEntity getEntity()
    {
        return event.getEntity();
    }

    public List<ItemStack> getDrops()
    {
        return event.getDrops();
    }

    public void setDroppedExp(int exp)
    {
        event.setDroppedExp(exp);
    }

    public boolean isFarmed()
    {
        return EventUtils.isEntityFarmed(event);
    }

    public boolean isFireFarmed()
    {
        return EventUtils.isEntityFireFarmed(event);
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
