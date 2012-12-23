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
package se.crafted.chrisb.ecoCreature.commons;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.LongRange;
import org.bukkit.entity.Entity;

public enum TimePeriod
{
    DAY, SUNSET, DUSK, NIGHT, DAWN, SUNRISE, NONE;

    private static final Map<LongRange, TimePeriod> PERIOD_MAP = new HashMap<LongRange, TimePeriod>();

    private static final long DAY_START = 0;
    private static final long SUNSET_START = 13000;
    private static final long DUSK_START = 13500;
    private static final long NIGHT_START = 14000;
    private static final long DAWN_START = 22000;
    private static final long SUNRISE_START = 23000;
    private static final long DAY_END = 24000;

    static {
        PERIOD_MAP.put(new LongRange(DAY_START, SUNSET_START - 1), DAY);
        PERIOD_MAP.put(new LongRange(SUNSET_START, DUSK_START - 1), SUNSET);
        PERIOD_MAP.put(new LongRange(DUSK_START, NIGHT_START - 1), DUSK);
        PERIOD_MAP.put(new LongRange(NIGHT_START, DAWN_START - 1), NIGHT);
        PERIOD_MAP.put(new LongRange(DAWN_START, SUNRISE_START - 1), DAWN);
        PERIOD_MAP.put(new LongRange(SUNRISE_START, DAY_END - 1), SUNRISE);
    }

    public static TimePeriod fromEntity(Entity entity)
    {
        TimePeriod timePeriod = NONE;

        if (entity != null) {
            timePeriod = fromTime(entity.getWorld().getTime());
        }

        return timePeriod;
    }

    public static TimePeriod fromTime(long time)
    {
        TimePeriod timePeriod = NONE;

        for (LongRange period : PERIOD_MAP.keySet()) {
            if (period.containsLong(time)) {
                timePeriod = PERIOD_MAP.get(period);
                break;
            }
        }

        return timePeriod;
    }
}
