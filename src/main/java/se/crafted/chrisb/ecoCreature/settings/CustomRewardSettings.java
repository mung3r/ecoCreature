package se.crafted.chrisb.ecoCreature.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.rewards.sources.DeathPenaltySource;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.PVPRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.CustomRewardType;

public class CustomRewardSettings extends AbstractRewardSettings
{
    private Map<CustomRewardType, List<AbstractRewardSource>> sources;

    public CustomRewardSettings(Map<CustomRewardType, List<AbstractRewardSource>> sources)
    {
        this.sources = sources;
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            return hasRewardSource((PlayerKilledEvent) event);
        }
        else if (event instanceof PlayerDeathEvent) {
            return hasRewardSource((PlayerDeathEvent) event);
        }

        return false;
    }

    private boolean hasRewardSource(PlayerKilledEvent event)
    {
        if (DependencyUtils.hasPermission(event.getKiller(), "reward.player")) {
            return DependencyUtils.hasEconomy() && getRewardSource(CustomRewardType.LEGACY_PVP) instanceof PVPRewardSource;
        }
        else {
            ECLogger.getInstance().debug(this.getClass(), "No reward for " + event.getKiller().getName() + " due to lack of permission for " + CustomRewardType.LEGACY_PVP);
        }

        return false;
    }

    private boolean hasRewardSource(PlayerDeathEvent event)
    {
        if (DependencyUtils.hasPermission(event.getEntity(), "reward.deathpenalty")) {
            return getRewardSource(CustomRewardType.DEATH_PENALTY) instanceof DeathPenaltySource;
        }
        else {
            ECLogger.getInstance().debug(this.getClass(), "No reward for " + event.getEntity().getName() + " due to lack of permission for " + CustomRewardType.DEATH_PENALTY.getName());
        }

        return false;
    }

    private boolean hasRewardSource(CustomRewardType type)
    {
        return type != null && sources.containsKey(type) && !sources.get(type).isEmpty();
    }

    @Override
    public AbstractRewardSource getRewardSource(Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            return getRewardSource(CustomRewardType.LEGACY_PVP);
        }
        else if (event instanceof PlayerDeathEvent) {
            return getRewardSource(CustomRewardType.DEATH_PENALTY);
        }

        return null;
    }

    private AbstractRewardSource getRewardSource(CustomRewardType type)
    {
        AbstractRewardSource source = null;

        if (hasRewardSource(type)) {
            source = sources.get(type).get(nextInt(sources.get(type).size()));
        }
        else {
            ECLogger.getInstance().warning("No reward defined for custom type: " + type.name());
        }

        return source;
    }

    public static AbstractRewardSettings parseConfig(ConfigurationSection config)
    {
        Map<CustomRewardType, List<AbstractRewardSource>> sources = new HashMap<CustomRewardType, List<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                CustomRewardType type = CustomRewardType.fromName(typeName);

                if (type != CustomRewardType.INVALID) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(typeName, rewardTable.getConfigurationSection(typeName)), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(mergeSets(source, rewardTable, config.getConfigurationSection("RewardSets")));
                }
            }

            if (config.getBoolean("System.Hunting.PenalizeDeath", false)) {
                AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(CustomRewardType.DEATH_PENALTY.getName(), config), config);
                if (!sources.containsKey(CustomRewardType.DEATH_PENALTY)) {
                    sources.put(CustomRewardType.DEATH_PENALTY, new ArrayList<AbstractRewardSource>());
                }

                sources.get(CustomRewardType.DEATH_PENALTY).add(source);
            }

            if (config.getBoolean("System.Hunting.PVPReward", false)) {
                AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(CustomRewardType.LEGACY_PVP.getName(), config), config);
                if (!sources.containsKey(CustomRewardType.LEGACY_PVP)) {
                    sources.put(CustomRewardType.LEGACY_PVP, new ArrayList<AbstractRewardSource>());
                }

                sources.get(CustomRewardType.LEGACY_PVP).add(source);
            }
        }

        return new CustomRewardSettings(sources);
    }
}
