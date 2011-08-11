package se.crafted.chrisb.ecoCreature.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.managers.ecoRewardManager;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;

public class ecoEntityListener extends EntityListener
{
    private final ecoCreature plugin;
    private Map<Entity, Player> creatureTable = new HashMap<Entity, Player>();

    public ecoEntityListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        if (event instanceof EntityDamageByEntityEvent) {
            onEntityDamageByEntityEvent((EntityDamageByEntityEvent) event);
        }
        else if (event instanceof EntityDamageByProjectileEvent) {
            onEntityDamageByProjectileEvent((EntityDamageByProjectileEvent) event);
        }
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Player) {
            return;
        }

        Player player = null;
        if (creatureTable.containsKey(event.getEntity())) {
            player = creatureTable.get(event.getEntity());
        }
        else {
            return;
        }

        if (ecoRewardManager.shouldOverrideDrops) {
            event.getDrops().clear();
        }

        if (player.getItemInHand().getType().equals(Material.BOW) && !ecoRewardManager.hasBowRewards) {
            if (ecoRewardManager.shouldOutputMessages) {
                player.sendMessage(ecoRewardManager.noBowRewardMessage);
            }
            creatureTable.remove(event.getEntity());
            return;
        }

        if (ecoEntityUtil.isUnderSeaLevel(player) && !ecoRewardManager.canHuntUnderSeaLevel) {
            if (ecoRewardManager.shouldOutputMessages) {
                player.sendMessage(ecoRewardManager.noBowRewardMessage);
            }
            creatureTable.remove(event.getEntity());
            return;
        }

        LivingEntity livingEntity = (LivingEntity) event.getEntity();

        if ((ecoEntityUtil.isNearSpawner(player) || ecoEntityUtil.isNearSpawner(livingEntity)) && !ecoRewardManager.canCampSpawner) {
            if (ecoRewardManager.shouldClearCampDrops) {
                event.getDrops().clear();
            }
            if (ecoRewardManager.shouldOutputSpawnerMessage) {
                player.sendMessage(ecoRewardManager.noCampMessage);
            }
        }
        else if (ecoCreature.permissionsHandler.has(player, "ecoCreature.Creature." + livingEntity.getClass().getSimpleName())) {
            plugin.getRewardManager().registerCreatureReward(player, ecoEntityUtil.getCreatureType(livingEntity));
        }

        event.getDrops().addAll(ecoRewardManager.rewards.get(ecoEntityUtil.getCreatureType(livingEntity)).computeDrops());
        creatureTable.remove(event.getEntity());
    }

    private void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        if (ecoEntityUtil.isDeathBlow(event)) {
            if (event.getDamager() instanceof Player) {
                creatureTable.put(event.getEntity(), (Player) event.getDamager());
            }
            else if (event.getDamager() instanceof Wolf && ecoRewardManager.isWolverineMode) {
                CraftWolf wolf = (CraftWolf) event.getDamager();
                if (wolf.isTamed()) {
                    creatureTable.put(event.getEntity(), plugin.getServer().getPlayer(wolf.getHandle().getOwnerName()));
                }
            }
        }
    }

    private void onEntityDamageByProjectileEvent(EntityDamageByProjectileEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        LivingEntity livingEntity = (LivingEntity) event.getEntity();

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            // TODO: What's the purpose of this player message?
            player.sendMessage(Double.toString(player.getLocation().toVector().distance(livingEntity.getLocation().toVector())));
            if (ecoEntityUtil.isDeathBlow(event))
                creatureTable.put(event.getEntity(), (Player) event.getDamager());
        }
    }
}