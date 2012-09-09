package se.crafted.chrisb.ecoCreature.rewards;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageSender;
import se.crafted.chrisb.ecoCreature.rewards.gain.Gain;
import se.crafted.chrisb.ecoCreature.rewards.parties.Party;
import se.crafted.chrisb.ecoCreature.rewards.rules.Rule;
import se.crafted.chrisb.ecoCreature.rewards.rules.SpawnerMobTracking;
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

    public boolean hasRewardSource(Entity entity)
    {
        return entity != null && hasRewardSource(RewardSourceType.fromEntity(entity));
    }

    public boolean hasRewardSource(RewardSourceType type)
    {
        return sources.containsKey(type) && !sources.get(type).isEmpty();
    }

    public RewardSource getRewardSource(Entity entity)
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

    public boolean isRuleBroken(EntityKilledEvent event)
    {
        for (Rule rule : huntingRules) {
            if (rule != null && rule.isBroken(event)) {
                if (rule.isClearDrops()) {
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                }

                MessageSender message = new MessageSender(event.getKiller(), rule.getMessage());
                message.send();

                return true;
            }
        }

        return false;
    }
}
