package se.crafted.chrisb.ecoCreature.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

@SuppressWarnings("serial")
public class PlayerDeathEvent extends Event
{
    private Player player;

    public PlayerDeathEvent(EntityDeathEvent event)
    {
        super("PlayerDeathEvent");

        this.player = (Player) event.getEntity();
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }
}
