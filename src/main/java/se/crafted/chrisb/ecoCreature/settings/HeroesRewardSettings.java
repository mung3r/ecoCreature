package se.crafted.chrisb.ecoCreature.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.HeroesRewardType;

public class HeroesRewardSettings extends AbstractRewardSettings
{
    private Map<HeroesRewardType, List<AbstractRewardSource>> sources;

    public HeroesRewardSettings(Map<HeroesRewardType, List<AbstractRewardSource>> sources)
    {
        this.sources = sources;
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
        if (DependencyUtils.hasHeroes() && event instanceof HeroChangeLevelEvent) {
            return hasRewardSource((HeroChangeLevelEvent) event);
        }

        return false;
    }

    private boolean hasRewardSource(HeroChangeLevelEvent event)
    {
        Player player = event.getHero().getPlayer();

        if (DependencyUtils.hasPermission(player, "reward.heromastered")) {
            if (event.getHero().getLevel() == event.getHeroClass().getMaxLevel()) {
                return hasRewardSource(HeroesRewardType.HERO_MASTERED);
            }
            else if (DependencyUtils.hasPermission(player, "reward.heroleveled")) {
                return hasRewardSource(HeroesRewardType.HERO_LEVELED);
            }
        }
        else if (DependencyUtils.hasPermission(player, "reward.heroleveled")) {
            return hasRewardSource(HeroesRewardType.HERO_LEVELED);
        }
        else {
            ECLogger.getInstance().debug(this.getClass(), "No reward for " + player.getName() + " due to lack of permission for " + HeroesRewardType.HERO_MASTERED.getName());
        }

        return false;
    }

    private boolean hasRewardSource(HeroesRewardType type)
    {
        return type != null && sources.containsKey(type) && !sources.get(type).isEmpty();
    }

    @Override
    public AbstractRewardSource getRewardSource(Event event)
    {
        if (DependencyUtils.hasHeroes() && event instanceof HeroChangeLevelEvent) {
            return getRewardSource(HeroesRewardType.HERO_MASTERED);
        }

        return null;
    }

    private AbstractRewardSource getRewardSource(HeroesRewardType type)
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
        Map<HeroesRewardType, List<AbstractRewardSource>> sources = new HashMap<HeroesRewardType, List<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                HeroesRewardType type = HeroesRewardType.fromName(typeName);

                if (type != HeroesRewardType.INVALID) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(typeName, rewardTable.getConfigurationSection(typeName)), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(mergeSets(source, rewardTable, config.getConfigurationSection("RewardSets")));
                }
            }
        }

        return new HeroesRewardSettings(sources);
    }
}
