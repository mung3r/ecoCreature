package se.crafted.chrisb.ecoCreature.rewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.gmail.nossr50.api.PartyAPI;
import com.herocraftonline.heroes.characters.Hero;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import couk.Adamki11s.Regios.Regions.Region;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.Utils;
import se.crafted.chrisb.ecoCreature.commons.TimePeriod;
import se.crafted.chrisb.ecoCreature.events.CreatureKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledByPlayerEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageManager;

public class RewardManager
{
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
    public double mobArenaMultiplier;
    public boolean isMobArenaShare;
    public double heroesPartyMultiplier;
    public boolean isHeroesPartyShare;
    public double mcMMOPartyMultiplier;
    public boolean isMcMMOPartyShare;

    public Map<String, Double> groupMultiplier;
    public Map<TimePeriod, Double> timeMultiplier;
    public Map<Environment, Double> envMultiplier;
    public Map<String, Double> worldGuardMultiplier;
    public Map<String, Double> regiosMultiplier;
    public Map<String, Double> residenceMultiplier;
    public Map<String, Double> factionsMultiplier;
    public Map<String, Double> townyMultiplier;
    public Map<RewardType, List<Reward>> rewards;

    private final ecoCreature plugin;
    private MessageManager messageManager;
    private boolean warnGroupMultiplierSupport;
    private Set<Integer> spawnerMobs;

    public RewardManager(ecoCreature plugin, MessageManager messageManager)
    {
        this.plugin = plugin;
        this.messageManager = messageManager;
        warnGroupMultiplierSupport = true;
        spawnerMobs = new HashSet<Integer>();

        groupMultiplier = new HashMap<String, Double>();
        timeMultiplier = new HashMap<TimePeriod, Double>();
        envMultiplier = new HashMap<Environment, Double>();
        worldGuardMultiplier = new HashMap<String, Double>();
        regiosMultiplier = new HashMap<String, Double>();
        residenceMultiplier = new HashMap<String, Double>();
        factionsMultiplier = new HashMap<String, Double>();
        townyMultiplier = new HashMap<String, Double>();
        rewards = new HashMap<RewardType, List<Reward>>();
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
        if (!hasPVPReward || !ecoCreature.hasPermission(event.getKiller(), "reward.player")) {
            return;
        }

        double amount = 0.0D;

        if (rewards.containsKey(RewardType.PLAYER)) {
            Reward reward = getRewardFromType(RewardType.PLAYER);

            if (reward != null) {
                Integer exp = reward.getExpAmount();

                amount = computeReward(event.getVictim(), reward);
                if (!reward.getDrops().isEmpty() && shouldOverrideDrops) {
                    event.getDrops().clear();
                }
                event.getDrops().addAll(reward.computeDrops());
                if (exp != null) {
                    event.setDroppedExp(exp);
                }
            }
        }
        else if (ecoCreature.hasEconomy()) {
            amount = isPercentPvpReward ? ecoCreature.getEconomy().getBalance(event.getVictim().getName()) * (pvpRewardAmount / 100.0D) : pvpRewardAmount;
        }

        if (amount > 0.0D && ecoCreature.hasEconomy()) {
            amount = Math.min(amount, ecoCreature.getEconomy().getBalance(event.getVictim().getName()));
            ecoCreature.getEconomy().withdrawPlayer(event.getVictim().getName(), amount);
            messageManager.penaltyMessage(messageManager.deathPenaltyMessage, event.getVictim(), amount);

            ecoCreature.getEconomy().depositPlayer(event.getKiller().getName(), amount);
            messageManager.rewardMessage(messageManager.pvpRewardMessage, event.getKiller(), amount, event.getVictim().getName(), "");
        }
    }

    public void registerDeathPenalty(Player player)
    {
        if (!hasDeathPenalty || !ecoCreature.hasPermission(player, "reward.deathpenalty") || !ecoCreature.hasEconomy()) {
            return;
        }

        double amount = isPercentPenalty ? ecoCreature.getEconomy().getBalance(player.getName()) * (penaltyAmount / 100.0D) : penaltyAmount;
        if (amount > 0.0D) {
            ecoCreature.getEconomy().withdrawPlayer(player.getName(), amount);
            messageManager.penaltyMessage(messageManager.deathPenaltyMessage, player, amount);
        }
    }

