package se.crafted.chrisb.ecoCreature.listeners;

import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;
import org.simiancage.DeathTpPlus.listeners.StreakEventsListener;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoStreakListener extends StreakEventsListener
{
    public ecoStreakListener(ecoCreature plugin)
    {
    }

    @Override
    public void onDeathStreakEvent(DeathStreakEvent event)
    {
        ecoCreature.getRewardManager(event.getPlayer()).registerDeathStreak(event.getPlayer(), event.getDeaths());
    }

    @Override
    public void onKillStreakEvent(KillStreakEvent event)
    {
        ecoCreature.getRewardManager(event.getPlayer()).registerKillStreak(event.getPlayer(), event.getKills());
    }
}
