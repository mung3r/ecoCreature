package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerKilledByPlayerEvent extends EntityDeathEvent
{
    public PlayerKilledByPlayerEvent(LivingEntity what, List<ItemStack> drops, int droppedExp)
    {
        super(what, drops, droppedExp);
    }

    public Player getVictim()
    {
        return (Player) getEntity();
    }

    public Player getKiller()
    {
        return (Player) ((EntityDamageByEntityEvent) getEntity().getLastDamageCause()).getDamager();
    }
}
