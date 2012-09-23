package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.commons.EventUtils;

public class EntityKilledEvent extends EntityDeathEvent
{
    public static EntityKilledEvent createEvent(EntityDeathEvent event)
    {
        return new EntityKilledEvent(event.getEntity(), event.getDrops(), event.getDroppedExp());
    }

    private EntityKilledEvent(LivingEntity entity, List<ItemStack> drops, int droppedExp)
    {
        super(entity, drops, droppedExp);
    }

    public Player getKiller()
    {
        return EventUtils.getKillerFromDeathEvent(this);
    }

    public String getWeaponName()
    {
        return isTamedCreatureKill() ? getTamedCreature().getType().getName() : EntityUtils.getItemNameInHand(getKiller());
    }

    public boolean isTamedCreatureKill()
    {
        return getTamedCreature() != null;
    }

    private LivingEntity getTamedCreature()
    {
        return EventUtils.getTamedKillerFromDeathEvent(this);
    }

    public boolean isProjectileKill()
    {
        return EventUtils.isProjectileKill(this);
    }
}
