package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class StreakEventListener implements Listener
{
    private final ecoCreature plugin;

    public StreakEventListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathStreakEvent(DeathStreakEvent event)
    {
        plugin.getRewardManager(event.getPlayer().getWorld()).registerDeathStreak(event.getPlayer(), event.getDeaths());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKillStreakEvent(KillStreakEvent event)
    {
        plugin.getRewardManager(event.getPlayer().getWorld()).registerKillStreak(event.getPlayer(), event.getKills());
    }
}
