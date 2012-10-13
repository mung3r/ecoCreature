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

    public String getName()
    {
        return name;
    }
}
