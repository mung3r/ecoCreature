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
package se.crafted.chrisb.ecoCreature.settings.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

public enum CustomEntityRewardType
{
    ANGRY_WOLF("AngryWolf", true),
    PLAYER("Player", true),
    POWERED_CREEPER("PoweredCreeper", true),
    WITHER_SKELETON("WitherSkeleton", true),
    ZOMBIE_VILLAGER("ZombieVillager", true),
    INVALID("__Invalid__", false);

    private static final Map<String, CustomEntityRewardType> NAME_MAP = new HashMap<String, CustomEntityRewardType>();

    static {
        for (CustomEntityRewardType type : EnumSet.allOf(CustomEntityRewardType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private final String name;
    private final boolean valid;

    CustomEntityRewardType(String name, boolean valid)
    {
        this.name = name.toLowerCase();
        this.valid = valid;
    }

    public boolean isValid()
    {
        return valid;
    }

    public static CustomEntityRewardType fromName(String name)
    {
        CustomEntityRewardType rewardType = INVALID;
        if (name != null && NAME_MAP.containsKey(name.toLowerCase())) {
            rewardType = NAME_MAP.get(name.toLowerCase());
        }
        return rewardType;
    }

    public static CustomEntityRewardType fromEntity(Entity entity)
    {
        CustomEntityRewardType entityType = INVALID;

        if (entity instanceof Creeper && ((Creeper) entity).isPowered()) {
            entityType = CustomEntityRewardType.POWERED_CREEPER;
        }
        else if (entity instanceof Player) {
            entityType = CustomEntityRewardType.PLAYER;
        }
        else if (entity instanceof Wolf && ((Wolf) entity).isAngry()) {
            entityType = CustomEntityRewardType.ANGRY_WOLF;
        }
        else if (entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
            entityType = CustomEntityRewardType.WITHER_SKELETON;
        }
        else if (entity instanceof Zombie && ((Zombie) entity).isVillager()) {
            entityType = CustomEntityRewardType.ZOMBIE_VILLAGER;
        }
        else if (entity instanceof LivingEntity) {
            entityType = CustomEntityRewardType.fromName(entity.getType().getName());
        }

        LoggerUtil.getInstance().debugTrue("No match for type: " + entity.getType().getName(), entityType.equals(INVALID));

        return entityType;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
