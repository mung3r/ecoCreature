package se.crafted.chrisb.ecoCreature.managers;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoReward;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil.TIME_PERIOD;
import se.crafted.chrisb.ecoCreature.utils.ecoLogger;

public class ecoRewardManager
{
    private final ecoCreature plugin;

    private ecoLogger log;

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
    public static Boolean isPercentPvpReward;
    public static Double penaltyAmount;
    public static double pvpRewardAmount;
    public static Boolean canHuntUnderSeaLevel;
    public static Boolean isWolverineMode;
    public static Boolean noFarm;

    public static HashMap<String, Double> groupMultiplier = new HashMap<String, Double>();
    public static HashMap<TIME_PERIOD, Double> timeMultiplier = new HashMap<TIME_PERIOD, Double>();
    public static HashMap<CreatureType, ecoReward> rewards;
    public static ecoReward spawnerReward;

    public ecoRewardManager(ecoCreature plugin)
    {
        this.plugin = plugin;
        this.log = plugin.getLogger();
    }

    public void registerPlayerDeath(EntityDeathEvent event)
    {
        Player player = (Player) event.getEntity();

        if (player == null) {
            return;
        }

        if (hasPVPReward && isPVPDeath(event)) {
            registerPVPReward(player, event);
        }
        else if (hasDeathPenalty) {
            registerDeathPenalty(player);
        }

        return;
    }

    public void registerPVPReward(Player player, EntityDeathEvent event)
    {
        if (!hasIgnoreCase(player, "ecoCreature.PVPReward")) {
            return;
        }

        Double amount = isPercentPvpReward ? ecoCreature.economy.getBalance(player.getName()) * (pvpRewardAmount / 100.0D) : pvpRewardAmount;
        if (amount > 0.0D) {
            if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
                if (damager instanceof Player) {

                    ecoCreature.economy.withdrawPlayer(player.getName(), amount);
                    plugin.getMessageManager().sendMessage(ecoMessageManager.deathPenaltyMessage, player, amount);

                    Player killer = (Player) damager;
                    ecoCreature.economy.depositPlayer(killer.getName(), amount);
                    plugin.getMessageManager().sendMessage(ecoMessageManager.pvpRewardMessage, killer, amount, player.getName(), "");
                }
            }
        }
    }

    public void registerDeathPenalty(Player player)
    {
        if (!hasIgnoreCase(player, "ecoCreature.DeathPenalty")) {
            return;
        }

        Double amount = isPercentPenalty ? ecoCreature.economy.getBalance(player.getName()) * (penaltyAmount / 100.0D) : penaltyAmount;
        if (amount > 0.0D) {
            ecoCreature.economy.withdrawPlayer(player.getName(), amount);
            plugin.getMessageManager().sendMessage(ecoMessageManager.deathPenaltyMessage, player, amount);
        }
    }

    public void registerCreatureDeath(Player player, LivingEntity tamedCreature, LivingEntity killedCreature)
    {
        if (player == null || killedCreature == null) {
            return;
        }

        if (!hasIgnoreCase(player, "ecoCreature.Creature.Craft"  + ecoEntityUtil.getCreatureType(killedCreature).getName())) {
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
            log.info("Unrecognized creature: " + killedCreature.getClass().getSimpleName());
            return;
        }

        registerReward(player, reward, weaponName);
    }

    public void registerSpawnerBreak(Player player, Block block)
    {
        if (player == null || block == null) {
            return;
        }

        if (!block.getType().equals(Material.MOB_SPAWNER)) {
            return;
        }

        if (hasIgnoreCase(player, "ecoCreature.Creature.Spawner")) {

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
            ecoCreature.economy.withdrawPlayer(player.getName(), Math.abs(amount));
            plugin.getMessageManager().sendMessage(reward.getPenaltyMessage(), player, Math.abs(amount), reward.getCreatureName(), weaponName);
        }
        else {
            plugin.getMessageManager().sendMessage(reward.getNoRewardMessage(), player, reward.getCreatureName(), weaponName);
        }
    }

    private Double computeReward(Player player, ecoReward reward)
    {
        Double amount = reward.getRewardAmount();
        Double groupAmount = 0D;
        Double timeAmount = 0D;

        if (isIntegerCurrency) {
            amount = (double) Math.round(amount);
        }

        try {
            String group = ecoCreature.permission.getPrimaryGroup(player.getWorld().getName(), player.getName()).toLowerCase();
            if (groupMultiplier.containsKey(group)) {
                groupAmount = amount * groupMultiplier.get(group) - amount;
            }

            timeAmount = amount * timeMultiplier.get(ecoEntityUtil.getTimePeriod(player)) - amount;
        }
        catch (Exception exception) {
            log.warning("Permissions does not support group multiplier");
        }

        return amount + groupAmount + timeAmount;
    }

    private Boolean isPVPDeath(EntityDeathEvent event)
    {
        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
            return damager instanceof Player;
        }
        
        return false;
    }

    private Boolean hasIgnoreCase(Player player, String perm)
    {
        return ecoCreature.permission.has(player, perm) || ecoCreature.permission.has(player, perm.toLowerCase());
    }
}