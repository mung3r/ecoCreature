package se.crafted.chrisb.ecoCreature.rewards;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.rewards.gain.Gain;
import se.crafted.chrisb.ecoCreature.rewards.parties.Party;
import se.crafted.chrisb.ecoCreature.rewards.rules.Rule;
import se.crafted.chrisb.ecoCreature.rewards.rules.SpawnerMobTracking;
import se.crafted.chrisb.ecoCreature.rewards.sources.DeathPenaltySource;
import se.crafted.chrisb.ecoCreature.rewards.sources.PVPRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.RewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.RewardSourceType;

public class RewardSettings
{
    private static Random random = new Random();

    private boolean clearDefaultDrops;
    private boolean overrideDrops;
    private boolean noFarm;
    private boolean noFarmFire;

    private Map<RewardSourceType, List<RewardSource>> sources;
    private Set<Gain> gainMultipliers;
    private Set<Party> parties;
    private Set<Rule> huntingRules;
    private SpawnerMobTracking spawnerMobTracking;

    public RewardSettings()
    {
        sources = new HashMap<RewardSourceType, List<RewardSource>>();
        gainMultipliers = new HashSet<Gain>();
        parties = new HashSet<Party>();
        huntingRules = new HashSet<Rule>();

        for (Rule rule : huntingRules) {
            if (rule instanceof SpawnerMobTracking) {
                spawnerMobTracking = (SpawnerMobTracking) rule;
            }
        }
    }

    public boolean isClearDefaultDrops()
    {
        return clearDefaultDrops;
    }

    public void setClearDefaultDrops(boolean clearDefaultDrops)
    {
        this.clearDefaultDrops = clearDefaultDrops;
    }

    public boolean isOverrideDrops()
    {
        return overrideDrops;
    }

    public void setOverrideDrops(boolean overrideDrops)
    {
        this.overrideDrops = overrideDrops;
    }

    public boolean isNoFarm()
    {
        return noFarm;
    }

    public void setNoFarm(boolean noFarm)
    {
        this.noFarm = noFarm;
    }

    public boolean isNoFarmFire()
    {
        return noFarmFire;
    }

    public void setNoFarmFire(boolean noFarmFire)
    {
        this.noFarmFire = noFarmFire;
    }

    public void setRewardSources(Map<RewardSourceType, List<RewardSource>> rewardSources)
    {
        this.sources = rewardSources;
    }

    public void setGainMultipliers(Set<Gain> gainMultipliers)
    {
        this.gainMultipliers = gainMultipliers;
    }

    public void setParties(Set<Party> parties)
    {
        this.parties = parties;
    }

    public void setHuntingRules(Set<Rule> huntingRules)
    {
        this.huntingRules = huntingRules;
    }

    public void addSpawnerMob(LivingEntity entity)
    {
        if (spawnerMobTracking != null) {
            spawnerMobTracking.addSpawnerMob(entity);
        }
    }

    public boolean hasRewardSource(Event event)
    {
        if (event instanceof BlockBreakEvent) {
            return hasRewardSource((BlockBreakEvent) event);
        }
        else if (event instanceof EntityKilledEvent) {
            return hasRewardSource((EntityKilledEvent) event);
        }
        else if (event instanceof HeroChangeLevelEvent) {
            return hasRewardSource((HeroChangeLevelEvent) event);
        }
        else if (event instanceof PlayerKilledEvent) {
            return hasRewardSource((PlayerKilledEvent) event);
        }
        else if (event instanceof PlayerDeathEvent) {
            return hasRewardSource((PlayerDeathEvent) event);
        }
        else if (event instanceof DeathStreakEvent) {
            return hasRewardSource((DeathStreakEvent) event);
        }
        else if (event instanceof KillStreakEvent) {
            return hasRewardSource((KillStreakEvent) event);
        }

        return false;
    }

    private boolean hasRewardSource(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (DependencyUtils.hasPermission(player, "reward.spawner")) {
            if (block.getType() == Material.MOB_SPAWNER) {
                return hasRewardSource(RewardSourceType.SPAWNER);
            }
        }
        else {
            ECLogger.getInstance().debug("No reward for " + block.getType().name() + " due to lack of permission for " + RewardSourceType.SPAWNER.getName());
        }

        return false;
    }

    private boolean hasRewardSource(EntityKilledEvent event)
    {
        if (DependencyUtils.hasPermission(event.getKiller(), "reward." + RewardSourceType.fromEntity(event.getEntity()).getName())) {
            if (!isRuleBroken(event)) {
                return hasRewardSource(event.getEntity());
            }
        }
        else {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " due to lack of permission for " + RewardSourceType.fromEntity(event.getEntity()).getName());
        }

