package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;

public class ecoDeathListener implements Listener
{
    public ecoDeathListener()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureKilledByPlayer(CreatureKilledByPlayerEvent event)
    {
        ecoCreature.getRewardManager(event.getPlayer()).registerCreatureDeath(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKilledByPlayer(PlayerKilledByPlayerEvent event)
    {
        ecoCreature.getRewardManager(event.getKiller()).registerPVPReward(event);
    }
}
