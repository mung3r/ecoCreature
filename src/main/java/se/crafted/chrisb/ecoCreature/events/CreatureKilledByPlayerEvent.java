package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoReward;
import se.crafted.chrisb.ecoCreature.models.ecoReward.RewardType;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;

@SuppressWarnings("serial")
public class CreatureKilledByPlayerEvent extends Event
{
    private EntityDeathEvent event;

    public CreatureKilledByPlayerEvent(EntityDeathEvent event)
    {
        super("CreatureKilledByPlayerEvent");

        this.event = event;
    }

    public Player getPlayer()
    {
        return ecoEntityUtil.getKillerFromDeathEvent(event);
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
        return ecoCreature.getRewardManager(event.getEntity()).rewards.get(RewardType.fromEntity(getKilledCreature()));
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
}
