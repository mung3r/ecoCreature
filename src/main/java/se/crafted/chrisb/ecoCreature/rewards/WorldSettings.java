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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.commons.CustomType;
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
import se.crafted.chrisb.ecoCreature.rewards.sources.RewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.PVPRewardSource;

public class WorldSettings
{
    private static Random random = new Random();

    private boolean clearOnNoDrops;
    private boolean overrideDrops;
    private boolean noFarm;
    private boolean noFarmFire;

    private Map<Material, List<RewardSource>> materialSources;
    private Map<EntityType, List<RewardSource>> entitySources;
    private Map<CustomType, List<RewardSource>> customSources;
    private Set<Gain> gainMultipliers;
    private Set<Party> parties;
    private Set<Rule> huntingRules;
    private SpawnerMobTracking spawnerMobTracking;

    public WorldSettings()
    {
        materialSources = new HashMap<Material, List<RewardSource>>();
        entitySources = new HashMap<EntityType, List<RewardSource>>();
        customSources = new HashMap<CustomType, List<RewardSource>>();

        gainMultipliers = new HashSet<Gain>();
        parties = new HashSet<Party>();
        huntingRules = new HashSet<Rule>();

        for (Rule rule : huntingRules) {
            if (rule instanceof SpawnerMobTracking) {
                spawnerMobTracking = (SpawnerMobTracking) rule;
            }
        }
    }

    public boolean isClearOnNoDrops()
    {
        return clearOnNoDrops;
    }

    public void setClearOnNoDrops(boolean clearOnNoDrops)
    {
        this.clearOnNoDrops = clearOnNoDrops;
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

    public void setMaterialSources(Map<Material, List<RewardSource>> materialSources)
    {
        this.materialSources = materialSources;
    }

    public void setEntitySources(Map<EntityType, List<RewardSource>> entitySources)
    {
        this.entitySources = entitySources;
    }

    public void setCustomSources(Map<CustomType, List<RewardSource>> customSources)
    {
        this.customSources = customSources;
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
        else if (event instanceof PlayerKilledEvent) {
            return hasRewardSource((PlayerKilledEvent) event);
        }
        else if (event instanceof PlayerDeathEvent) {
            return hasRewardSource((PlayerDeathEvent) event);
        }
        else if (DependencyUtils.hasHeroes() && event instanceof HeroChangeLevelEvent) {
            return hasRewardSource((HeroChangeLevelEvent) event);
        }
        else if (DependencyUtils.hasDeathTpPlus() && event instanceof DeathStreakEvent) {
            return hasRewardSource((DeathStreakEvent) event);
        }
        else if (DependencyUtils.hasDeathTpPlus() && event instanceof KillStreakEvent) {
            return hasRewardSource((KillStreakEvent) event);
        }

        return false;
    }

    private boolean hasRewardSource(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (DependencyUtils.hasPermission(player, "reward." + block.getType().name())) {
            return hasRewardSource(block.getType());
        }
        else {
            ECLogger.getInstance().debug("No reward for " + player.getName() + " due to lack of permission for " + block.getType().name());
        }

        return false;
    }

    private boolean hasRewardSource(EntityKilledEvent event)
    {
        Player killer = event.getKiller();
        LivingEntity entity = event.getEntity();
        CustomType customType = CustomType.fromEntity(entity);

        if (hasRewardSource(customType)) {
            if (DependencyUtils.hasPermission(killer, "reward." + customType.getName())) {
                if (!isRuleBroken(event)) {
                    return hasRewardSource(customType);
                }
            }
            else {
                ECLogger.getInstance().debug("No reward for " + killer.getName() + " due to lack of permission for " + customType.getName());
            }
        }
        else {
            if (DependencyUtils.hasPermission(killer, "reward." + entity.getType().getName())) {
                if (!isRuleBroken(event) && hasRewardSource(entity.getType())) {
                    return true;
                }
            }
            else {
                ECLogger.getInstance().debug("No reward for " + killer.getName() + " due to lack of permission for " + entity.getType().getName());
            }
        }

        return false;
    }

    private boolean hasRewardSource(HeroChangeLevelEvent event)
    {
        Player player = event.getHero().getPlayer();

        if (DependencyUtils.hasPermission(player, "reward.hero_mastered")) {
            if (event.getHero().getLevel() == event.getHeroClass().getMaxLevel()) {
                return hasRewardSource(CustomType.HERO_MASTERED);
            }
        }
        else {
            ECLogger.getInstance().debug("No reward for " + player.getName() + " due to lack of permission for " + CustomType.HERO_MASTERED.getName());
        }

        return false;
    }

    private boolean hasRewardSource(PlayerKilledEvent event)
    {
        if (DependencyUtils.hasPermission(event.getKiller(), "reward.player")) {
            return hasRewardSource(CustomType.PLAYER) || (DependencyUtils.hasEconomy() && getRewardSource(CustomType.LEGACY_PVP) instanceof PVPRewardSource);
        }
        else {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " due to lack of permission for " + CustomType.PLAYER.getName());
        }

        return false;
    }

