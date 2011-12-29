package se.crafted.chrisb.ecoCreature.listeners;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;

public class ecoDeathListener extends DeathEventsListener
{
    public ecoDeathListener()
    {
    }

    @Override
    public void onCreatureKilledByPlayer(CreatureKilledByPlayerEvent event)
    {
        ecoCreature.getRewardManager(event.getPlayer()).registerCreatureDeath(event.getPlayer(), event.getTamedCreature(), event.getKilledCreature(), event.getDrops());
    }

    @Override
    public void onPlayerKilledByPlayer(PlayerKilledByPlayerEvent event)
    {
        ecoCreature.getRewardManager(event.getKiller()).registerPVPReward(event.getVictim(), event.getKiller(), event.getDrops());
    }
}
