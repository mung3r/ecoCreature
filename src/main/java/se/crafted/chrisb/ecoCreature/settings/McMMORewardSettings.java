package se.crafted.chrisb.ecoCreature.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.McMMORewardType;

public class McMMORewardSettings extends AbstractRewardSettings
{
    private Map<McMMORewardType, List<AbstractRewardSource>> sources;

    public McMMORewardSettings(Map<McMMORewardType, List<AbstractRewardSource>> sources)
    {
        this.sources = sources;
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
        if (DependencyUtils.hasMcMMO() && event instanceof McMMOPlayerLevelUpEvent) {
            return hasRewardSource((McMMOPlayerLevelUpEvent) event);
        }

        return false;
    }

    private boolean hasRewardSource(McMMOPlayerLevelUpEvent event)
    {
        if (DependencyUtils.hasPermission(event.getPlayer(), "reward.mcmmoleveled")) {
            return hasRewardSource(McMMORewardType.MCMMO_LEVELED);
        }
        else {
            ECLogger.getInstance().debug(this.getClass(), "No reward for " + event.getPlayer().getName() + " due to lack of permission for " + McMMORewardType.MCMMO_LEVELED.getName());
        }

        return false;
    }

    private boolean hasRewardSource(McMMORewardType type)
    {
        return type != null && sources.containsKey(type) && !sources.get(type).isEmpty();
    }

    @Override
    public AbstractRewardSource getRewardSource(Event event)
    {
        if (event instanceof McMMOPlayerLevelUpEvent) {
            return getRewardSource(McMMORewardType.MCMMO_LEVELED);
        }

        return null;
    }

    private AbstractRewardSource getRewardSource(McMMORewardType type)
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
        Map<McMMORewardType, List<AbstractRewardSource>> sources = new HashMap<McMMORewardType, List<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                McMMORewardType type = McMMORewardType.fromName(typeName);

                if (type != McMMORewardType.INVALID) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(typeName, rewardTable.getConfigurationSection(typeName)), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(mergeSets(source, rewardTable, config.getConfigurationSection("RewardSets")));
                }
            }
        }

        return new McMMORewardSettings(sources);
    }
}
