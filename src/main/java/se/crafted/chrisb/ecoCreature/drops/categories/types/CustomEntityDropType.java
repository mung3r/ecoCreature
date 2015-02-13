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
package se.crafted.chrisb.ecoCreature.drops.categories.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

public enum CustomEntityDropType
{
    ANGRY_WOLF("AngryWolf", true),
    KILLER_RABBIT("KillerRabbit", true),
    PLAYER("Player", true),
    POWERED_CREEPER("PoweredCreeper", true),
    WITHER_SKELETON("WitherSkeleton", true),
    ZOMBIE_VILLAGER("ZombieVillager", true),
    ZOMBIE_BABY("ZombieBaby", true),
    INVALID("__Invalid__", false);

    private static final Map<String, CustomEntityDropType> NAME_MAP = new HashMap<>();

    static {
        for (CustomEntityDropType type : EnumSet.allOf(CustomEntityDropType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private final String name;
    private final boolean valid;

    CustomEntityDropType(String name, boolean valid)
    {
        this.name = name.toLowerCase();
        this.valid = valid;
    }

    public boolean isValid()
    {
        return valid;
    }

    public static CustomEntityDropType fromName(String name)
    {
        CustomEntityDropType dropType = INVALID;
        if (name != null && NAME_MAP.containsKey(name.toLowerCase())) {
            dropType = NAME_MAP.get(name.toLowerCase());
        }
        return dropType;
    }

    public static CustomEntityDropType fromEntity(Entity entity)
    {
        CustomEntityDropType entityType = INVALID;

        if (entity instanceof Creeper && ((Creeper) entity).isPowered()) {
            entityType = CustomEntityDropType.POWERED_CREEPER;
        }
        else if (entity instanceof Rabbit && Type.THE_KILLER_BUNNY.equals(((Rabbit) entity).getRabbitType())) {
            entityType = CustomEntityDropType.KILLER_RABBIT;
        }
        else if (entity instanceof Player) {
            entityType = CustomEntityDropType.PLAYER;
        }
        else if (entity instanceof Wolf && ((Wolf) entity).isAngry()) {
            entityType = CustomEntityDropType.ANGRY_WOLF;
        }
        else if (entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
            entityType = CustomEntityDropType.WITHER_SKELETON;
        }
        else if (entity instanceof Zombie && ((Zombie) entity).isVillager()) {
            entityType = CustomEntityDropType.ZOMBIE_VILLAGER;
        }
        else if (entity instanceof Zombie && ((Zombie) entity).isBaby()) {
            entityType = CustomEntityDropType.ZOMBIE_BABY;
        }
        else if (entity instanceof LivingEntity) {
            entityType = CustomEntityDropType.fromName(entity.getType().getName());
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
