package se.crafted.chrisb.ecoCreature.settings.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum StreakRewardType
{
    DEATH_STREAK("DeathStreak"),
    KILL_STREAK("KillStreak"),
    INVALID("__Invalid__");

    private static final Map<String, StreakRewardType> NAME_MAP = new HashMap<String, StreakRewardType>();

    static {
        for (StreakRewardType type : EnumSet.allOf(StreakRewardType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private String name;

    StreakRewardType(String name)
    {
        if (name != null) {
            this.name = name.toLowerCase();
        }
    }

    public static StreakRewardType fromName(String name)
    {
        StreakRewardType rewardType = INVALID;
        if (name != null && NAME_MAP.containsKey(name.toLowerCase())) {
            rewardType = NAME_MAP.get(name.toLowerCase());
        }
        return rewardType;
    }

    public String getName()
    {
        return name;
    }
}
