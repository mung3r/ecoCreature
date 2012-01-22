package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;

public class ecoEntityListener implements Listener
{
    public ecoEntityListener()
    {
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event instanceof PlayerDeathEvent) {
            if (ecoEntityUtil.isPVPDeath(event)) {
                Bukkit.getPluginManager().callEvent(new PlayerKilledByPlayerEvent(event));
            }
            else {
                ecoCreature.getRewardManager(event.getEntity()).registerDeathPenalty((Player) event.getEntity());
            }
            return;
        }

        Player killer = ecoEntityUtil.getKillerFromDeathEvent(event);

        if (killer == null) {
            if (ecoCreature.getRewardManager(event.getEntity()).noFarm) {
                ecoCreature.getRewardManager(event.getEntity()).handleNoFarm(event);
            }
            return;
        }

        Bukkit.getPluginManager().callEvent(new CreatureKilledByPlayerEvent(event));
    }
}
