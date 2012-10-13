package se.crafted.chrisb.ecoCreature.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;

import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.events.handlers.PluginEventHandler;

public class McMMOEventListener implements Listener
{
    private final PluginEventHandler handler;

    public McMMOEventListener(PluginEventHandler handler)
    {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMcMMOPlayerLevelUp(McMMOPlayerLevelUpEvent event)
    {
        for (RewardEvent rewardEvent : handler.createRewardEvents(event)) {
            Bukkit.getPluginManager().callEvent(rewardEvent);
        }
    }
}
