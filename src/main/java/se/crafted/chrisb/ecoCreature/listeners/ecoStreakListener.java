package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoStreakListener implements Listener
{
    public ecoStreakListener()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathStreakEvent(DeathStreakEvent event)
    {
        ecoCreature.getRewardManager(event.getPlayer()).registerDeathStreak(event.getPlayer(), event.getDeaths());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKillStreakEvent(KillStreakEvent event)
    {
        ecoCreature.getRewardManager(event.getPlayer()).registerKillStreak(event.getPlayer(), event.getKills());
    }
}
