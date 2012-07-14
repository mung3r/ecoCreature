package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoReward;
import se.crafted.chrisb.ecoCreature.models.ecoReward.RewardType;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;

public class CreatureKilledByPlayerEvent extends EntityDeathEvent
{
    public CreatureKilledByPlayerEvent(LivingEntity what, List<ItemStack> drops, int droppedExp)
    {
        super(what, drops, droppedExp);
    }

    public Player getPlayer()
    {
        return ecoEntityUtil.getKillerFromDeathEvent(this);
    }

    public Player getKiller()
    {
        return getPlayer();
    }

    public LivingEntity getKilledCreature()
    {
        return (LivingEntity) getEntity();
    }

    public Material getWeapon()
    {
        return Material.getMaterial(getPlayer().getItemInHand().getTypeId());
    }

    public LivingEntity getTamedCreature()
    {
        return ecoEntityUtil.getTamedKillerFromDeathEvent(this);
    }

    public ecoReward getReward()
    {
        return ecoCreature.getRewardManager(getEntity()).getRewardFromType(RewardType.fromEntity(getKilledCreature()));
    }
}
