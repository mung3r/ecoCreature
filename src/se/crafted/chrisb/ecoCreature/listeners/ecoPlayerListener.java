package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoPlayerListener extends PlayerListener
{
    private final ecoCreature plugin;

    public ecoPlayerListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        plugin.getRewardManager().registerDeathPenalty(event.getPlayer());
    }
}