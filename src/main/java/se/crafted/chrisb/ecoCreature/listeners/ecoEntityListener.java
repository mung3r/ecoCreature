package se.crafted.chrisb.ecoCreature.listeners;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

        Player player = ecoEntityUtil.getKillerFromDeathEvent(event);

        if (player == null) {
            if (ecoRewardManager.noFarm) {
                plugin.getRewardManager().handleNoFarm(event);
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

        LivingEntity killedCreature = (LivingEntity) event.getEntity();

        if ((ecoEntityUtil.isNearSpawner(player) || ecoEntityUtil.isNearSpawner(killedCreature)) && !ecoRewardManager.canCampSpawner) {
            if (ecoRewardManager.shouldClearCampDrops) {
                event.getDrops().clear();
            }
            plugin.getMessageManager().sendMessage(ecoMessageManager.noCampMessage, player);
        }
        else {
            plugin.getRewardManager().registerCreatureDeath(player, ecoEntityUtil.getTamedKillerFromDeathEvent(event), killedCreature);
        }

        if (ecoRewardManager.rewards.containsKey(ecoEntityUtil.getCreatureType(killedCreature))) {
            if (ecoRewardManager.shouldOverrideDrops) {
                event.getDrops().clear();
            }

            event.getDrops().addAll(ecoRewardManager.rewards.get(ecoEntityUtil.getCreatureType(killedCreature)).computeDrops());
        }
    }
}