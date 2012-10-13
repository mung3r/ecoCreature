package se.crafted.chrisb.ecoCreature.settings.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum HeroesRewardType
{
    HERO_LEVELED("HeroLeveled"),
    HERO_MASTERED("HeroMastered"),
    INVALID("__Invalid__");

    private static final Map<String, HeroesRewardType> NAME_MAP = new HashMap<String, HeroesRewardType>();

    static {
        for (HeroesRewardType type : EnumSet.allOf(HeroesRewardType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private String name;

    HeroesRewardType(String name)
    {
        if (name != null) {
            this.name = name.toLowerCase();
        }
    }

    public static HeroesRewardType fromName(String name)
    {
        HeroesRewardType rewardType = INVALID;
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
