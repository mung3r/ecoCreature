package se.crafted.chrisb.ecoCreature.settings.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum DeathTpPlusRewardType
{
    DEATH_STREAK("DeathStreak"),
    KILL_STREAK("KillStreak"),
    INVALID("__Invalid__");

    private static final Map<String, DeathTpPlusRewardType> NAME_MAP = new HashMap<String, DeathTpPlusRewardType>();

    static {
        for (DeathTpPlusRewardType type : EnumSet.allOf(DeathTpPlusRewardType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private String name;

    DeathTpPlusRewardType(String name)
    {
        if (name != null) {
            this.name = name.toLowerCase();
        }
    }

    public static DeathTpPlusRewardType fromName(String name)
    {
        DeathTpPlusRewardType rewardType = INVALID;
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
