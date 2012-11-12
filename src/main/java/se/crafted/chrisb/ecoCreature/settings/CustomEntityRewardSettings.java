/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
 * ecoCreature is licensed under the GNU Lesser General Public License.
 *
 * ecoCreature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ecoCreature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
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
        if (event instanceof PlayerKilledEvent) {
            return hasRewardSource((PlayerKilledEvent) event);
        }
        else if (event instanceof EntityKilledEvent) {
            return hasRewardSource((EntityKilledEvent) event);
        }

        return false;
    }

    private boolean hasRewardSource(PlayerKilledEvent event)
    {
        if (DependencyUtils.hasPermission(event.getKiller(), "reward.player") && hasRewardSource(CustomEntityRewardType.PLAYER)) {
            return true;
        }
        else {
            LoggerUtil.getInstance().debug(this.getClass(), "No reward for " + event.getKiller().getName() + " due to lack of permission for " + CustomEntityRewardType.PLAYER);
        }

        return false;
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
            LoggerUtil.getInstance().debug(this.getClass(), "No reward for " + killer.getName() + " due to lack of permission for " + entity.getType().getName());
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
        if (event instanceof PlayerKilledEvent) {
            return getRewardSource(((PlayerKilledEvent) event).getEntity());
        }
        else if (event instanceof EntityKilledEvent) {
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
            LoggerUtil.getInstance().warning("No reward found for entity: " + entity.getType().getName());
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
            LoggerUtil.getInstance().warning("No reward defined for entity type: " + entityType.getName());
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

                Map<MessageToken, String> parameters = Collections.emptyMap();
                MessageHandler message = new MessageHandler(rule.getMessage(), parameters);
                message.send(event.getKiller());

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
