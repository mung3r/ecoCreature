package se.crafted.chrisb.ecoCreature.rewards;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public enum RewardType
{
    ANGRY_WOLF("AngryWolf"),
    BLAZE("Blaze"),
    CAVE_SPIDER("CaveSpider"),
    CHICKEN("Chicken"),
    COW("Cow"),
    CREEPER("Creeper"),
    CUSTOM("Custom"),
    DEATH_PENALTY("DeathPenalty"),
    DEATH_STREAK("DeathStreak"),
    ENDER_DRAGON("EnderDragon"),
    ENDERMAN("Enderman"),
    GHAST("Ghast"),
    GIANT("Giant"),
    HERO_MASTERED("HeroMastered"),
    IRON_GOLEM("VillagerGolem"),
    KILL_STREAK("KillStreak"),
    MAGMA_CUBE("LavaSlime"),
    MONSTER("Monster"),
    MUSHROOM_COW("MushroomCow"),
    OCELOT("Ozelot"),
    PIG("Pig"),
    PIG_ZOMBIE("PigZombie"),
    PLAYER("Player"),
    POWERED_CREEPER("PoweredCreeper"),
    SHEEP("Sheep"),
    SILVERFISH("Silverfish"),
    SKELETON("Skeleton"),
    SLIME("Slime"),
    SNOWMAN("SnowMan"),
    SPAWNER("Spawner"),
    SPIDER("Spider"),
    SQUID("Squid"),
    VILLAGER("Villager"),
    WOLF("Wolf"),
    ZOMBIE("Zombie"),
    UNKNOWN("Unknown");

    private static final Map<String, RewardType> NAME_MAP = new HashMap<String, RewardType>();

    static {
        for (RewardType type : EnumSet.allOf(RewardType.class)) {
            NAME_MAP.put(type.name, type);
        }

        // NOTE: backward compatibility
        NAME_MAP.put("MagmaCube".toLowerCase(), MAGMA_CUBE);
    }

    private String name;

    RewardType(String name)
    {
        if (name != null) {
            this.name = name.toLowerCase();
        }
    }

    public static RewardType fromName(String name)
    {
        RewardType rewardType = null;
        if (name != null) {
            rewardType = NAME_MAP.get(name.toLowerCase());
        }
        return rewardType;
    }

    public static RewardType fromEntity(Entity entity)
    {
        RewardType rewardType = null;

        if (entity instanceof Creeper) {
            rewardType = ((Creeper) entity).isPowered() ? RewardType.POWERED_CREEPER : RewardType.CREEPER;
        }
        else if (entity instanceof Player) {
            rewardType = RewardType.PLAYER;
        }
        else if (entity instanceof Wolf) {
            rewardType = ((Wolf) entity).isAngry() ? RewardType.ANGRY_WOLF : RewardType.WOLF;
        }
        else if (entity instanceof LivingEntity) {
            rewardType = RewardType.fromName(entity.getType().getName());
        }

        if (rewardType == null) {
            ECLogger.getInstance().warning("Unknown creature type: " + entity.getType().getName());
            rewardType = UNKNOWN;
        }

        return rewardType;
    }

    public String getName()
    {
        return name;
    }
}
