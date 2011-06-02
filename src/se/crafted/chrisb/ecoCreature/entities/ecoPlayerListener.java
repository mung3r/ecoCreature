package se.crafted.chrisb.ecoCreature.entities;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.utils.ecoConstants;

public class ecoPlayerListener extends PlayerListener
{
  public void onPlayerRespawn(PlayerRespawnEvent paramPlayerRespawnEvent)
  {
    if (ecoConstants.PD)
    {
      Player localPlayer = paramPlayerRespawnEvent.getPlayer();
      ecoCreature.getRewardHandler().RegisterAccident(localPlayer);
    }
  }
}