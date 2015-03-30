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

public enum HeroesDropType
{
    HERO_LEVELED("HeroLeveled", true),
    HERO_MASTERED("HeroMastered", true),
    INVALID("__Invalid__", false);

    private static final Map<String, HeroesDropType> NAME_MAP = new HashMap<>();

    static {
        for (HeroesDropType type : EnumSet.allOf(HeroesDropType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private final String name;
    private final boolean valid;

    HeroesDropType(String name, boolean valid)
    {
        this.name = name.toLowerCase();
        this.valid = valid;
    }

    public boolean isValid()
    {
        return valid;
    }

    public static HeroesDropType fromName(String name)
    {
        HeroesDropType dropType = INVALID;
        if (StringUtils.isNotEmpty(name) && NAME_MAP.containsKey(name.toLowerCase())) {
            dropType = NAME_MAP.get(name.toLowerCase());
        }
        return dropType;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
