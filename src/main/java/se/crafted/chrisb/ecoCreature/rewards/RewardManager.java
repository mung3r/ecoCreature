package se.crafted.chrisb.ecoCreature.rewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.api.PartyAPI;
import com.herocraftonline.heroes.characters.Hero;

import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageManager;
import se.crafted.chrisb.ecoCreature.metrics.MetricsManager;
import se.crafted.chrisb.ecoCreature.rewards.gain.Gain;

public class RewardManager
{
    private static Random random = new Random();

    public boolean isIntegerCurrency;

    public boolean canCampSpawner;
    public boolean campByDistance;
    public boolean campByEntity;
    public boolean shouldClearDefaultDrops;
    public boolean shouldOverrideDrops;
    public boolean isFixedDrops;
    public boolean shouldClearCampDrops;
    public int campRadius;
    public boolean hasBowRewards;
    public boolean hasDeathPenalty;
    public boolean hasPVPReward;
    public boolean isPercentPenalty;
    public boolean isPercentPvpReward;
    public double penaltyAmount;
    public double pvpRewardAmount;
    public boolean canHuntUnderSeaLevel;
    public boolean isWolverineMode;
    public boolean noFarm;
    public boolean noFarmFire;
    public boolean hasMobArenaRewards;
    public boolean hasCreativeModeRewards;
    public boolean isMobArenaShare;
    public boolean isHeroesPartyShare;
    public boolean isMcMMOPartyShare;

    private MetricsManager metricsManager;
    private MessageManager messageManager;
    private Map<RewardType, List<Reward>> rewards;
    private Set<Gain> gainMultipliers;

    private Set<Integer> spawnerMobs;

    public RewardManager(MetricsManager metricsManager, MessageManager messageManager)
    {
        this.metricsManager = metricsManager;
        this.messageManager = messageManager;
        rewards = new HashMap<RewardType, List<Reward>>();
        gainMultipliers = new HashSet<Gain>();

        spawnerMobs = new HashSet<Integer>();
    }

    public void setRewards(Map<RewardType, List<Reward>> rewards)
    {
        this.rewards = rewards;
    }

    public void setGainMultipliers(Set<Gain> gainMultipliers)
    {
        this.gainMultipliers = gainMultipliers;
    }

    public boolean isSpawnerMob(Entity entity)
    {
        return spawnerMobs.remove(Integer.valueOf(entity.getEntityId()));
    }

    public void setSpawnerMob(Entity entity)
    {
        // Only add to the array if we're tracking by entity. Avoids a memory leak.
        if (!canCampSpawner && campByEntity) {
            spawnerMobs.add(Integer.valueOf(entity.getEntityId()));
        }
    }

    public void registerPVPReward(PlayerKilledByPlayerEvent event)
    {
        if (!hasPVPReward || !DependencyUtils.hasPermission(event.getKiller(), "reward.player")) {
            return;
        }

        double amount = 0.0D;

        if (hasReward(RewardType.PLAYER)) {
            Reward reward = getRewardForType(RewardType.PLAYER);
            amount = reward.getCoin().getOutcome() * getGainMultiplier(event.getKiller());
            if (reward.hasDrops() && shouldOverrideDrops) {
                event.getDrops().clear();
            }
            event.getDrops().addAll(reward.getDropOutcomes(isFixedDrops));
            if (reward.hasExp()) {
                event.setDroppedExp(reward.getExp().getOutcome());
            }
        }
        else if (DependencyUtils.hasEconomy()) {
            amount = isPercentPvpReward ? DependencyUtils.getEconomy().getBalance(event.getVictim().getName()) * (pvpRewardAmount / 100.0D) : pvpRewardAmount;
        }

        if (isIntegerCurrency) {
            amount = Math.round(amount);
        }
        if (amount > 0.0D && DependencyUtils.hasEconomy()) {
            amount = Math.min(amount, DependencyUtils.getEconomy().getBalance(event.getVictim().getName()));
            DependencyUtils.getEconomy().withdrawPlayer(event.getVictim().getName(), amount);
            messageManager.deathPenaltyMessage(messageManager.deathPenaltyMessage, event.getVictim(), amount);

            DependencyUtils.getEconomy().depositPlayer(event.getKiller().getName(), amount);
            messageManager.rewardMessage(messageManager.pvpRewardMessage, event.getKiller(), amount, event.getVictim().getName(), event.getWeaponName());
        }
    }

    public void registerDeathPenalty(Player player)
    {
        if (!hasDeathPenalty || !DependencyUtils.hasPermission(player, "reward.deathpenalty") || !DependencyUtils.hasEconomy()) {
            return;
        }

        double amount = isPercentPenalty ? DependencyUtils.getEconomy().getBalance(player.getName()) * (penaltyAmount / 100.0D) : penaltyAmount;
        if (isIntegerCurrency) {
            amount = Math.round(amount);
        }
        if (amount > 0.0D) {
            DependencyUtils.getEconomy().withdrawPlayer(player.getName(), amount);
            messageManager.deathPenaltyMessage(messageManager.deathPenaltyMessage, player, amount);
        }
    }

