package se.crafted.chrisb.ecoCreature.events.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import se.crafted.chrisb.ecoCreature.commons.EventUtils;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.events.handlers.RewardEventHandler;
import se.crafted.chrisb.ecoCreature.rewards.RewardSettings;

public class EntityDeathEventListener implements Listener
{
    private final RewardEventHandler handler;

    public EntityDeathEventListener(RewardEventHandler handler)
    {
        this.handler = handler;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event instanceof PlayerDeathEvent) {
            return;
        }

        handleDefaultDrops(event);

        if (EventUtils.getKillerFromDeathEvent(event) instanceof Player) {
            for (RewardEvent rewardEvent : handler.getRewardEvents(EntityKilledEvent.createEvent(event))) {
                Bukkit.getPluginManager().callEvent(rewardEvent);
            }
        }
        else {
            handleNoFarm(event);
        }
    }

    private void handleDefaultDrops(EntityDeathEvent event)
    {
        RewardSettings settings = handler.getSettings(event.getEntity().getWorld());

        if (settings.isClearDefaultDrops()) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }

    private void handleNoFarm(EntityDeathEvent event)
    {
        RewardSettings settings = handler.getSettings(event.getEntity().getWorld());

        if (settings.isNoFarm()) {
            EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

            if (damageEvent != null) {
                if (damageEvent instanceof EntityDamageByBlockEvent && damageEvent.getCause().equals(DamageCause.CONTACT)) {
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                }
                else if (damageEvent.getCause() != null) {
                    if (damageEvent.getCause().equals(DamageCause.FALL) || damageEvent.getCause().equals(DamageCause.DROWNING) || damageEvent.getCause().equals(DamageCause.SUFFOCATION)) {
                        event.getDrops().clear();
                        event.setDroppedExp(0);
                    }
                    else if (settings.isNoFarmFire() && (damageEvent.getCause().equals(DamageCause.FIRE) || damageEvent.getCause().equals(DamageCause.FIRE_TICK))) {
                        event.getDrops().clear();
                        event.setDroppedExp(0);
                    }
                }
            }
        }
    }
}