    public void registerCreatureDeath(CreatureKilledByPlayerEvent event)
    {
        if (shouldClearDefaultDrops) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }

        if (event.getKiller().getItemInHand().getType().equals(Material.BOW) && !hasBowRewards) {
            messageManager.basicMessage(messageManager.noBowRewardMessage, event.getKiller());
            return;
        }

        if (Utils.isUnderSeaLevel(event.getKiller()) && !canHuntUnderSeaLevel) {
            messageManager.basicMessage(messageManager.noBowRewardMessage, event.getKiller());
            return;
        }

        if (event.getTamedCreature() != null && !isWolverineMode) {
            ecoCreature.getECLogger().debug("No reward for " + event.getKiller().getName() + " killing with their pet.");
            return;
        }

        if (Utils.isOwner(event.getKiller(), event.getKilledCreature())) {
            ecoCreature.getECLogger().debug("No reward for " + event.getKiller().getName() + " killing pets.");
            return;
        }

        if (plugin.hasMobArena() && plugin.getMobArenaHandler().isPlayerInArena(event.getKiller()) && !hasMobArenaRewards) {
            ecoCreature.getECLogger().debug("No reward for " + event.getKiller().getName() + " in Mob Arena.");
            return;
        }

        if (!hasCreativeModeRewards && event.getKiller().getGameMode() == GameMode.CREATIVE) {
            ecoCreature.getECLogger().debug("No reward for " + event.getKiller().getName() + " in creative mode.");
            return;
        }

        if (!canCampSpawner && (campByDistance || campByEntity)) {
            // Reordered the conditional slightly, to make it more efficient, since
            // java will stop evaluating when it knows the outcome.
            if ((campByEntity && isSpawnerMob(event.getKilledCreature())) || (campByDistance && (isNearSpawner(event.getKiller()) || isNearSpawner(event.getKilledCreature())))) {
                if (shouldClearCampDrops) {
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                }
                messageManager.spawnerMessage(messageManager.noCampMessage, event.getKiller());
                ecoCreature.getECLogger().debug("No reward for " + event.getKiller().getName() + " spawn camping.");
                return;
            }
        }

        if (!ecoCreature.hasPermission(event.getKiller(), "reward." + RewardType.fromEntity(event.getKilledCreature()).getName())) {
            ecoCreature.getECLogger().debug("No reward for " + event.getKiller().getName() + " due to lack of permission for " + RewardType.fromEntity(event.getKilledCreature()).getName());
            return;
        }

        Reward reward = getRewardFromEntity(event.getKilledCreature());