    private boolean hasRewardSource(PlayerDeathEvent event)
    {
        if (DependencyUtils.hasPermission(event.getEntity(), "reward.deathpenalty")) {
            return getRewardSource(CustomType.DEATH_PENALTY) instanceof DeathPenaltySource;
        }
        else {
            ECLogger.getInstance().debug("No reward for " + event.getEntity().getName() + " due to lack of permission for " + CustomType.DEATH_PENALTY.getName());
        }

        return false;
    }

    private boolean hasRewardSource(DeathStreakEvent event)
    {
        if (DependencyUtils.hasPermission(event.getPlayer(), "reward.deathstreak")) {
            return hasRewardSource(CustomType.DEATH_STREAK);
        }
        else {
            ECLogger.getInstance().debug("No reward for " + event.getPlayer().getName() + " due to lack of permission for " + CustomType.DEATH_STREAK.getName());
        }

        return false;
    }

    private boolean hasRewardSource(KillStreakEvent event)
    {
        if (DependencyUtils.hasPermission(event.getPlayer(), "reward.killstreak")) {
            return hasRewardSource(CustomType.KILL_STREAK);
        }
        else {
            ECLogger.getInstance().debug("No reward for " + event.getPlayer().getName() + " due to lack of permission for " + CustomType.KILL_STREAK.getName());
        }

        return false;
    }

    private boolean hasRewardSource(Material material)
    {
        return material != null && materialSources.containsKey(material) && !materialSources.get(material).isEmpty();
    }

    private boolean hasRewardSource(EntityType type)
    {
        return type != null && entitySources.containsKey(type) && !entitySources.get(type).isEmpty();
    }

    public boolean hasRewardSource(CustomType type)
    {
        return type != null && customSources.containsKey(type) && !customSources.get(type).isEmpty();
    }

    public RewardSource getRewardSource(Event event)
    {
        if (event instanceof BlockBreakEvent) {
            return getRewardSource(((BlockBreakEvent) event).getBlock());
        }
        else if (event instanceof EntityKilledEvent) {
            return getRewardSource(((EntityKilledEvent) event).getEntity());
        }
        else if (event instanceof PlayerKilledEvent) {
            return getRewardSource(CustomType.PLAYER);
        }
        else if (event instanceof PlayerDeathEvent) {
            return getRewardSource(CustomType.DEATH_PENALTY);
        }
        else if (DependencyUtils.hasHeroes() && event instanceof HeroChangeLevelEvent) {
            return getRewardSource(CustomType.HERO_MASTERED);
        }
        else if (DependencyUtils.hasDeathTpPlus() && event instanceof DeathStreakEvent) {
            return getRewardSource(CustomType.DEATH_STREAK);
        }
        else if (DependencyUtils.hasDeathTpPlus() && event instanceof KillStreakEvent) {
            return getRewardSource(CustomType.KILL_STREAK);
        }

        return null;
    }

    private RewardSource getRewardSource(Block block)
    {
        RewardSource source = null;

        if (hasRewardSource(block.getType())) {
            source = getRewardSource(block.getType());
        }
        else {
            ECLogger.getInstance().warning("No reward found for entity " + block.getType().name());
        }

        return source;
    }

    private RewardSource getRewardSource(Entity entity)
    {
        RewardSource source = null;

        if (hasRewardSource(CustomType.fromEntity(entity))) {
            source = getRewardSource(CustomType.fromEntity(entity));
        }
        else if (hasRewardSource(entity.getType())) {
            source = getRewardSource(entity.getType());
        }
        else {
            ECLogger.getInstance().warning("No reward found for entity " + entity.getType().getName());
        }

        return source;
    }

    private RewardSource getRewardSource(Material material)
    {
        RewardSource source = null;

        if (hasRewardSource(material)) {
            source = materialSources.get(material).get(random.nextInt(materialSources.get(material).size()));
        }
        else {
            ECLogger.getInstance().warning("No reward defined for " + material);
        }

        return source;
    }

    private RewardSource getRewardSource(EntityType entityType)
    {
        RewardSource source = null;

        if (hasRewardSource(entityType)) {
            source = entitySources.get(entityType).get(random.nextInt(entitySources.get(entityType).size()));
        }
        else {
            ECLogger.getInstance().warning("No reward defined for " + entityType.getName());
        }

        return source;
    }

    public RewardSource getRewardSource(CustomType type)
    {
        RewardSource source = null;

        if (hasRewardSource(type)) {
            source = customSources.get(type).get(random.nextInt(customSources.get(type).size()));

            if (source == null) {
                source = (PVPRewardSource) getRewardSource(CustomType.LEGACY_PVP);
            }
        }
        else {
            ECLogger.getInstance().warning("No reward defined for " + type.name());
        }

        return source;
    }

    public double getGainMultiplier(Player player)
    {
        double multiplier = 1.0;

        for (Gain gain : gainMultipliers) {
            multiplier *= gain.getMultiplier(player);
        }

        return multiplier;
    }

    public Set<String> getParty(Player player)
    {
        Set<String> players = new HashSet<String>();

        for (Party party : parties) {
            if (party.isShared()) {
                players.addAll(party.getPlayers(player));
            }
        }

        return players;
    }

    private boolean isRuleBroken(EntityKilledEvent event)
    {
        for (Rule rule : huntingRules) {
            if (rule.isBroken(event)) {
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
