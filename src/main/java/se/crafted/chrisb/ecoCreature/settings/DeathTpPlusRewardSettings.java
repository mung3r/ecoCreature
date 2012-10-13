package se.crafted.chrisb.ecoCreature.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.DeathTpPlusRewardType;

public class DeathTpPlusRewardSettings extends AbstractRewardSettings
{
    private Map<DeathTpPlusRewardType, List<AbstractRewardSource>> sources;

    public DeathTpPlusRewardSettings(Map<DeathTpPlusRewardType, List<AbstractRewardSource>> sources)
    {
        this.sources = sources;
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
        if (DependencyUtils.hasDeathTpPlus() && event instanceof DeathStreakEvent) {
            return hasRewardSource((DeathStreakEvent) event);
        }
        else if (DependencyUtils.hasDeathTpPlus() && event instanceof KillStreakEvent) {
            return hasRewardSource((KillStreakEvent) event);
        }

        return false;
    }

    private boolean hasRewardSource(DeathStreakEvent event)
    {
        if (DependencyUtils.hasPermission(event.getPlayer(), "reward.deathstreak")) {
            return hasRewardSource(DeathTpPlusRewardType.DEATH_STREAK);
        }
        else {
            ECLogger.getInstance().debug(this.getClass(), "No reward for " + event.getPlayer().getName() + " due to lack of permission for " + DeathTpPlusRewardType.DEATH_STREAK.getName());
        }

        return false;
    }

    private boolean hasRewardSource(KillStreakEvent event)
    {
        if (DependencyUtils.hasPermission(event.getPlayer(), "reward.killstreak")) {
            return hasRewardSource(DeathTpPlusRewardType.KILL_STREAK);
        }
        else {
            ECLogger.getInstance().debug(this.getClass(), "No reward for " + event.getPlayer().getName() + " due to lack of permission for " + DeathTpPlusRewardType.KILL_STREAK.getName());
        }

        return false;
    }

    private boolean hasRewardSource(DeathTpPlusRewardType type)
    {
        return type != null && sources.containsKey(type) && !sources.get(type).isEmpty();
    }

    @Override
    public AbstractRewardSource getRewardSource(Event event)
    {
        if (DependencyUtils.hasDeathTpPlus() && event instanceof DeathStreakEvent) {
            return getRewardSource(DeathTpPlusRewardType.DEATH_STREAK);
        }
        else if (DependencyUtils.hasDeathTpPlus() && event instanceof KillStreakEvent) {
            return getRewardSource(DeathTpPlusRewardType.KILL_STREAK);
        }

        return null;
    }

    private AbstractRewardSource getRewardSource(DeathTpPlusRewardType type)
    {
        AbstractRewardSource source = null;

        if (hasRewardSource(type)) {
            source = sources.get(type).get(random.nextInt(sources.get(type).size()));
        }
        else {
            ECLogger.getInstance().warning("No reward defined for custom type: " + type.name());
        }

        return source;
    }

    public static AbstractRewardSettings parseConfig(ConfigurationSection config)
    {
        Map<DeathTpPlusRewardType, List<AbstractRewardSource>> sources = new HashMap<DeathTpPlusRewardType, List<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                DeathTpPlusRewardType type = DeathTpPlusRewardType.fromName(typeName);

                if (type != DeathTpPlusRewardType.INVALID) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(typeName, rewardTable.getConfigurationSection(typeName)), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(mergeSets(source, rewardTable, config.getConfigurationSection("RewardSets")));
                }
            }
        }

        return new DeathTpPlusRewardSettings(sources);
    }
}
