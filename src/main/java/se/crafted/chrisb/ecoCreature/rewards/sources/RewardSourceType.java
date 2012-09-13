package se.crafted.chrisb.ecoCreature.rewards.sources;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public enum RewardSourceType
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
    LEGACY_PVP("LegacyPVP"),
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

    private static final Map<String, RewardSourceType> NAME_MAP = new HashMap<String, RewardSourceType>();

    static {
        for (RewardSourceType type : EnumSet.allOf(RewardSourceType.class)) {
            NAME_MAP.put(type.name, type);
        }

        // NOTE: backward compatibility
        NAME_MAP.put("MagmaCube".toLowerCase(), MAGMA_CUBE);
    }

    private String name;

    RewardSourceType(String name)
    {
        if (name != null) {
            this.name = name.toLowerCase();
        }
    }

    public static RewardSourceType fromName(String name)
    {
        RewardSourceType rewardType = null;
        if (name != null) {
            rewardType = NAME_MAP.get(name.toLowerCase());
        }
        return rewardType;
    }

    public static RewardSourceType fromEntity(Entity entity)
    {
        RewardSourceType rewardType = null;

        if (entity instanceof Creeper) {
            rewardType = ((Creeper) entity).isPowered() ? RewardSourceType.POWERED_CREEPER : RewardSourceType.CREEPER;
        }
        else if (entity instanceof Player) {
            rewardType = RewardSourceType.PLAYER;
        }
        else if (entity instanceof Wolf) {
            rewardType = ((Wolf) entity).isAngry() ? RewardSourceType.ANGRY_WOLF : RewardSourceType.WOLF;
        }
        else if (entity instanceof LivingEntity) {
            rewardType = RewardSourceType.fromName(entity.getType().getName());
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

    public String getPermission()
    {
        return "ecocreature.reward." + name;
    }
}
