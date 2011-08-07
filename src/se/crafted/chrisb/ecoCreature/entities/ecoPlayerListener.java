package se.crafted.chrisb.ecoCreature.entities;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.utils.ecoConstants;

public class ecoPlayerListener extends PlayerListener
{
    private final ecoCreature plugin;

    public ecoPlayerListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent)
    {
        if (ecoConstants.hasDeathPenalty) {
            Player player = playerRespawnEvent.getPlayer();
            plugin.getRewardHandler().registerAccident(player);
        }
    }
}