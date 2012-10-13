package se.crafted.chrisb.ecoCreature.settings.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public enum CustomEntityRewardType
{
    ANGRY_WOLF("AngryWolf"),
    PLAYER("Player"),
    POWERED_CREEPER("PoweredCreeper"),
    INVALID("__Invalid__");

    private static final Map<String, CustomEntityRewardType> NAME_MAP = new HashMap<String, CustomEntityRewardType>();

    static {
        for (CustomEntityRewardType type : EnumSet.allOf(CustomEntityRewardType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private String name;

    CustomEntityRewardType(String name)
    {
        if (name != null) {
            this.name = name.toLowerCase();
        }
    }

    public static CustomEntityRewardType fromName(String name)
    {
        CustomEntityRewardType rewardType = INVALID;
        if (name != null && NAME_MAP.containsKey(name.toLowerCase())) {
            rewardType = NAME_MAP.get(name.toLowerCase());
        }
        return rewardType;
    }

    public static CustomEntityRewardType fromEntity(Entity entity)
    {
        CustomEntityRewardType entityType = INVALID;

        if (entity instanceof Creeper && ((Creeper) entity).isPowered()) {
            entityType = CustomEntityRewardType.POWERED_CREEPER;
        }
        else if (entity instanceof Player) {
            entityType = CustomEntityRewardType.PLAYER;
        }
        else if (entity instanceof Wolf && ((Wolf) entity).isAngry()) {
            entityType = CustomEntityRewardType.ANGRY_WOLF;
        }
        else if (entity instanceof LivingEntity) {
            entityType = CustomEntityRewardType.fromName(entity.getType().getName());
        }

        if (entityType == INVALID) {
            ECLogger.getInstance().debug(CustomEntityRewardType.class, "No match for entity type: " + entity.getType().getName());
        }

        return entityType;
    }

    public String getName()
    {
        return name;
    }
}
