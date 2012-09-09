package se.crafted.chrisb.ecoCreature.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.commons.EventUtils;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.events.handlers.RewardEventHandler;

public class PlayerDeathEventListener implements Listener
{
    private final RewardEventHandler handler;

    public PlayerDeathEventListener(RewardEventHandler handler)
    {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(PlayerDeathEvent event)
    {
        if (EventUtils.isPVPDeath(event)) {
            for (RewardEvent rewardEvent : handler.getRewardEvents(PlayerKilledEvent.createEvent(event))) {
                Bukkit.getPluginManager().callEvent(rewardEvent);
            }
        }
        else if (!EventUtils.isSuicide(event)) {
            for (RewardEvent rewardEvent : handler.getRewardEvents(event)) {
                Bukkit.getPluginManager().callEvent(rewardEvent);
            }
        }
    }
}
