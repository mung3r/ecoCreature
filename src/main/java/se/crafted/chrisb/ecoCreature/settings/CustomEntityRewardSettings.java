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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.CustomEntityRewardType;

public class CustomEntityRewardSettings extends AbstractRewardSettings<CustomEntityRewardType>
{
    public CustomEntityRewardSettings(Map<CustomEntityRewardType, List<AbstractRewardSource>> sources)
    {
        super(sources);
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

        if (DependencyUtils.hasPermission(killer, "reward." + CustomEntityRewardType.fromEntity(entity))) {
            if (hasRewardSource(CustomEntityRewardType.fromEntity(entity)) && !isRuleBroken(event)) {
                return true;
            }
        }
        else {
            LoggerUtil.getInstance().debug(this.getClass(), "No reward for " + killer.getName() + " due to lack of permission for " + CustomEntityRewardType.fromEntity(entity));
        }

        return false;
    }

    private boolean hasRewardSource(CustomEntityRewardType type)
    {
        return type != null && getSources().containsKey(type) && !getSources().get(type).isEmpty();
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
            LoggerUtil.getInstance().warning("No reward found for entity: " + CustomEntityRewardType.fromEntity(entity));
        }

        return source;
    }

    private AbstractRewardSource getRewardSource(CustomEntityRewardType entityType)
    {
        AbstractRewardSource source = null;

        if (hasRewardSource(entityType)) {
            source = getSources().get(entityType).get(nextInt(getSources().get(entityType).size()));
        }
        else {
            LoggerUtil.getInstance().debug(this.getClass(), "No reward defined for entity type: " + entityType);
        }

        return source;
    }

    public static AbstractRewardSettings<CustomEntityRewardType> parseConfig(ConfigurationSection config)
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
