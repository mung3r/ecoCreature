package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoReward;
import se.crafted.chrisb.ecoCreature.models.ecoReward.RewardType;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;

public class CreatureKilledByPlayerEvent extends Event
{
    private static final HandlerList HANDLERS = new HandlerList();
    private EntityDeathEvent event;

    public CreatureKilledByPlayerEvent(EntityDeathEvent event)
    {
        this.event = event;
    }

    public Player getPlayer()
    {
        return ecoEntityUtil.getKillerFromDeathEvent(event);
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
        return ecoEntityUtil.getTamedKillerFromDeathEvent(event);
    }

    public ecoReward getReward()
    {
        return ecoCreature.getRewardManager(event.getEntity()).getRewardFromType(RewardType.fromEntity(getKilledCreature()));
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
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }
}
