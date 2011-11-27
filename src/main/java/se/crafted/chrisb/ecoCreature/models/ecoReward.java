package se.crafted.chrisb.ecoCreature.models;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil;

public class ecoReward
{
    public enum RewardType {
        ANGRY_WOLF("AngryWolf"),
        BLAZE("Blaze"),
        CAVE_SPIDER("CaveSpider"),
        CHICKEN("Chicken"),
        COW("Cow"),
        CREEPER("Creeper"),
        ENDER_DRAGON("EnderDragon"),
        ENDERMAN("Enderman"),
        GHAST("Ghast"),
        GIANT("Giant"),
        KILL_STREAK("KillStreak"),
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
        SPAWNER("Spawner"),
        SPIDER("Spider"),
        SQUID("Squid"),
        VILLAGER("Villager"),
        WOLF("Wolf"),
        ZOMBIE("Zombie");

        private static final Map<String, RewardType> mapping = new HashMap<String, RewardType>();

        static {
            for (RewardType type : EnumSet.allOf(RewardType.class)) {
                mapping.put(type.name, type);
            }
        }

        private String name;

        RewardType(String name)
        {
            this.name = name;
        }

        public static RewardType fromName(String name)
        {
            return mapping.get(name);
        }

        public static RewardType fromEntity(Entity entity)
        {
            RewardType rewardType = null;

            if (entity instanceof Player) {
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
                CreatureType creatureType = ecoEntityUtil.getCreatureType(entity);
                if (creatureType != null) {
                    rewardType = RewardType.fromName(creatureType.getName());
                }
            }

            return rewardType;
        }
    }

    private String rewardName;
    private RewardType rewardType;
    private List<ecoDrop> drops;
    private Double coinMin;
    private Double coinMax;
    private Double coinPercentage;
    private Double rewardAmount;

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
        if (coinMin == coinMax) {
            rewardAmount = coinMax;
        }
        else if (coinMin > coinMax) {
            rewardAmount = coinMin;
        }
        else {
            rewardAmount = coinMin + Math.random() * (coinMax - coinMin);
        }

        if (Math.random() > coinPercentage / 100.0D) {
            rewardAmount = 0.0D;
        }

        return rewardAmount;
    }

    @Override
    public String toString()
    {
        return "ecoReward [rewardName=" + rewardName + ", creatureType=" + rewardType + ", drops=" + drops + ", coinMin=" + coinMin + ", coinMax=" + coinMax + ", coinPercentage=" + coinPercentage + ", rewardAmount=" + rewardAmount
                + ", noRewardMessage=" + noRewardMessage + ", rewardMessage=" + rewardMessage + ", penaltyMessage=" + penaltyMessage + "]";
    }
}
