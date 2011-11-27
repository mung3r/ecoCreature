package se.crafted.chrisb.ecoCreature.managers;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoReward;
import se.crafted.chrisb.ecoCreature.models.ecoReward.RewardType;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil.TimePeriod;
import se.crafted.chrisb.ecoCreature.utils.ecoLogger;

public class ecoRewardManager implements Cloneable
{
    public static Boolean warnGroupMultiplierSupport = true;

    public Boolean isIntegerCurrency;

    public Boolean canCampSpawner;
    public Boolean shouldOverrideDrops;
    public Boolean isFixedDrops;
    public Boolean shouldClearCampDrops;
    public int campRadius;
    public Boolean hasBowRewards;
    public Boolean hasDeathPenalty;
    public Boolean hasPVPReward;
    public Boolean isPercentPenalty;
    public Boolean isPercentPvpReward;
    public Double penaltyAmount;
    public double pvpRewardAmount;
    public Boolean canHuntUnderSeaLevel;
    public Boolean isWolverineMode;
    public Boolean hasDTPRewards;
    public double dtpRewardAmount;
    public double dtpPenaltyAmount;
    public Boolean noFarm;

    public HashMap<String, Double> groupMultiplier;
    public HashMap<TimePeriod, Double> timeMultiplier;
    public HashMap<Environment, Double> envMultiplier;
    public HashMap<RewardType, ecoReward> rewards;

    private final ecoCreature plugin;
    private final ecoLogger log;

    public ecoRewardManager(ecoCreature plugin)
    {
        this.plugin = plugin;
        log = this.plugin.getLogger();

        groupMultiplier = new HashMap<String, Double>();
        timeMultiplier = new HashMap<TimePeriod, Double>();
        envMultiplier = new HashMap<Environment, Double>();
        rewards = new HashMap<RewardType, ecoReward>();
    }

    public void registerPVPReward(Player player, Player damager, List<ItemStack> drops)
    {
        if (!hasPVPReward || !hasPermission(player, "reward.player")) {
            return;
        }

        Double amount = 0.0D;

        if (rewards.containsKey(RewardType.PLAYER)) {
            ecoReward reward = rewards.get(RewardType.PLAYER);

            amount = computeReward(player, reward);
            if (!drops.isEmpty() && shouldOverrideDrops) {
                drops.clear();
            }
            drops.addAll(reward.computeDrops());
        }
        else if (plugin.hasEconomy()) {
            amount = isPercentPvpReward ? ecoCreature.economy.getBalance(player.getName()) * (pvpRewardAmount / 100.0D) : pvpRewardAmount;
        }

        if (amount > 0.0D && plugin.hasEconomy()) {
            amount = Math.min(amount, ecoCreature.economy.getBalance(player.getName()));
            ecoCreature.economy.withdrawPlayer(player.getName(), amount);
            ecoCreature.getMessageManager(player).sendMessage(ecoCreature.getMessageManager(player).deathPenaltyMessage, player, amount);

            Player killer = (Player) damager;
            ecoCreature.economy.depositPlayer(killer.getName(), amount);
            ecoCreature.getMessageManager(player).sendMessage(ecoCreature.getMessageManager(player).pvpRewardMessage, killer, amount, player.getName(), "");
        }
    }

    public void registerDeathPenalty(Player player)
    {
        if (!hasDeathPenalty || !hasPermission(player, "reward.deathpenalty") || !plugin.hasEconomy()) {
            return;
        }

        Double amount = isPercentPenalty ? ecoCreature.economy.getBalance(player.getName()) * (penaltyAmount / 100.0D) : penaltyAmount;
        if (amount > 0.0D) {
            ecoCreature.economy.withdrawPlayer(player.getName(), amount);
            ecoCreature.getMessageManager(player).sendMessage(ecoCreature.getMessageManager(player).deathPenaltyMessage, player, amount);
        }
    }

    public void registerCreatureDeath(Player killer, LivingEntity tamedCreature, LivingEntity killedCreature, List<ItemStack> drops)
    {
        if (killer.getItemInHand().getType().equals(Material.BOW) && !hasBowRewards) {
            ecoCreature.getMessageManager(killer).sendMessage(ecoCreature.getMessageManager(killer).noBowRewardMessage, killer);
            return;
        }
        else if (ecoEntityUtil.isUnderSeaLevel(killer) && !canHuntUnderSeaLevel) {
            ecoCreature.getMessageManager(killer).sendMessage(ecoCreature.getMessageManager(killer).noBowRewardMessage, killer);
            return;
        }
        else if (ecoEntityUtil.isOwner(killer, killedCreature)) {
            // TODO: message no killing your own pets?
            return;
        }
        else if (ecoCreature.mobArenaHandler != null && ecoCreature.mobArenaHandler.isPlayerInArena(killer)) {
            // TODO: message no arena awards?
            return;
        }
        else if ((ecoEntityUtil.isNearSpawner(killer) || ecoEntityUtil.isNearSpawner(killedCreature)) && !canCampSpawner) {
            if (shouldClearCampDrops) {
                drops.clear();
            }
            ecoCreature.getMessageManager(killer).sendMessage(ecoCreature.getMessageManager(killer).noCampMessage, killer);
            return;
        }
        else if (!hasPermission(killer, "reward." + RewardType.fromEntity(killedCreature).getName())) {
            return;
        }

        ecoReward reward = rewards.get(RewardType.fromEntity(killedCreature));

        if (reward == null) {
            log.warning("Unrecognized reward");
        }
        else {
            String weaponName = tamedCreature != null ? RewardType.fromEntity(tamedCreature).getName() : Material.getMaterial(killer.getItemInHand().getTypeId()).name();
            registerReward(killer, reward, weaponName);
            if (!drops.isEmpty() && shouldOverrideDrops) {
                drops.clear();
            }
            drops.addAll(reward.computeDrops());
        }
    }

