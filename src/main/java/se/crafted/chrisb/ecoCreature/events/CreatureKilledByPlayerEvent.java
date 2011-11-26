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
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;

@SuppressWarnings("serial")
public class CreatureKilledByPlayerEvent extends Event
{
    private Player player;
    private LivingEntity killedCreature;
    private Material weapon;
    private LivingEntity tamedCreature;
    private ecoReward reward;
    private List<ItemStack> drops;

    public CreatureKilledByPlayerEvent(EntityDeathEvent event)
    {
        super("CreatureKilledByPlayerEvent");

        player = ecoEntityUtil.getKillerFromDeathEvent(event);
        killedCreature = (LivingEntity) event.getEntity();
        weapon = Material.getMaterial(player.getItemInHand().getTypeId());
        tamedCreature = ecoEntityUtil.getTamedKillerFromDeathEvent(event);
        reward = ecoCreature.getRewardManager(event.getEntity()).rewards.get(killedCreature);
        drops = event.getDrops();
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public LivingEntity getKilledCreature()
    {
        return killedCreature;
    }

    public void setKilledCreature(LivingEntity killedCreature)
    {
        this.killedCreature = killedCreature;
    }

    public Material getWeapon()
    {
        return weapon;
    }

    public void setWeapon(Material weapon)
    {
        this.weapon = weapon;
    }

    public LivingEntity getTamedCreature()
    {
        return tamedCreature;
    }

    public void setTamedCreature(LivingEntity tamedCreature)
    {
        this.tamedCreature = tamedCreature;
    }

    public ecoReward getReward()
    {
        return reward;
    }

    public void setReward(ecoReward reward)
    {
        this.reward = reward;
    }

    public List<ItemStack> getDrops()
    {
        return drops;
    }

    public void setDrops(List<ItemStack> drops)
    {
        this.drops = drops;
    }
}
