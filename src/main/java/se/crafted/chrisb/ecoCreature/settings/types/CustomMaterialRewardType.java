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
package se.crafted.chrisb.ecoCreature.settings.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public enum CustomMaterialRewardType
{
    LEGACY_SPAWNER("Spawner"),
    INVALID("__Invalid__");

    private static final Map<String, CustomMaterialRewardType> NAME_MAP = new HashMap<String, CustomMaterialRewardType>();

    static {
        for (CustomMaterialRewardType type : EnumSet.allOf(CustomMaterialRewardType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private String name;

    CustomMaterialRewardType(String name)
    {
        if (name != null) {
            this.name = name.toLowerCase();
        }
    }

    public static CustomMaterialRewardType fromName(String name)
    {
        CustomMaterialRewardType material = INVALID;
        if (name != null && NAME_MAP.containsKey(name.toLowerCase())) {
            material = NAME_MAP.get(name.toLowerCase());
        }
        return material;
    }

    public static CustomMaterialRewardType fromMaterial(Material material)
    {
        CustomMaterialRewardType type = INVALID;

        if (material == Material.MOB_SPAWNER) {
            type = LEGACY_SPAWNER;
        }

        return type;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
