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
        plugin.getRewardManager().registerCreatureDeath(event.getPlayer(), event.getTamedCreature(), event.getKilledCreature(), event.getDrops());
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        plugin.getRewardManager().registerDeathPenalty(event.getPlayer());
    }

    @Override
    public void onPlayerKilledByPlayer(PlayerKilledByPlayerEvent event)
    {
        plugin.getRewardManager().registerPVPReward(event.getVictim(), event.getKiller());
    }
}
