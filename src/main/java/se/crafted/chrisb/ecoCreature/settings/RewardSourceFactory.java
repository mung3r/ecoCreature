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

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.DeathPenaltySource;
import se.crafted.chrisb.ecoCreature.rewards.sources.EntityRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.HeroesRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.MaterialRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.McMMORewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.PVPRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.SetRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.StreakRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.CustomEntityRewardType;
import se.crafted.chrisb.ecoCreature.settings.types.CustomMaterialRewardType;
import se.crafted.chrisb.ecoCreature.settings.types.CustomRewardType;
import se.crafted.chrisb.ecoCreature.settings.types.StreakRewardType;
import se.crafted.chrisb.ecoCreature.settings.types.HeroesRewardType;
import se.crafted.chrisb.ecoCreature.settings.types.McMMORewardType;

public final class RewardSourceFactory
{
    private RewardSourceFactory()
    {
    }

    public static AbstractRewardSource createSource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        if (CustomMaterialRewardType.fromName(name) != CustomMaterialRewardType.INVALID) {
            source = createCustomMaterialSource(name, config);
        }
        else if (Material.matchMaterial(name) != null) {
            source = new MaterialRewardSource(config);
        }
        else if (CustomEntityRewardType.fromName(name) != CustomEntityRewardType.INVALID) {
            source = createCustomEntitySource(name, config);
        }
        else if (EntityType.fromName(name) != null) {
            source = new EntityRewardSource(config);
        }
        else if (CustomRewardType.fromName(name) != CustomRewardType.INVALID) {
            source = createCustomSource(name, config);
        }
        else if (StreakRewardType.fromName(name) != StreakRewardType.INVALID) {
            source = StreakRewardSource.createRewardSource(name, config);
        }
        else if (HeroesRewardType.fromName(name) != HeroesRewardType.INVALID) {
            source = HeroesRewardSource.createRewardSource(name, config);
        }
        else if (McMMORewardType.fromName(name) != McMMORewardType.INVALID) {
            source = McMMORewardSource.createRewardSource(name, config);
        }

        if (source != null) {
            LoggerUtil.getInstance().debug(RewardSourceFactory.class, name + " mapped to " + source.getClass().getSimpleName());
        }
        return source;
    }

    private static AbstractRewardSource createCustomMaterialSource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        switch (CustomMaterialRewardType.fromName(name)) {
            case LEGACY_SPAWNER:
                source = new MaterialRewardSource(config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + name);
        }
        return source;
    }

    private static AbstractRewardSource createCustomEntitySource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        switch (CustomEntityRewardType.fromName(name)) {
            case ANGRY_WOLF:
            case PLAYER:
            case POWERED_CREEPER:
            case WITHER_SKELETON:
                source = new EntityRewardSource(config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + name);
        }
        return source;
    }

    private static AbstractRewardSource createCustomSource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        switch (CustomRewardType.fromName(name)) {
            case DEATH_PENALTY:
                source = new DeathPenaltySource(config);
                break;
            case LEGACY_PVP:
                source = new PVPRewardSource(config);
                break;
            case SET:
                source = new SetRewardSource(config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + name);
        }
        return source;
    }
}