        return false;
    }

    private boolean hasRewardSource(HeroChangeLevelEvent event)
    {
        Player player = event.getHero().getPlayer();

        if (DependencyUtils.hasPermission(player, "reward.hero_mastered")) {
            if (event.getHero().getLevel() == event.getHeroClass().getMaxLevel()) {
                return hasRewardSource(RewardSourceType.HERO_MASTERED);
            }
        }
        else {
            ECLogger.getInstance().debug("No reward for " + player.getName() + " due to lack of permission for " + RewardSourceType.HERO_MASTERED.getName());
        }

        return false;
    }

    private boolean hasRewardSource(PlayerKilledEvent event)
    {
        if (DependencyUtils.hasPermission(event.getKiller(), "reward.player")) {
            return hasRewardSource(RewardSourceType.PLAYER) || (DependencyUtils.hasEconomy() && getRewardSource(RewardSourceType.LEGACY_PVP) instanceof PVPRewardSource);
        }
        else {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " due to lack of permission for " + RewardSourceType.PLAYER.getName());
        }

        return false;
    }

    private boolean hasRewardSource(PlayerDeathEvent event)
    {
        if (DependencyUtils.hasPermission(event.getEntity(), "reward.deathpenalty")) {
            return getRewardSource(RewardSourceType.DEATH_PENALTY) instanceof DeathPenaltySource;
        }
        else {
            ECLogger.getInstance().debug("No reward for " + event.getEntity().getName() + " due to lack of permission for " + RewardSourceType.DEATH_PENALTY.getName());
        }

        return false;
    }

    private boolean hasRewardSource(DeathStreakEvent event)
    {
        if (DependencyUtils.hasPermission(event.getPlayer(), "reward.deathstreak")) {
            return hasRewardSource(RewardSourceType.DEATH_STREAK);
        }
        else {
            ECLogger.getInstance().debug("No reward for " + event.getPlayer().getName() + " due to lack of permission for " + RewardSourceType.DEATH_STREAK.getName());
        }

        return false;
    }

    private boolean hasRewardSource(KillStreakEvent event)
    {
        if (DependencyUtils.hasPermission(event.getPlayer(), "reward.killstreak")) {
            return hasRewardSource(RewardSourceType.KILL_STREAK);
        }
        else {
            ECLogger.getInstance().debug("No reward for " + event.getPlayer().getName() + " due to lack of permission for " + RewardSourceType.KILL_STREAK.getName());
        }

        return false;
    }

    private boolean hasRewardSource(Entity entity)
    {
        return entity != null && hasRewardSource(RewardSourceType.fromEntity(entity));
    }

    public boolean hasRewardSource(RewardSourceType type)
    {
        return sources.containsKey(type) && !sources.get(type).isEmpty();
    }

    public RewardSource getRewardSource(Event event)
    {
        if (event instanceof BlockBreakEvent) {
            return getRewardSource(RewardSourceType.SPAWNER);
        }
        else if (event instanceof EntityKilledEvent) {
            return getRewardSource(((EntityKilledEvent) event).getEntity());
        }
        else if (event instanceof HeroChangeLevelEvent) {
            return getRewardSource(RewardSourceType.HERO_MASTERED);
        }
        else if (event instanceof PlayerKilledEvent) {
            return getRewardSource(RewardSourceType.PLAYER);
        }
        else if (event instanceof PlayerDeathEvent) {
            return getRewardSource(RewardSourceType.DEATH_PENALTY);
        }
        else if (event instanceof DeathStreakEvent) {
            return getRewardSource(RewardSourceType.DEATH_STREAK);
        }
        else if (event instanceof KillStreakEvent) {
            return getRewardSource(RewardSourceType.KILL_STREAK);
        }

        return null;
    }

    private RewardSource getRewardSource(Entity entity)
    {
        RewardSource source = null;

        if (hasRewardSource(entity)) {
            source = getRewardSource(RewardSourceType.fromEntity(entity));
        }
        else {
            ECLogger.getInstance().warning("No reward found for entity " + entity.getClass());
        }

        return source;
    }

    public RewardSource getRewardSource(RewardSourceType type)
    {
        RewardSource source = null;

        if (hasRewardSource(type)) {
            source = sources.get(type).get(random.nextInt(sources.get(type).size()));

            if (source == null) {
                source = (PVPRewardSource) getRewardSource(RewardSourceType.LEGACY_PVP);
            }
        }
        else {
            ECLogger.getInstance().warning("No reward defined for " + type);
        }

        return source;
    }

    public double getGainMultiplier(Player player)
    {
        double multiplier = 1.0;

        for (Gain gain : gainMultipliers) {
            if (gain != null) {
                multiplier *= gain.getMultiplier(player);
            }
        }

        return multiplier;
    }

    public Set<String> getParty(Player player)
    {
        Set<String> players = new HashSet<String>();

        for (Party party : parties) {
            if (party != null && party.isShared()) {
                players.addAll(party.getPlayers(player));
            }
        }

        return players;
    }

    private boolean isRuleBroken(EntityKilledEvent event)
    {
        for (Rule rule : huntingRules) {
            if (rule != null && rule.isBroken(event)) {
                if (rule.isClearDrops()) {
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                }

                MessageHandler message = new MessageHandler(event.getKiller(), rule.getMessage());
                message.send();

                return true;
            }
        }

        return false;
    }
}
