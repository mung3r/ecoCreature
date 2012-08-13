package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.Utils;
import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;

public class DeathEventListener implements Listener
{
    private final ecoCreature plugin;

    public DeathEventListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        if (Utils.isPVPDeath(event)) {
            Bukkit.getPluginManager().callEvent(new PlayerKilledByPlayerEvent(event));
        }
        else {
            plugin.getRewardManager(event.getEntity().getWorld()).registerDeathPenalty(event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (!(event instanceof PlayerDeathEvent)) {
            Player killer = Utils.getKillerFromDeathEvent(event);

            if (killer != null) {
                Bukkit.getPluginManager().callEvent(new CreatureKilledByPlayerEvent(event));
            }
            else {
                plugin.getRewardManager(event.getEntity().getWorld()).handleNoFarm(event);
            }
        }
    }
}
