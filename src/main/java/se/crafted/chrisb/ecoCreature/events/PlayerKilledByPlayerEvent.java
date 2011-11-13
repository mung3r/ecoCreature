package se.crafted.chrisb.ecoCreature.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

@SuppressWarnings("serial")
public class PlayerKilledByPlayerEvent extends Event
{
    private Player victim;
    private Player killer;

    public PlayerKilledByPlayerEvent(EntityDeathEvent event)
    {
        super("PlayerKilledByPlayerEvent");

        this.victim = (Player) event.getEntity();
        this.killer = (Player) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
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

}
