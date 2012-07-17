package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;

public class ecoDeathListener implements Listener
{
    private ecoCreature plugin;

    public ecoDeathListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureKilledByPlayer(CreatureKilledByPlayerEvent event)
    {
        plugin.getRewardManager(event.getPlayer().getWorld()).registerCreatureDeath(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKilledByPlayer(PlayerKilledByPlayerEvent event)
    {
        plugin.getRewardManager(event.getKiller().getWorld()).registerPVPReward(event);
    }
}
