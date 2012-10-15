package se.crafted.chrisb.ecoCreature.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.rewards.rules.Rule;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.CustomEntityRewardType;

public class CustomEntityRewardSettings extends AbstractRewardSettings
{
    private Map<CustomEntityRewardType, List<AbstractRewardSource>> sources;
    private Set<Rule> huntingRules;

    public CustomEntityRewardSettings(Map<CustomEntityRewardType, List<AbstractRewardSource>> sources)
    {
        huntingRules = Collections.emptySet();
        this.sources = sources;
    }

    public void setHuntingRules(Set<Rule> huntingRules)
    {
        this.huntingRules = huntingRules;
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
    	return event instanceof EntityKilledEvent && hasRewardSource((EntityKilledEvent) event);
    }

    private boolean hasRewardSource(EntityKilledEvent event)
    {
        Player killer = event.getKiller();
        LivingEntity entity = event.getEntity();

        if (DependencyUtils.hasPermission(killer, "reward." + entity.getType().getName())) {
            if (hasRewardSource(CustomEntityRewardType.fromEntity(entity)) && !isRuleBroken(event)) {
                return true;
            }
        }
        else {
            ECLogger.getInstance().debug(this.getClass(), "No reward for " + killer.getName() + " due to lack of permission for " + entity.getType().getName());
        }

        return false;
    }

    private boolean hasRewardSource(CustomEntityRewardType type)
    {
        return type != null && sources.containsKey(type) && !sources.get(type).isEmpty();
    }

    @Override
    public AbstractRewardSource getRewardSource(Event event)
    {
        if (event instanceof EntityKilledEvent) {
            return getRewardSource(((EntityKilledEvent) event).getEntity());
        }

        return null;
    }

    private AbstractRewardSource getRewardSource(Entity entity)
    {
        AbstractRewardSource source = null;

        if (hasRewardSource(CustomEntityRewardType.fromEntity(entity))) {
            source = getRewardSource(CustomEntityRewardType.fromEntity(entity));
        }
        else {
            ECLogger.getInstance().warning("No reward found for entity: " + entity.getType().getName());
        }

        return source;
    }

    private AbstractRewardSource getRewardSource(CustomEntityRewardType entityType)
    {
        AbstractRewardSource source = null;

        if (hasRewardSource(entityType)) {
            source = sources.get(entityType).get(nextInt(sources.get(entityType).size()));
        }
        else {
            ECLogger.getInstance().warning("No reward defined for entity type: " + entityType.getName());
        }

        return source;
    }

    protected boolean isRuleBroken(EntityKilledEvent event)
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

    public static AbstractRewardSettings parseConfig(ConfigurationSection config)
    {
        Map<CustomEntityRewardType, List<AbstractRewardSource>> sources = new HashMap<CustomEntityRewardType, List<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                CustomEntityRewardType type = CustomEntityRewardType.fromName(typeName);

                if (type != CustomEntityRewardType.INVALID) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(typeName, rewardTable.getConfigurationSection(typeName)), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(mergeSets(source, rewardTable, config.getConfigurationSection("RewardSets")));
                }
            }
        }

        CustomEntityRewardSettings settings = new CustomEntityRewardSettings(sources);
        settings.setHuntingRules(loadHuntingRules(config));
        return settings;
    }
}
