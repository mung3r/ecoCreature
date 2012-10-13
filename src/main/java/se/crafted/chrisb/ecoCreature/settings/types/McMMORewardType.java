package se.crafted.chrisb.ecoCreature.settings.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum McMMORewardType
{
    MCMMO_LEVELED("mcMMOLeveled"),
    INVALID("__Invalid__");

    private static final Map<String, McMMORewardType> NAME_MAP = new HashMap<String, McMMORewardType>();

    static {
        for (McMMORewardType type : EnumSet.allOf(McMMORewardType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private String name;

    McMMORewardType(String name)
    {
        if (name != null) {
            this.name = name.toLowerCase();
        }
    }

    public static McMMORewardType fromName(String name)
    {
        McMMORewardType rewardType = INVALID;
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
