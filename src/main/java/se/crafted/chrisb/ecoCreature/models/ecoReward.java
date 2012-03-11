package se.crafted.chrisb.ecoCreature.models;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoReward
{
    public enum RewardType {
        ANGRY_WOLF("AngryWolf"),
        BLAZE("Blaze"),
        CAVE_SPIDER("CaveSpider"),
        CHICKEN("Chicken"),
        COW("Cow"),
        CREEPER("Creeper"),
        DEATH_STREAK("DeathStreak"),
        ENDER_DRAGON("EnderDragon"),
        ENDERMAN("Enderman"),
        GHAST("Ghast"),
        GIANT("Giant"),
        KILL_STREAK("KillStreak"),
        MAGMA_CUBE("LavaSlime"),
        MONSTER("Monster"),
        MUSHROOM_COW("MushroomCow"),
        PIG("Pig"),
        PIG_ZOMBIE("PigZombie"),
        PLAYER("Player"),
        POWERED_CREEPER("PoweredCreeper"),
        SHEEP("Sheep"),
        SILVERFISH("Silverfish"),
        SKELETON("Skeleton"),
        SLIME("Slime"),
        SNOWMAN("Snowman"),
        SPAWNER("Spawner"),
        SPIDER("Spider"),
        SQUID("Squid"),
        VILLAGER("Villager"),
        WOLF("Wolf"),
        ZOMBIE("Zombie"),
        UNKNOWN("Unknown");

        private static final Map<String, RewardType> mapping = new HashMap<String, RewardType>();

        static {
            for (RewardType type : EnumSet.allOf(RewardType.class)) {
                mapping.put(type.name, type);
            }

            // NOTE: backward compatibility
            mapping.put("MagmaCube".toLowerCase(), MAGMA_CUBE);
        }

        private String name;

        RewardType(String name)
        {
            if (name != null)
                this.name = name.toLowerCase();
        }

        public static RewardType fromName(String name)
        {
            RewardType rewardType = null;
            if (name != null)
                rewardType = mapping.get(name.toLowerCase());
            return rewardType;
        }

        public static RewardType fromEntity(Entity entity)
        {
            RewardType rewardType = null;

            if (entity instanceof Creeper) {
                Creeper creeper = (Creeper) entity;
                if (creeper.isPowered()) {
                    rewardType = RewardType.POWERED_CREEPER;
                }
                else {
                    rewardType = RewardType.CREEPER;
                }
            }
            else if (entity instanceof Player) {
                rewardType = RewardType.PLAYER;
            }
            else if (entity instanceof Wolf) {
                Wolf wolf = (Wolf) entity;
                if (wolf.isAngry()) {
                    rewardType = ANGRY_WOLF;
                }
                else {
                    rewardType = WOLF;
                }
            }
            else {
                EntityType creatureType = entity.getType();
                if (creatureType != null) {
                    rewardType = RewardType.fromName(creatureType.getName());
                    if (rewardType == null) {
                        ecoCreature.getEcoLogger().warning("Unknown creature type: " + creatureType.getName());
                    }
                }
            }

            if (rewardType == null) {
                rewardType = UNKNOWN;
            }

            return rewardType;
        }

        public String getName()
        {
            return name;
        }
    }

    private static Random random = new Random();

    private String rewardName;
    private RewardType rewardType;
    private List<ecoDrop> drops;
    private Double coinMin;
    private Double coinMax;
    private Double coinPercentage;
    private Integer expMin;
    private Integer expMax;
    private Double expPercentage;

    private ecoMessage noRewardMessage;
    private ecoMessage rewardMessage;
    private ecoMessage penaltyMessage;

    public String getCreatureName()
    {
        return rewardName;
    }

    public void setRewardName(String creatureName)
    {
        this.rewardName = creatureName;
    }

    public void setRewardType(RewardType creatureType)
    {
        this.rewardType = creatureType;
    }

    public RewardType getRewardType()
    {
        return rewardType;
    }

    public List<ecoDrop> getDrops()
    {
        return drops;
    }

    public void setDrops(List<ecoDrop> drops)
    {
        this.drops = drops;
    }

    public Double getCoinMin()
    {
        return coinMin;
    }

    public void setCoinMin(Double coinMin)
    {
        this.coinMin = coinMin;
    }

    public Double getCoinMax()
    {
        return coinMax;
    }

    public void setCoinMax(Double coinMax)
    {
        this.coinMax = coinMax;
    }

    public Double getCoinPercentage()
    {
        return coinPercentage;
    }

    public void setCoinPercentage(Double coinPercentage)
    {
        this.coinPercentage = coinPercentage;
    }

    public Integer getExpMin()
    {
        return expMin;
    }

    public void setExpMin(Integer expMin)
    {
        this.expMin = expMin;
    }

    public Integer getExpMax()
    {
        return expMax;
    }

    public void setExpMax(Integer expMax)
    {
        this.expMax = expMax;
    }

    public Double getExpPercentage()
    {
        return expPercentage;
    }

    public void setExpPercentage(Double expPercentage)
    {
        this.expPercentage = expPercentage;
    }

    public ecoMessage getNoRewardMessage()
    {
        return noRewardMessage;
    }

    public void setNoRewardMessage(ecoMessage noRewardMessage)
    {
        this.noRewardMessage = noRewardMessage;
    }

    public ecoMessage getRewardMessage()
    {
        return rewardMessage;
    }

    public void setRewardMessage(ecoMessage rewardMessage)
    {
        this.rewardMessage = rewardMessage;
    }

    public ecoMessage getPenaltyMessage()
    {
        return penaltyMessage;
    }

    public void setPenaltyMessage(ecoMessage penaltyMessage)
    {
        this.penaltyMessage = penaltyMessage;
    }

    public List<ItemStack> computeDrops()
    {
        List<ItemStack> stack = new ArrayList<ItemStack>();

        if (drops != null) {
            for (ecoDrop drop : drops) {
                ItemStack itemStack = drop.computeItemStack();
                if (itemStack != null) {
                    stack.add(itemStack);
                }
            }
        }

        return stack;
    }

    public Double getRewardAmount()
    {
        Double rewardAmount;

        if (Math.random() > coinPercentage / 100.0D) {
            rewardAmount = 0.0D;
        }
        else {
            if (coinMin == coinMax) {
                rewardAmount = coinMax;
            }
            else if (coinMin > coinMax) {
                rewardAmount = coinMin;
            }
            else {
                rewardAmount = coinMin + Math.random() * (coinMax - coinMin);
            }
        }

        return rewardAmount;
    }

    public Integer getExpAmount()
    {
        Integer xpAmount;
        if (expMin == null || expMax == null || expPercentage == null) {
            xpAmount = null;
        }
        else {
            if (Math.random() > expPercentage / 100.0D) {
                xpAmount = 0;
            }
            else {
                if (expMin == expMax) {
                    xpAmount = expMin;
                }
                else if (expMin > expMax) {
                    xpAmount = expMin;
                }
                else {
                    xpAmount = expMin + random.nextInt(expMax - expMin + 1);
                }
            }
        }

        return xpAmount;
    }

    @Override
    public String toString()
    {
        return String.format("ecoReward [rewardName=%s, rewardType=%s, drops=%s, coinMin=%s, coinMax=%s, coinPercentage=%s, expMin=%s, expMax=%s, expPercentage=%s, noRewardMessage=%s, rewardMessage=%s, penaltyMessage=%s]", rewardName,
                rewardType, drops, coinMin, coinMax, coinPercentage, expMin, expMax, expPercentage, noRewardMessage, rewardMessage, penaltyMessage);
    }
}
