package se.crafted.chrisb.ecoCreature.rewards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

public class Reward
{
    private static Random random = new Random();

    private String rewardName;
    private RewardType rewardType;
    private List<Drop> drops;
    private Double coinMin;
    private Double coinMax;
    private Double coinPercentage;
    private Integer expMin;
    private Integer expMax;
    private Double expPercentage;

    private String noRewardMessage;
    private String rewardMessage;
    private String penaltyMessage;

    public String getRewardName()
    {
        return rewardName;
    }

    public void setRewardName(String rewardName)
    {
        this.rewardName = rewardName;
    }

    public void setRewardType(RewardType creatureType)
    {
        this.rewardType = creatureType;
    }

    public RewardType getRewardType()
    {
        return rewardType;
    }

    public List<Drop> getDrops()
    {
        return drops;
    }

    public void setDrops(List<Drop> drops)
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

    public String getNoRewardMessage()
    {
        return noRewardMessage;
    }

    public void setNoRewardMessage(String noRewardMessage)
    {
        this.noRewardMessage = noRewardMessage;
    }

    public String getRewardMessage()
    {
        return rewardMessage;
    }

    public void setRewardMessage(String rewardMessage)
    {
        this.rewardMessage = rewardMessage;
    }

    public String getPenaltyMessage()
    {
        return penaltyMessage;
    }

    public void setPenaltyMessage(String penaltyMessage)
    {
        this.penaltyMessage = penaltyMessage;
    }

    public List<ItemStack> computeDrops()
    {
        List<ItemStack> stack = new ArrayList<ItemStack>();

        if (drops != null) {
            for (Drop drop : drops) {
                ItemStack itemStack = drop.computeItemStack();
                if (itemStack != null) {
                    stack.add(itemStack);
                }
            }
        }

        return stack;
    }

    public double getRewardAmount()
    {
        double rewardAmount;

        if (Math.random() > coinPercentage / 100.0D) {
            rewardAmount = 0.0D;
        }
        else {
            if (coinMin.equals(coinMax)) {
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
        Integer expAmount;
        if (expMin == null || expMax == null || expPercentage == null) {
            expAmount = null;
        }
        else {
            if (Math.random() > expPercentage / 100.0D) {
                expAmount = 0;
            }
            else {
                if (expMin.equals(expMax)) {
                    expAmount = expMin;
                }
                else if (expMin > expMax) {
                    expAmount = expMin;
                }
                else {
                    expAmount = expMin + random.nextInt(expMax - expMin + 1);
                }
            }
        }

        return expAmount;
    }

    @Override
    public String toString()
    {
        return String.format("Reward [rewardName=%s, rewardType=%s, drops=%s, coinMin=%s, coinMax=%s, coinPercentage=%s, expMin=%s, expMax=%s, expPercentage=%s, noRewardMessage=%s, rewardMessage=%s, penaltyMessage=%s]", rewardName,
                rewardType, drops, coinMin, coinMax, coinPercentage, expMin, expMax, expPercentage, noRewardMessage, rewardMessage, penaltyMessage);
    }
}
