package se.crafted.chrisb.ecoCreature.events.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class SpawnEventListener implements Listener
{
    private final ecoCreature plugin;

    public SpawnEventListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (!event.isCancelled() && event.getSpawnReason() == SpawnReason.SPAWNER) {
            plugin.getRewardSettings(event.getEntity().getWorld()).addSpawnerMob(event.getEntity());
        }
    }
}
