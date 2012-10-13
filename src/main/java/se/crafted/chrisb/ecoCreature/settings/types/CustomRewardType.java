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

    public String getName()
    {
        return name;
    }
}
