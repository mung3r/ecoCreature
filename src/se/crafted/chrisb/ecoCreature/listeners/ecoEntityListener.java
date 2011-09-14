package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.managers.ecoRewardManager;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;

public class ecoEntityListener extends EntityListener
{
    private final ecoCreature plugin;

    public ecoEntityListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Player) {
            return;
        }

        Player player = null;
        LivingEntity tamedCreature = null;

        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
            if (subEvent.getDamager() instanceof Player) {
                player = (Player) subEvent.getDamager();
            }
            else if (subEvent.getDamager() instanceof Tameable) {
                if (((Tameable) subEvent.getDamager()).isTamed() && ((Tameable) subEvent.getDamager()).getOwner() instanceof Player) {
                    tamedCreature = (LivingEntity) subEvent.getDamager();
                    player = (Player) ((Tameable) subEvent.getDamager()).getOwner();
                }
            }
            else if (subEvent.getDamager() instanceof Projectile) {
                if (((Projectile) subEvent.getDamager()).getShooter() instanceof Player) {
                    player = (Player) ((Projectile) subEvent.getDamager()).getShooter();
                }
            }
        }

        if (player == null) {
            return;
        }

        if (ecoRewardManager.shouldOverrideDrops) {
            event.getDrops().clear();
        }

        if (player.getItemInHand().getType().equals(Material.BOW) && !ecoRewardManager.hasBowRewards) {
            if (ecoRewardManager.shouldOutputMessages) {
                player.sendMessage(ecoRewardManager.noBowRewardMessage);
            }
            return;
        }
        else if (ecoEntityUtil.isUnderSeaLevel(player) && !ecoRewardManager.canHuntUnderSeaLevel) {
            if (ecoRewardManager.shouldOutputMessages) {
                player.sendMessage(ecoRewardManager.noBowRewardMessage);
            }
            return;
        }

        LivingEntity livingEntity = (LivingEntity) event.getEntity();

        if ((ecoEntityUtil.isNearSpawner(player) || ecoEntityUtil.isNearSpawner(livingEntity)) && !ecoRewardManager.canCampSpawner) {
            if (ecoRewardManager.shouldClearCampDrops) {
                event.getDrops().clear();
            }
            if (ecoRewardManager.shouldOutputMessages && ecoRewardManager.shouldOutputSpawnerMessage) {
                player.sendMessage(ecoRewardManager.noCampMessage);
            }
        }
        else {
            plugin.getRewardManager().registerCreatureReward(player, tamedCreature, livingEntity);
        }

        event.getDrops().addAll(ecoRewardManager.rewards.get(ecoEntityUtil.getCreatureType(livingEntity)).computeDrops());
    }
}