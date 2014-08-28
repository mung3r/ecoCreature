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
package se.crafted.chrisb.ecoCreature.drops;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomDropType;
import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomEntityDropType;
import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomMaterialDropType;
import se.crafted.chrisb.ecoCreature.drops.categories.types.HeroesDropType;
import se.crafted.chrisb.ecoCreature.drops.categories.types.McMMODropType;
import se.crafted.chrisb.ecoCreature.drops.categories.types.StreakDropType;
import se.crafted.chrisb.ecoCreature.drops.sources.AbstractDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.DeathPenaltyDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.EntityDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.HeroesDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.MaterialDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.McMMODropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.PVPDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.SetDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.StreakDropSource;

import java.util.ArrayList;
import java.util.Collection;

public final class DropSourceFactory
{
    private DropSourceFactory()
    {
    }

    public static Collection<AbstractDropSource> createSetSources(String section, ConfigurationSection config)
    {
        Collection<AbstractDropSource> sources = new ArrayList<AbstractDropSource>();
        sources.add(new SetDropSource(section, config));
        return sources;
    }

    public static Collection<AbstractDropSource> createSources(String section, ConfigurationSection config)
    {
        Collection<AbstractDropSource> sources = new ArrayList<AbstractDropSource>();
        String name = parseTypeName(section);
        
        if (CustomMaterialDropType.fromName(name).isValid()) {
            sources.addAll(createCustomMaterialSources(section, config));
        }
        else if (Material.matchMaterial(name) != null) {
            sources.add(new MaterialDropSource(section, config));
        }
        else if (CustomEntityDropType.fromName(name).isValid()) {
            sources.addAll(createCustomEntitySources(section, config));
        }
        else if (EntityType.fromName(name) != null) {
            sources.add(new EntityDropSource(section, config));
        }
        else if (CustomDropType.fromName(name).isValid()) {
            sources.addAll(createCustomSources(section, config));
        }
        else if (StreakDropType.fromName(name).isValid()) {
            sources.add(StreakDropSource.createDropSource(section, config));
        }
        else if (HeroesDropType.fromName(name).isValid()) {
            sources.add(HeroesDropSource.createDropSource(section, config));
        }
        else if (McMMODropType.fromName(name).isValid()) {
            sources.add(McMMODropSource.createDropSource(section, config));
        }

        if (sources.size() == 1) {
            LoggerUtil.getInstance().debug(name + " mapped to " + sources.iterator().next().getClass().getSimpleName());
        }
        return sources;
    }

    private static Collection<AbstractDropSource> createCustomMaterialSources(String section, ConfigurationSection config)
    {
        Collection<AbstractDropSource> sources = new ArrayList<AbstractDropSource>();

        switch (CustomMaterialDropType.fromName(parseTypeName(section))) {
            case LEGACY_SPAWNER:
                sources.add(new MaterialDropSource(section, config));
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + section);
        }
        return sources;
    }

    private static Collection<AbstractDropSource> createCustomEntitySources(String section, ConfigurationSection config)
    {
        Collection<AbstractDropSource> sources = new ArrayList<AbstractDropSource>();

        switch (CustomEntityDropType.fromName(parseTypeName(section))) {
            case ANGRY_WOLF:
            case PLAYER:
            case POWERED_CREEPER:
            case WITHER_SKELETON:
            case ZOMBIE_VILLAGER:
                sources.add(new EntityDropSource(section, config));
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + section);
        }
        return sources;
    }

    private static Collection<AbstractDropSource> createCustomSources(String section, ConfigurationSection config)
    {
        Collection<AbstractDropSource> sources = new ArrayList<AbstractDropSource>();

        switch (CustomDropType.fromName(parseTypeName(section))) {
            case DEATH_PENALTY:
                sources.add(new DeathPenaltyDropSource(config.getConfigurationSection(section)));
                break;
            case LEGACY_PVP:
                sources.add(new PVPDropSource(config.getConfigurationSection(section)));
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + section);
        }
        return sources;
    }

    public static String parseTypeName(String section)
    {
        String name = null;

        if (section != null && section.lastIndexOf(".") > -1) {
            name = section.substring(section.lastIndexOf(".") + 1);
        }
        return name;
    }
}
