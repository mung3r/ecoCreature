package se.crafted.chrisb.ecoCreature.managers;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoReward;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil.TIME_PERIOD;
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
    public Boolean noFarm;

    public HashMap<String, Double> groupMultiplier = new HashMap<String, Double>();
    public HashMap<TIME_PERIOD, Double> timeMultiplier = new HashMap<TIME_PERIOD, Double>();
    public HashMap<CreatureType, ecoReward> rewards;
    public ecoReward spawnerReward;

    private final ecoCreature plugin;
    private final ecoLogger log;

    public ecoRewardManager(ecoCreature plugin)
    {
        this.plugin = plugin;
        log = this.plugin.getLogger();
    }

    @Override
    public ecoRewardManager clone()
    {
        try {
            return (ecoRewardManager) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void registerPVPReward(Player player, Player damager)
    {
        if (!hasPVPReward || !hasIgnoreCase(player, "ecoCreature.PVPReward") || !plugin.hasEconomy()) {
            return;
        }

        Double amount = isPercentPvpReward ? ecoCreature.economy.getBalance(player.getName()) * (pvpRewardAmount / 100.0D) : pvpRewardAmount;
        if (amount > 0.0D) {
            ecoCreature.economy.withdrawPlayer(player.getName(), amount);
            ecoCreature.getMessageManager(player).sendMessage(ecoCreature.getMessageManager(player).deathPenaltyMessage, player, amount);

            Player killer = (Player) damager;
            ecoCreature.economy.depositPlayer(killer.getName(), amount);
            ecoCreature.getMessageManager(player).sendMessage(ecoCreature.getMessageManager(player).pvpRewardMessage, killer, amount, player.getName(), "");
        }
    }

    public void registerDeathPenalty(Player player)
    {
        if (!hasDeathPenalty || !hasIgnoreCase(player, "ecoCreature.DeathPenalty") || !plugin.hasEconomy()) {
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
        else if (!hasIgnoreCase(killer, "ecoCreature.Creature.Craft" + ecoEntityUtil.getCreatureType(killedCreature).getName())) {
            return;
        }

        ecoReward reward = rewards.get(ecoEntityUtil.getCreatureType(killedCreature));

        if (reward == null) {
            log.warning("Unrecognized creature");
        }
        else {
            String weaponName = tamedCreature != null ? ecoEntityUtil.getCreatureType(tamedCreature).getName() : Material.getMaterial(killer.getItemInHand().getTypeId()).name();
            registerReward(killer, reward, weaponName);
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

        if (hasIgnoreCase(player, "ecoCreature.Creature.Spawner")) {

            registerReward(player, spawnerReward, Material.getMaterial(player.getItemInHand().getTypeId()).name());

            for (ItemStack itemStack : spawnerReward.computeDrops()) {
                block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
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
            if (warnGroupMultiplierSupport) {
                log.warning("Permissions does not support group multiplier");
                warnGroupMultiplierSupport = false;
            }
        }

        return amount + groupAmount + timeAmount;
    }

    private Boolean hasIgnoreCase(Player player, String perm)
    {
        return ecoCreature.permission.has(player, perm) || ecoCreature.permission.has(player, perm.toLowerCase());
    }
}