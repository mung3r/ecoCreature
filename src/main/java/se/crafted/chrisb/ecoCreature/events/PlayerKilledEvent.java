package se.crafted.chrisb.ecoCreature.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.commons.EntityUtils;

public class PlayerKilledEvent extends PlayerDeathEvent
{
    public static PlayerKilledEvent createEvent(PlayerDeathEvent event)
    {
        return new PlayerKilledEvent(event.getEntity(), event.getDrops(), event.getDroppedExp(), event.getNewExp(), event.getNewTotalExp(), event.getNewLevel(), event.getDeathMessage());
    }

    private PlayerKilledEvent(Player player, List<ItemStack> drops, int droppedExp, int newExp, int newTotalExp, int newLevel, String deathMessage)
    {
        super(player, drops, droppedExp, newExp, newTotalExp, newLevel, deathMessage);
    }

    public Player getVictim()
    {
        return getEntity();
    }

    public Player getKiller()
    {
        return getEntity().getKiller();
    }

    public String getWeaponName()
    {
        return EntityUtils.getItemNameInHand(getKiller());
    }
}
