package se.crafted.chrisb.ecoCreature.listeners;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerDeathEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.utils.ecoLogger;

public class ecoDeathListener extends DeathEventsListener
{
    private final ecoCreature plugin;
    private final ecoLogger log;

    public ecoDeathListener(ecoCreature plugin)
    {
        this.plugin = plugin;
        log = this.plugin.getLogger();
    }

    @Override
    public void onCreatureKilledByPlayer(CreatureKilledByPlayerEvent event)
    {
        ecoCreature.getRewardManager(event.getPlayer()).registerCreatureDeath(event.getPlayer(), event.getTamedCreature(), event.getKilledCreature(), event.getDrops());
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        ecoCreature.getRewardManager(event.getPlayer()).registerDeathPenalty(event.getPlayer());
    }

    @Override
    public void onPlayerKilledByPlayer(PlayerKilledByPlayerEvent event)
    {
        ecoCreature.getRewardManager(event.getKiller()).registerPVPReward(event.getVictim(), event.getKiller(), event.getDrops());
    }
}