    public void registerSpawnerBreak(Player player, Block block)
    {
        if (player == null || block == null) {
            return;
        }

        if (!block.getType().equals(Material.MOB_SPAWNER)) {
            return;
        }

        if (hasPermission(player, "reward.spawner") && rewards.containsKey(RewardType.SPAWNER)) {

            registerReward(player, rewards.get(RewardType.SPAWNER), Material.getMaterial(player.getItemInHand().getTypeId()).name());

            for (ItemStack itemStack : rewards.get(RewardType.SPAWNER).computeDrops()) {
                block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
            }
        }
    }

    public void registerDeathStreak(Player player)
    {
        if (hasPermission(player, "reward.deathstreak") && rewards.containsKey(RewardType.DEATH_STREAK)) {

            registerReward(player, rewards.get(RewardType.DEATH_STREAK), "");

            for (ItemStack itemStack : rewards.get(RewardType.DEATH_STREAK).computeDrops()) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            }
        }
    }

    public void registerKillStreak(Player player)
    {
        if (hasPermission(player, "reward.killstreak") && rewards.containsKey(RewardType.KILL_STREAK)) {

            registerReward(player, rewards.get(RewardType.KILL_STREAK), "");

            for (ItemStack itemStack : rewards.get(RewardType.KILL_STREAK).computeDrops()) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            }
        }
    }

    public void handleNoFarm(EntityDeathEvent event)
    {
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

        if (damageEvent != null) {
            if (damageEvent instanceof EntityDamageByBlockEvent && damageEvent.getCause().equals(DamageCause.CONTACT)) {
                event.getDrops().clear();
            }
            else if (damageEvent.getCause() != null && (damageEvent.getCause().equals(DamageCause.FALL) || damageEvent.getCause().equals(DamageCause.DROWNING) || damageEvent.getCause().equals(DamageCause.SUFFOCATION))) {
                event.getDrops().clear();
            }
        }
    }

    private void registerReward(Player player, ecoReward reward, String weaponName)
    {
        Double amount = computeReward(player, reward);

        if (amount > 0.0D && plugin.hasEconomy()) {
            ecoCreature.economy.depositPlayer(player.getName(), amount);
            ecoCreature.getMessageManager(player).sendMessage(reward.getRewardMessage(), player, amount, reward.getCreatureName(), weaponName);
        }
        else if (amount < 0.0D && plugin.hasEconomy()) {
            ecoCreature.economy.withdrawPlayer(player.getName(), Math.abs(amount));
            ecoCreature.getMessageManager(player).sendMessage(reward.getPenaltyMessage(), player, Math.abs(amount), reward.getCreatureName(), weaponName);
        }
        else {
            ecoCreature.getMessageManager(player).sendMessage(reward.getNoRewardMessage(), player, reward.getCreatureName(), weaponName);
        }
    }

    private Double computeReward(Player player, ecoReward reward)
    {
        Double amount = reward.getRewardAmount();
        Double groupAmount = 0D;
        Double timeAmount = 0D;
        Double envAmount = 0D;

        if (isIntegerCurrency) {
            amount = (double) Math.round(amount);
        }

        try {
            String group = ecoCreature.permission.getPrimaryGroup(player.getWorld().getName(), player.getName()).toLowerCase();
            if (hasPermission(player, "gain.group") && groupMultiplier.containsKey(group)) {
                groupAmount = amount * groupMultiplier.get(group) - amount;
            }

            if (hasPermission(player, "gain.time") && timeMultiplier.containsKey(ecoEntityUtil.getTimePeriod(player))) {
                timeAmount = amount * timeMultiplier.get(ecoEntityUtil.getTimePeriod(player)) - amount;
            }

            if (hasPermission(player, "gain.environment") && envMultiplier.containsKey(player.getWorld().getEnvironment())) {
                envAmount = amount * envMultiplier.get(player.getWorld().getEnvironment()) - amount;
            }
        }
        catch (Exception exception) {
            if (warnGroupMultiplierSupport) {
                log.warning("Permissions does not support group multiplier");
                warnGroupMultiplierSupport = false;
            }
        }

        log.debug("base amount is " + amount);
        log.debug("group amount is " + groupAmount);
        log.debug("time amount is " + timeAmount);
        log.debug("env amount is " + envAmount);

        return amount + groupAmount + timeAmount + envAmount;
    }

    private Boolean hasPermission(Player player, String perm)
    {
        return ecoCreature.permission.has(player, "ecoCreature." + perm) || ecoCreature.permission.has(player, "ecocreature." + perm.toLowerCase());
    }
}