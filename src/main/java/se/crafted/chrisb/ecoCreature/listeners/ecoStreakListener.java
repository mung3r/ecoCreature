package se.crafted.chrisb.ecoCreature.listeners;

import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;
import org.simiancage.DeathTpPlus.listeners.StreakEventsListener;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoStreakListener extends StreakEventsListener
{
    private ecoCreature plugin;

    public ecoStreakListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onDeathStreakEvent(DeathStreakEvent event)
    {
        // TODO: register some kind of penalty
        plugin.getLogger().info("DEBUG: caught death streak event");
    }

    @Override
    public void onKillStreakEvent(KillStreakEvent event)
    {
        // TODO: register some kind of reward
        plugin.getLogger().info("DEBUG: caught kill streak event");
    }
}
