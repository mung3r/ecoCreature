package se.crafted.chrisb.ecoCreature.listeners;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerDeathEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;

public class ecoDeathListener extends DeathEventsListener
{
    private ecoCreature plugin;

    public ecoDeathListener(ecoCreature plugin)
    {
        this.plugin = plugin;
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
        ecoCreature.getRewardManager(event.getKiller()).registerPVPReward(event.getVictim(), event.getKiller());
    }
}
