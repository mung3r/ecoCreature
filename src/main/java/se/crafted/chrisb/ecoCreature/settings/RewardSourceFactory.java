/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2014, R. Ramos <http://github.com/mung3r/>
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

    public static AbstractRewardSource createSetSource(String section, ConfigurationSection config)
    {
        return new SetRewardSource(section, config);
    }

    public static AbstractRewardSource createSource(String section, ConfigurationSection config)
    {
        AbstractRewardSource source = null;
        String name = parseRewardName(section);
        
        if (CustomMaterialRewardType.fromName(name).isValid()) {
            source = createCustomMaterialSource(section, config);
        }
        else if (Material.matchMaterial(name) != null) {
            source = new MaterialRewardSource(section, config);
        }
        else if (CustomEntityRewardType.fromName(name).isValid()) {
            source = createCustomEntitySource(section, config);
        }
        else if (EntityType.fromName(name) != null) {
            source = new EntityRewardSource(section, config);
        }
        else if (CustomRewardType.fromName(name).isValid()) {
            source = createCustomSource(section, config);
        }
        else if (StreakRewardType.fromName(name).isValid()) {
            source = StreakRewardSource.createRewardSource(section, config);
        }
        else if (HeroesRewardType.fromName(name).isValid()) {
            source = HeroesRewardSource.createRewardSource(section, config);
        }
        else if (McMMORewardType.fromName(name).isValid()) {
            source = McMMORewardSource.createRewardSource(section, config);
        }

        if (source != null) {
            LoggerUtil.getInstance().debug(name + " mapped to " + source.getClass().getSimpleName());
        }
        return source;
    }

    private static AbstractRewardSource createCustomMaterialSource(String section, ConfigurationSection config)
    {
        AbstractRewardSource source;

        switch (CustomMaterialRewardType.fromName(parseRewardName(section))) {
            case LEGACY_SPAWNER:
                source = new MaterialRewardSource(section, config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + section);
        }
        return source;
    }

    private static AbstractRewardSource createCustomEntitySource(String section, ConfigurationSection config)
    {
        AbstractRewardSource source;

        switch (CustomEntityRewardType.fromName(parseRewardName(section))) {
            case ANGRY_WOLF:
            case PLAYER:
            case POWERED_CREEPER:
            case WITHER_SKELETON:
            case ZOMBIE_VILLAGER:
                source = new EntityRewardSource(section, config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + section);
        }
        return source;
    }

    private static AbstractRewardSource createCustomSource(String section, ConfigurationSection config)
    {
        AbstractRewardSource source;

        switch (CustomRewardType.fromName(parseRewardName(section))) {
            case DEATH_PENALTY:
                source = new DeathPenaltySource(config.getConfigurationSection(section));
                break;
            case LEGACY_PVP:
                source = new PVPRewardSource(config.getConfigurationSection(section));
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + section);
        }
        return source;
    }

    public static String parseRewardName(String section)
    {
        String name = null;

        if (section != null && section.lastIndexOf(".") > -1) {
            name = section.substring(section.lastIndexOf(".") + 1);
        }
        return name;
    }
}
