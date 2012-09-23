package se.crafted.chrisb.ecoCreature.commons;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;


public enum CustomType
{
    ANGRY_WOLF("AngryWolf"),
    DEATH_PENALTY("DeathPenalty"),
    DEATH_STREAK("DeathStreak"), 
    HERO_MASTERED("HeroMastered"),
    KILL_STREAK("KillStreak"),
    LEGACY_PVP("LegacyPVP"),
    LEGACY_SPAWNER("Spawner"),
    PLAYER("Player"),
    POWERED_CREEPER("PoweredCreeper"),
    SET("Set");

    private static final Map<String, CustomType> NAME_MAP = new HashMap<String, CustomType>();

    static {
        for (CustomType type : EnumSet.allOf(CustomType.class)) {
            NAME_MAP.put(type.name, type);
        }
    }

    private String name;

    CustomType(String name)
    {
        if (name != null) {
            this.name = name.toLowerCase();
        }
    }

    public static CustomType fromName(String name)
    {
        CustomType rewardType = null;
        if (name != null) {
            rewardType = NAME_MAP.get(name.toLowerCase());
        }
        return rewardType;
    }

    public static CustomType fromEntity(Entity entity)
    {
        CustomType rewardType = null;

        if (entity instanceof Creeper && ((Creeper) entity).isPowered()) {
            rewardType = CustomType.POWERED_CREEPER;
        }
        else if (entity instanceof Player) {
            rewardType = CustomType.PLAYER;
        }
        else if (entity instanceof Wolf && ((Wolf) entity).isAngry()) {
            rewardType = CustomType.ANGRY_WOLF;
        }
        else if (entity instanceof LivingEntity) {
            rewardType = CustomType.fromName(entity.getType().getName());
        }

        if (rewardType == null) {
            ECLogger.getInstance().warning("Unknown creature type: " + entity.getType().getName());
        }

        return rewardType;
    }

    public String getName()
    {
        return name;
    }
}
