package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.commons.EventUtils;

public class CreatureKilledByPlayerEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private EntityDeathEvent event;

    public CreatureKilledByPlayerEvent(EntityDeathEvent event)
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

    public Player getPlayer()
    {
        return EventUtils.getKillerFromDeathEvent(event);
    }

    public Player getKiller()
    {
        return getPlayer();
    }

    public LivingEntity getKilledCreature()
    {
        return (LivingEntity) event.getEntity();
    }

    public Material getWeapon()
    {
        return Material.getMaterial(getPlayer().getItemInHand().getTypeId());
    }

    public LivingEntity getTamedCreature()
    {
        return EventUtils.getTamedKillerFromDeathEvent(event);
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
