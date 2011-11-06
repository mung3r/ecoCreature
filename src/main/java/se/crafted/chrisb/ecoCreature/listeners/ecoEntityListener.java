package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.managers.ecoMessageManager;
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
            plugin.getRewardManager().registerPlayerDeath(event);
            return;
        }

        Player player = null;
        LivingEntity tamedCreature = null;

        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
            if (damager instanceof Player) {
                player = (Player) damager;
            }
            else if (damager instanceof Tameable) {
                if (((Tameable) damager).isTamed() && ((Tameable) damager).getOwner() instanceof Player) {
                    tamedCreature = (LivingEntity) damager;
                    player = (Player) ((Tameable) damager).getOwner();
                }
            }
            else if (damager instanceof Projectile) {
                if (((Projectile) damager).getShooter() instanceof Player) {
                    player = (Player) ((Projectile) damager).getShooter();
                }
            }
        }

        if (player == null) {
            EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
            if (ecoRewardManager.noFarm && damageEvent != null) {
                if (damageEvent instanceof EntityDamageByBlockEvent && damageEvent.getCause().equals(DamageCause.CONTACT)) {
                    event.getDrops().clear();
                }
                else if (damageEvent.getCause() != null && (damageEvent.getCause().equals(DamageCause.FALL) || damageEvent.getCause().equals(DamageCause.DROWNING) || damageEvent.getCause().equals(DamageCause.SUFFOCATION))) {
                    event.getDrops().clear();
                }
            }
            return;
        }

        if (player.getItemInHand().getType().equals(Material.BOW) && !ecoRewardManager.hasBowRewards) {
            plugin.getMessageManager().sendMessage(ecoMessageManager.noBowRewardMessage, player);
            return;
        }
        else if (ecoEntityUtil.isUnderSeaLevel(player) && !ecoRewardManager.canHuntUnderSeaLevel) {
            plugin.getMessageManager().sendMessage(ecoMessageManager.noBowRewardMessage, player);
            return;
        }

        LivingEntity livingEntity = (LivingEntity) event.getEntity();

        if ((ecoEntityUtil.isNearSpawner(player) || ecoEntityUtil.isNearSpawner(livingEntity)) && !ecoRewardManager.canCampSpawner) {
            if (ecoRewardManager.shouldClearCampDrops) {
                event.getDrops().clear();
            }
            plugin.getMessageManager().sendMessage(ecoMessageManager.noCampMessage, player);
        }
        else {
            plugin.getRewardManager().registerCreatureDeath(player, tamedCreature, livingEntity);
        }

        if (ecoRewardManager.rewards.containsKey(ecoEntityUtil.getCreatureType(livingEntity))) {
            if (ecoRewardManager.shouldOverrideDrops) {
                event.getDrops().clear();
            }

            event.getDrops().addAll(ecoRewardManager.rewards.get(ecoEntityUtil.getCreatureType(livingEntity)).computeDrops());
        }
    }
}