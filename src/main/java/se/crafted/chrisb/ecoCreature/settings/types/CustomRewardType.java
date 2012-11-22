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

public enum CustomRewardType
{
    DEATH_PENALTY("DeathPenalty"),
    LEGACY_PVP("LegacyPVP"),
    SET("Set"),
    INVALID("__Invalid__");

    private static final Map<String, CustomRewardType> NAME_MAP = new HashMap<String, CustomRewardType>();

    static {
        for (CustomRewardType type : EnumSet.allOf(CustomRewardType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private String name;

    CustomRewardType(String name)
    {
        if (name != null) {
            this.name = name.toLowerCase();
        }
    }

    public static CustomRewardType fromName(String name)
    {
        CustomRewardType rewardType = INVALID;
        if (name != null && NAME_MAP.containsKey(name.toLowerCase())) {
            rewardType = NAME_MAP.get(name.toLowerCase());
        }
        return rewardType;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