        if (reward != null) {
            Integer exp = reward.getExpAmount();
            if (exp != null) {
                event.setDroppedExp(exp);
            }
            String weaponName = event.getTamedCreature() != null ? RewardType.fromEntity(event.getTamedCreature()).getName() : Material.getMaterial(event.getKiller().getItemInHand().getTypeId()).name();
            registerReward(event.getKiller(), reward, weaponName);
            try {
                List<ItemStack> rewardDrops = reward.computeDrops();
                if (!rewardDrops.isEmpty()) {
                    if (!event.getDrops().isEmpty() && shouldOverrideDrops) {
                        event.getDrops().clear();
                    }
                    event.getDrops().addAll(rewardDrops);
                }
            }
            catch (IllegalArgumentException e) {
                ecoCreature.getECLogger().warning(e.getMessage());
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

        if (ecoCreature.hasPermission(player, "reward.spawner") && rewards.containsKey(RewardType.SPAWNER)) {

            Reward reward = getRewardFromType(RewardType.SPAWNER);

            if (reward != null) {
                registerReward(player, reward, Material.getMaterial(player.getItemInHand().getTypeId()).name());

                for (ItemStack itemStack : reward.computeDrops()) {
                    block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
                }
            }
        }
    }

    public void registerDeathStreak(Player player, int deaths)
    {
        if (ecoCreature.hasPermission(player, "reward.deathstreak") && rewards.containsKey(RewardType.DEATH_STREAK)) {

            Reward reward = getRewardFromType(RewardType.DEATH_STREAK);

            if (reward != null) {
                reward.setCoinMin(reward.getCoinMin() * deaths);
                reward.setCoinMax(reward.getCoinMax() * deaths);
                registerReward(player, reward, "");

                for (ItemStack itemStack : reward.computeDrops()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                }
            }
        }
    }

    public void registerKillStreak(Player player, int kills)
    {
        if (ecoCreature.hasPermission(player, "reward.killstreak") && rewards.containsKey(RewardType.KILL_STREAK)) {

            Reward reward = getRewardFromType(RewardType.KILL_STREAK);

            if (reward != null) {
                reward.setCoinMin(reward.getCoinMin() * kills);
                reward.setCoinMax(reward.getCoinMax() * kills);
                registerReward(player, reward, "");

                for (ItemStack itemStack : reward.computeDrops()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                }
            }
        }
    }

    public void registerHeroMasteredReward(Hero hero)
    {
        if (ecoCreature.hasPermission(hero.getPlayer(), "reward.hero_mastered") && rewards.containsKey(RewardType.HERO_MASTERED)) {

            Reward reward = getRewardFromType(RewardType.HERO_MASTERED);

            if (reward != null) {
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

    private Reward getRewardFromEntity(Entity entity)
    {
        RewardType rewardType = RewardType.fromEntity(entity);
        Reward reward = null;

        if (rewardType != null) {
            reward = getRewardFromType(rewardType);
        }
        else {
            ecoCreature.getECLogger().warning("No reward found for entity " + entity.getClass());
        }

        return reward;
    }

    private Reward getRewardFromType(RewardType rewardType)
    {
        Random random = new Random();
        Reward reward = null;

        if (rewards.containsKey(rewardType)) {
            List<Reward> rewardList = rewards.get(rewardType);
            if (rewardList.size() > 0) {
                reward = rewardList.get(random.nextInt(rewardList.size()));
            }
        }
        else {
            ecoCreature.getECLogger().warning("No reward defined for " + rewardType);
        }
        return reward;
    }

    private void registerReward(Player player, Reward reward, String weaponName)
    {
        double amount = computeReward(player, reward);
        List<Player> party = new ArrayList<Player>();

        if (isHeroesPartyShare && plugin.hasHeroes() && plugin.getHeroes().getCharacterManager().getHero(player).hasParty()) {
            for (Hero hero : plugin.getHeroes().getCharacterManager().getHero(player).getParty().getMembers()) {
                party.add(hero.getPlayer());
            }
            amount /= (double) party.size();
        }
        else if (isMcMMOPartyShare && plugin.hasMcMMO() && PartyAPI.inParty(player)) {
            party.addAll(PartyAPI.getOnlineMembers(player));
            amount /= (double) party.size();
        }
        else if (isMobArenaShare && plugin.hasMobArena() && plugin.getMobArenaHandler().isPlayerInArena(player)) {
            party.addAll(plugin.getMobArenaHandler().getArenaWithPlayer(player).getAllPlayers());
            amount /= (double) party.size();
        }
        else {
            party.add(player);
        }

        for (Player member : party) {
            if (amount > 0.0D && ecoCreature.hasEconomy()) {
                ecoCreature.getEconomy().depositPlayer(member.getName(), amount);
                messageManager.rewardMessage(reward.getRewardMessage(), member, amount, reward.getRewardName(), weaponName);
            }
            else if (amount < 0.0D && ecoCreature.hasEconomy()) {
                ecoCreature.getEconomy().withdrawPlayer(member.getName(), Math.abs(amount));
                messageManager.rewardMessage(reward.getPenaltyMessage(), member, Math.abs(amount), reward.getRewardName(), weaponName);
            }
            else {
                messageManager.noRewardMessage(reward.getNoRewardMessage(), member, reward.getRewardName(), weaponName);
            }
        }

        plugin.getMetrics().addCount(reward.getRewardType());
    }

    private double computeReward(Player player, Reward reward)
    {
        double amount = reward.getRewardAmount();

        try {
            if (ecoCreature.getPermission().getPrimaryGroup(player.getWorld().getName(), player.getName()) != null) {
                String group = ecoCreature.getPermission().getPrimaryGroup(player.getWorld().getName(), player.getName()).toLowerCase();
                if (ecoCreature.hasPermission(player, "gain.group") && groupMultiplier.containsKey(group)) {
                    amount *= groupMultiplier.get(group);
                }
            }
        }
        catch (UnsupportedOperationException e) {
            if (warnGroupMultiplierSupport) {
                ecoCreature.getECLogger().warning(e.getMessage());
                warnGroupMultiplierSupport = false;
            }
        }

        if (ecoCreature.hasPermission(player, "gain.time") && timeMultiplier.containsKey(TimePeriod.fromEntity(player))) {
            amount *= timeMultiplier.get(TimePeriod.fromEntity(player));
        }

        if (ecoCreature.hasPermission(player, "gain.environment") && envMultiplier.containsKey(player.getWorld().getEnvironment())) {
            amount *= envMultiplier.get(player.getWorld().getEnvironment());
        }

        if (ecoCreature.hasPermission(player, "gain.worldguard") && plugin.hasWorldGuard()) {
            RegionManager regionManager = plugin.getRegionManager(player.getWorld());
            if (regionManager != null) {
                Iterator<ProtectedRegion> regions = regionManager.getApplicableRegions(player.getLocation()).iterator();
                while (regions.hasNext()) {
                    String regionName = regions.next().getId();
                    if (worldGuardMultiplier.containsKey(regionName)) {
                        amount *= worldGuardMultiplier.get(regionName);
                        ecoCreature.getECLogger().debug("WorldGuard multiplier applied");
                    }
                }
            }
        }

        if (ecoCreature.hasPermission(player, "gain.regios") && plugin.hasRegios()) {
            Region region = plugin.getRegiosAPI().getRegion(player.getLocation());
            if (region != null && regiosMultiplier.containsKey(region.getName())) {
                amount *= regiosMultiplier.get(region.getName());
                ecoCreature.getECLogger().debug("Regios multiplier applied");
            }
        }

        if (ecoCreature.hasPermission(player, "gain.residence") && plugin.hasResidence()) {
            ClaimedResidence residence = Residence.getResidenceManager().getByLoc(player.getLocation());
            if (residence != null && residenceMultiplier.containsKey(residence.getName())) {
                amount *= residenceMultiplier.get(residence.getName());
                ecoCreature.getECLogger().debug("Residence multiplier applied");
            }
        }

        if (ecoCreature.hasPermission(player, "gain.heroes") && plugin.hasHeroes() && plugin.getHeroes().getCharacterManager().getHero(player).hasParty()) {
            amount *= heroesPartyMultiplier;
            ecoCreature.getECLogger().debug("Heroes multiplier applied");
        }

        if (ecoCreature.hasPermission(player, "gain.mcmmo") && plugin.hasMcMMO()) {
            amount *= mcMMOPartyMultiplier;
            ecoCreature.getECLogger().debug("mcMMO multiplier applied");
        }

        if (hasMobArenaRewards && ecoCreature.hasPermission(player, "gain.mobarena") && plugin.hasMobArena() && plugin.getMobArenaHandler().isPlayerInArena(player)) {
            amount *= mobArenaMultiplier;
            ecoCreature.getECLogger().debug("MobArena multiplier applied");
        }

        if (ecoCreature.hasPermission(player, "gain.factions") && plugin.hasFactions()) {
            Faction faction = Board.getFactionAt(new FLocation(player.getLocation()));
            if (faction != null && factionsMultiplier.containsKey(faction.getTag())) {
                amount *= factionsMultiplier.get(faction.getTag());
                ecoCreature.getECLogger().debug("Factions multiplier applied");
            }
        }

        if (ecoCreature.hasPermission(player, "gain.towny") && plugin.hasTowny()) {
            String townName = TownyUniverse.getTownName(player.getLocation());
            if (townName != null && townyMultiplier.containsKey(townName)) {
                amount *= townyMultiplier.get(townName);
                ecoCreature.getECLogger().debug("Towny multiplier applied");
            }
        }

        return isIntegerCurrency ? (double) Math.round(amount) : amount;
    }

    private boolean isNearSpawner(Entity entity)
    {
        Location loc = entity.getLocation();
        BlockState[] tileEntities = entity.getLocation().getChunk().getTileEntities();
        int r = plugin.getRewardManager(entity.getWorld()).campRadius;
        r *= r;
        for (BlockState state : tileEntities) {
            if (state instanceof CreatureSpawner && state.getBlock().getLocation().distanceSquared(loc) <= r) {
                return true;
            }
        }
        return false;
    }
}
