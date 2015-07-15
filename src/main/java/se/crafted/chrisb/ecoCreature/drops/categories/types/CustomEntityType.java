/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature.drops.categories.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

public enum CustomEntityType
{
    ANGRY_WOLF("AngryWolf", EntityType.WOLF),
    KILLER_RABBIT("KillerRabbit", EntityType.RABBIT),
    PLAYER("Player", EntityType.PLAYER),
    POWERED_CREEPER("PoweredCreeper", EntityType.CREEPER),
    WITHER_SKELETON("WitherSkeleton", EntityType.WITHER),
    ZOMBIE_VILLAGER("ZombieVillager", EntityType.ZOMBIE),
    ZOMBIE_BABY("ZombieBaby", EntityType.ZOMBIE),
    INVALID("__Invalid__", null);

    private static final Map<String, CustomEntityType> NAME_MAP = new HashMap<>();

    static {
        for (CustomEntityType type : EnumSet.allOf(CustomEntityType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private final String name;
    private final EntityType type;

    CustomEntityType(String name, EntityType type)
    {
        this.name = name.toLowerCase();
        this.type = type;
    }

    public EntityType getType()
    {
        return type;
    }

    public boolean isValid()
    {
        return type != null;
    }

    public static CustomEntityType fromName(String name)
    {
        CustomEntityType dropType = INVALID;
        if (StringUtils.isNotEmpty(name) && NAME_MAP.containsKey(name.toLowerCase())) {
            dropType = NAME_MAP.get(name.toLowerCase());
        }
        return dropType;
    }

    public static CustomEntityType fromEntity(Entity entity)
    {
        CustomEntityType entityType = INVALID;

        if (entity instanceof Creeper && ((Creeper) entity).isPowered()) {
            entityType = CustomEntityType.POWERED_CREEPER;
        }
        else if (entity instanceof Rabbit && Type.THE_KILLER_BUNNY.equals(((Rabbit) entity).getRabbitType())) {
            entityType = CustomEntityType.KILLER_RABBIT;
        }
        else if (entity instanceof Player) {
            entityType = CustomEntityType.PLAYER;
        }
        else if (entity instanceof Wolf && ((Wolf) entity).isAngry()) {
            entityType = CustomEntityType.ANGRY_WOLF;
        }
        else if (entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
            entityType = CustomEntityType.WITHER_SKELETON;
        }
        else if (entity instanceof Zombie && ((Zombie) entity).isVillager()) {
            entityType = CustomEntityType.ZOMBIE_VILLAGER;
        }
        else if (entity instanceof Zombie && ((Zombie) entity).isBaby()) {
            entityType = CustomEntityType.ZOMBIE_BABY;
        }
        else if (entity instanceof LivingEntity) {
            entityType = CustomEntityType.fromName(entity.getType().getName());
        }

        //LoggerUtil.getInstance().debugTrue("No match for type: " + entity.getType().getName(), entityType.equals(INVALID));

        return entityType;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
