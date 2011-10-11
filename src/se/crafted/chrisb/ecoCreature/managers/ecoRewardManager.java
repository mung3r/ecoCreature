package se.crafted.chrisb.ecoCreature.managers;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoReward;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;

public class ecoRewardManager
{
    private final ecoCreature plugin;

    public static Boolean isIntegerCurrency;

    public static Boolean canCampSpawner;
    public static Boolean shouldOverrideDrops;
    public static Boolean isFixedDrops;
    public static Boolean shouldClearCampDrops;
    public static int campRadius;
    public static Boolean hasBowRewards;
    public static Boolean hasDeathPenalty;
    public static Boolean hasPVPReward;
    public static Boolean isPercentPenalty;
    public static Double penaltyAmount;
    public static Boolean canHuntUnderSeaLevel;
    public static Boolean isWolverineMode;

    public static HashMap<String, Double> groupMultiplier = new HashMap<String, Double>();
    public static HashMap<CreatureType, ecoReward> rewards;
    public static ecoReward spawnerReward;

    public ecoRewardManager(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    public void registerDeathPenalty(EntityDeathEvent event)
    {
        Player player = (Player) event.getEntity();

        if (player == null) {
            return;
        }

        Double amount = isPercentPenalty ? ecoCreature.economy.getBalance(player.getName()) * (penaltyAmount / 100.0D) : penaltyAmount;
        if (amount <= 0.0D) {
            return;
        }

        if (hasDeathPenalty) {
            String perm = "ecoCreature.DeathPenalty";
            if (!ecoCreature.permission.has(player, perm) && !ecoCreature.permission.has(player, perm.toLowerCase())) {
                return;
            }

            ecoCreature.economy.withdrawPlayer(player.getName(), amount);

            plugin.getMessageManager().sendMessage(ecoMessageManager.deathPenaltyMessage, player, amount);
        }

        if (hasPVPReward) {
            String perm = "ecoCreature.PVPReward";
            if (!ecoCreature.permission.has(player, perm) && !ecoCreature.permission.has(player, perm.toLowerCase())) {
                return;
            }
            
            if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
                if (subEvent.getDamager() instanceof Player) {
                    Player killer = (Player) subEvent.getDamager();

                    ecoCreature.economy.depositPlayer(killer.getName(), amount);

                    plugin.getMessageManager().sendMessage(ecoMessageManager.pvpRewardMessage, killer, amount, player.getName(), "");
                }
            }
        }

        return;
    }

    public void registerCreatureReward(Player player, LivingEntity tamedCreature, LivingEntity killedCreature)
    {
        if (player == null || killedCreature == null) {
            return;
        }

        String perm = "ecoCreature.Creature.Craft" + ecoEntityUtil.getCreatureType(killedCreature).getName();
        if (!ecoCreature.permission.has(player, perm) && !ecoCreature.permission.has(player, perm.toLowerCase())) {
            return;
        }

        if (killedCreature instanceof Tameable) {
            if (((Tameable) killedCreature).isTamed() && ((Tameable) killedCreature).getOwner() instanceof Player) {
                Player owner = (Player) ((Tameable) killedCreature).getOwner();
                if (owner.getName().equals(player.getName())) {
                    return;
                }
            }
        }

        ecoReward reward = rewards.get(ecoEntityUtil.getCreatureType(killedCreature));
        String weaponName = tamedCreature != null ? ecoEntityUtil.getCreatureType(tamedCreature).getName() : Material.getMaterial(player.getItemInHand().getTypeId()).name();

        if (reward == null) {
            plugin.getLogger().info("Unrecognized creature: " + killedCreature.getClass().getSimpleName());
        }
        else {
            registerReward(player, reward, weaponName);
        }
    }

    public void registerSpawnerReward(Player player, Block block)
    {
        if (player == null || block == null) {
            return;
        }

        if (!block.getType().equals(Material.MOB_SPAWNER)) {
            return;
        }

        String perm = "ecoCreature.Creature.Spawner";
        if (ecoCreature.permission.has(player, perm) || ecoCreature.permission.has(player, perm.toLowerCase())) {

            registerReward(player, spawnerReward, Material.getMaterial(player.getItemInHand().getTypeId()).name());

            for (ItemStack itemStack : spawnerReward.computeDrops()) {
                block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
            }
        }
    }

    private void registerReward(Player player, ecoReward reward, String weaponName)
    {
        Double amount = computeReward(player, reward);

        if (amount > 0.0D) {
            ecoCreature.economy.depositPlayer(player.getName(), amount);
            plugin.getMessageManager().sendMessage(reward.getRewardMessage(), player, amount, reward.getCreatureName(), weaponName);
        }
        else if (amount < 0.0D) {
            ecoCreature.economy.withdrawPlayer(player.getName(), amount);
            plugin.getMessageManager().sendMessage(reward.getPenaltyMessage(), player, amount, reward.getCreatureName(), weaponName);
        }
        else {
            plugin.getMessageManager().sendMessage(reward.getNoRewardMessage(), player, reward.getCreatureName(), weaponName);
        }
    }

    private Double computeReward(Player player, ecoReward reward)
    {
        Double amount = reward.getRewardAmount();

        if (isIntegerCurrency) {
            amount = (double) Math.round(amount);
        }

        try {
            String group = ecoCreature.permission.getPrimaryGroup(player.getWorld().getName(), player.getName()).toLowerCase();
            if (groupMultiplier.containsKey(group)) {
                amount *= groupMultiplier.get(group);
            }
        }
        catch (Exception exception) {
            plugin.getLogger().warning("Permissions does not support group multiplier");
        }

        return amount;
    }
}