    public void registerCreatureDeath(CreatureKilledByPlayerEvent event)
    {
        if (shouldClearDefaultDrops) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }

        if (EntityUtils.getItemTypeInHand(event.getKiller()).equals(Material.BOW) && !hasBowRewards) {
            messageManager.basicMessage(messageManager.noBowRewardMessage, event.getKiller());
            return;
        }

        if (EntityUtils.isUnderSeaLevel(event.getKiller()) && !canHuntUnderSeaLevel) {
            messageManager.basicMessage(messageManager.noBowRewardMessage, event.getKiller());
            return;
        }

        if (event.usedTamedCreature() && !isWolverineMode) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " killing with their pet.");
            return;
        }

        if (EntityUtils.isOwner(event.getKiller(), event.getKilledCreature())) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " killing pets.");
            return;
        }

        if (DependencyUtils.hasMobArena() && DependencyUtils.getMobArenaHandler().isPlayerInArena(event.getKiller()) && !hasMobArenaRewards) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " in Mob Arena.");
            return;
        }

        if (!hasCreativeModeRewards && event.getKiller().getGameMode() == GameMode.CREATIVE) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " in creative mode.");
            return;
        }

        if (!canCampSpawner && (campByDistance || campByEntity)) {
            // Reordered the conditional slightly, to make it more efficient,
            // since java will stop evaluating when it knows the outcome.
            if ((campByEntity && isSpawnerMob(event.getKilledCreature())) || (campByDistance && (EntityUtils.isNearSpawner(event.getKiller(), campRadius) || EntityUtils.isNearSpawner(event.getKilledCreature(), campRadius)))) {
                if (shouldClearCampDrops) {
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                }
                messageManager.spawnerMessage(messageManager.noCampMessage, event.getKiller());
                ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " spawn camping.");
                return;
            }
        }

        if (!DependencyUtils.hasPermission(event.getKiller(), "reward." + RewardType.fromEntity(event.getKilledCreature()).getName())) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " due to lack of permission for " + RewardType.fromEntity(event.getKilledCreature()).getName());
            return;
        }

        if (hasReward(event.getKilledCreature())) {
            Reward reward = getRewardForEntity(event.getKilledCreature());

            if (reward.hasExp()) {
                event.setDroppedExp(reward.getExp().getOutcome());
            }
            registerReward(event.getKiller(), reward, event.getWeaponName());
            try {
                List<ItemStack> rewardDrops = reward.getDropOutcomes(isFixedDrops);
                if (!rewardDrops.isEmpty()) {
                    if (!event.getDrops().isEmpty() && shouldOverrideDrops) {
                        event.getDrops().clear();
                    }
                    event.getDrops().addAll(rewardDrops);
                }
            }
            catch (IllegalArgumentException e) {
                ECLogger.getInstance().warning(e.getMessage());
            }
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

        if (DependencyUtils.hasPermission(player, "reward.spawner") && rewards.containsKey(RewardType.SPAWNER)) {

            if (hasReward(RewardType.SPAWNER)) {
                Reward reward = getRewardForType(RewardType.SPAWNER);
                registerReward(player, reward, EntityUtils.getItemNameInHand(player));

                for (ItemStack itemStack : reward.getDropOutcomes(isFixedDrops)) {
                    block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
                }
            }
        }
    }

    public void registerDeathStreak(Player player, int deaths)
    {
        if (DependencyUtils.hasPermission(player, "reward.deathstreak") && rewards.containsKey(RewardType.DEATH_STREAK)) {

            if (hasReward(RewardType.DEATH_STREAK)) {
                Reward reward = getRewardForType(RewardType.DEATH_STREAK);
                // TODO: incorrectly modifies the reward for subsequent calls
                reward.getCoin().setMin(reward.getCoin().getMin() * deaths);
                reward.getCoin().setMax(reward.getCoin().getMax() * deaths);
                registerReward(player, reward, "");

                for (ItemStack itemStack : reward.getDropOutcomes(isFixedDrops)) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                }
            }
        }
    }

    public void registerKillStreak(Player player, int kills)
    {
        if (DependencyUtils.hasPermission(player, "reward.killstreak") && rewards.containsKey(RewardType.KILL_STREAK)) {

            if (hasReward(RewardType.KILL_STREAK)) {
                Reward reward = getRewardForType(RewardType.KILL_STREAK);
                // TODO: incorrectly modifies the reward for subsequent calls
                reward.getCoin().setMin(reward.getCoin().getMin() * kills);
                reward.getCoin().setMax(reward.getCoin().getMax() * kills);
                registerReward(player, reward, "");

                for (ItemStack itemStack : reward.getDropOutcomes(isFixedDrops)) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                }
            }
        }
    }

    public void registerHeroMasteredReward(Hero hero)
    {
        if (DependencyUtils.hasPermission(hero.getPlayer(), "reward.hero_mastered") && rewards.containsKey(RewardType.HERO_MASTERED)) {

            if (hasReward(RewardType.HERO_MASTERED)) {
                Reward reward = getRewardForType(RewardType.HERO_MASTERED);
                registerReward(hero.getPlayer(), reward, "");
            }
        }
    }

    public void handleNoFarm(EntityDeathEvent event)
    {
        if (noFarm) {
            EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

            if (damageEvent != null) {
                if (damageEvent instanceof EntityDamageByBlockEvent && damageEvent.getCause().equals(DamageCause.CONTACT)) {
                    event.getDrops().clear();
                }
                else if (damageEvent.getCause() != null) {
                    if (damageEvent.getCause().equals(DamageCause.FALL) || damageEvent.getCause().equals(DamageCause.DROWNING) || damageEvent.getCause().equals(DamageCause.SUFFOCATION)) {
                        event.getDrops().clear();
                    }
                    else if (noFarmFire && (damageEvent.getCause().equals(DamageCause.FIRE) || damageEvent.getCause().equals(DamageCause.FIRE_TICK))) {
                        event.getDrops().clear();
                    }
                }
            }
        }
    }

    private boolean hasReward(Entity entity)
    {
        boolean hasReward = false;

        if (entity != null) {
            hasReward(RewardType.fromEntity(entity));
        }

        return hasReward;
    }

    private boolean hasReward(RewardType type)
    {
        return rewards.containsKey(type) && !rewards.get(type).isEmpty();
    }

    private Reward getRewardForEntity(Entity entity)
    {
        Reward reward = null;

        if (hasReward(entity)) {
            reward = getRewardForType(RewardType.fromEntity(entity));
        }
        else {
            ECLogger.getInstance().warning("No reward found for entity " + entity.getClass());
        }

        return reward;
    }

    private Reward getRewardForType(RewardType type)
    {
        Reward reward = null;

        if (hasReward(type)) {
            reward = rewards.get(type).get(random.nextInt(rewards.get(type).size()));
        }
        else {
            ECLogger.getInstance().warning("No reward defined for " + type);
        }

        return reward;
    }

    private void registerReward(Player player, Reward reward, String weaponName)
    {
        double amount = reward.hasCoin() ? reward.getCoin().getOutcome() * getGainMultiplier(player) : 0.0;
        List<Player> party = new ArrayList<Player>();

        if (isHeroesPartyShare && DependencyUtils.hasHeroes() && DependencyUtils.getHeroes().getCharacterManager().getHero(player).hasParty()) {
            for (Hero hero : DependencyUtils.getHeroes().getCharacterManager().getHero(player).getParty().getMembers()) {
                party.add(hero.getPlayer());
            }
            amount /= (double) party.size();
        }
        else if (isMcMMOPartyShare && DependencyUtils.hasMcMMO() && PartyAPI.inParty(player)) {
            party.addAll(PartyAPI.getOnlineMembers(player));
            amount /= (double) party.size();
        }
        else if (isMobArenaShare && DependencyUtils.hasMobArena() && DependencyUtils.getMobArenaHandler().isPlayerInArena(player)) {
            party.addAll(DependencyUtils.getMobArenaHandler().getArenaWithPlayer(player).getAllPlayers());
            amount /= (double) party.size();
        }
        else {
            party.add(player);
        }

        if (isIntegerCurrency) {
            amount = Math.round(amount);
        }
        for (Player member : party) {
            if (amount > 0.0D && DependencyUtils.hasEconomy()) {
                DependencyUtils.getEconomy().depositPlayer(member.getName(), amount);
                messageManager.rewardMessage(reward.getRewardMessage(), member, amount, reward.getName(), weaponName);
            }
            else if (amount < 0.0D && DependencyUtils.hasEconomy()) {
                DependencyUtils.getEconomy().withdrawPlayer(member.getName(), Math.abs(amount));
                messageManager.penaltyMessage(reward.getPenaltyMessage(), member, amount, reward.getName(), weaponName);
            }
            else {
                messageManager.noRewardMessage(reward.getNoRewardMessage(), member, reward.getName(), weaponName);
            }
        }

        metricsManager.addCount(reward.getType());
    }

    private double getGainMultiplier(Player player)
    {
        double multiplier = 1.0;

        for (Gain gain : gainMultipliers) {
            if (gain != null) {
                multiplier *= gain.getMultiplier(player);
            }
        }

        return multiplier;
    }
}
