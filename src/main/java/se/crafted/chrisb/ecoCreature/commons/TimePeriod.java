package se.crafted.chrisb.ecoCreature.commons;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;

public enum TimePeriod
{
    DAY, SUNSET, DUSK, NIGHT, DAWN, SUNRISE, IDENTITY;

    private static final Map<String, TimePeriod> NAME_MAP = new HashMap<String, TimePeriod>();

    private static final long DAY_START = 0;
    private static final long SUNSET_START = 13000;
    private static final long DUSK_START = 13500;
    private static final long NIGHT_START = 14000;
    private static final long DAWN_START = 22000;
    private static final long SUNRISE_START = 23000;

    static {
        for (TimePeriod type : TimePeriod.values()) {
            NAME_MAP.put(type.toString(), type);
        }
    }

    public static TimePeriod fromName(String period)
    {
        return NAME_MAP.get(period.toUpperCase());
    }

    public static TimePeriod fromEntity(Entity entity)
    {
        TimePeriod timePeriod = IDENTITY;

        if (entity != null) {
            long time = entity.getWorld().getTime();

            if (time >= DAY_START && time < SUNSET_START) {
                timePeriod = DAY;
            }
            else if (time >= SUNSET_START && time < DUSK_START) {
                timePeriod = SUNSET;
            }
            else if (time >= DUSK_START && time < NIGHT_START) {
                timePeriod = DUSK;
            }
            else if (time >= NIGHT_START && time < DAWN_START) {
                timePeriod = NIGHT;
            }
            else if (time >= DAWN_START && time < SUNRISE_START) {
                timePeriod = DAWN;
            }
            else if (time >= SUNRISE_START && time < DAY_START) {
                timePeriod = SUNRISE;
            }
        }

        return timePeriod;
    }
}
