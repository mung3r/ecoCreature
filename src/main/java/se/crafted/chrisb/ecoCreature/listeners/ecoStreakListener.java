package se.crafted.chrisb.ecoCreature.listeners;

import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;
import org.simiancage.DeathTpPlus.listeners.StreakEventsListener;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.utils.ecoLogger;

public class ecoStreakListener extends StreakEventsListener
{
    private final ecoCreature plugin;
    private final ecoLogger log;

    public ecoStreakListener(ecoCreature plugin)
    {
        this.plugin = plugin;
        log = this.plugin.getLogger();
    }

    @Override
    public void onDeathStreakEvent(DeathStreakEvent event)
    {
        ecoCreature.getRewardManager(event.getPlayer()).registerDeathStreak(event.getPlayer());
        log.debug("caught death streak event");
    }

    @Override
    public void onKillStreakEvent(KillStreakEvent event)
    {
        ecoCreature.getRewardManager(event.getPlayer()).registerKillStreak(event.getPlayer());
        log.debug("caught kill streak event");
    }
}
