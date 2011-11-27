package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("serial")
public class PlayerKilledByPlayerEvent extends Event
{
    private Player victim;
    private Player killer;
    private List<ItemStack> drops;

    public PlayerKilledByPlayerEvent(EntityDeathEvent event)
    {
        super("PlayerKilledByPlayerEvent");

        this.victim = (Player) event.getEntity();
        this.killer = (Player) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
        this.drops = event.getDrops();
    }

    public Player getVictim()
    {
        return victim;
    }

    public void setVictim(Player victim)
    {
        this.victim = victim;
    }

    public Player getKiller()
    {
        return killer;
    }

    public void setKiller(Player killer)
    {
        this.killer = killer;
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
