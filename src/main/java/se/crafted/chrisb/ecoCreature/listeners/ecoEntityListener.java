package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;

public class ecoEntityListener extends EntityListener
{
    private final ecoCreature plugin;

    public ecoEntityListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event instanceof PlayerDeathEvent) {
            if (ecoEntityUtil.isPVPDeath(event)) {
                plugin.getServer().getPluginManager().callEvent(new PlayerKilledByPlayerEvent(event));
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

        plugin.getServer().getPluginManager().callEvent(new CreatureKilledByPlayerEvent(event));
    }
}
