package se.crafted.chrisb.ecoCreature.models;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.CreatureType;
import org.bukkit.inventory.ItemStack;

public class ecoReward
{
    private String rewardName;
    private CreatureType creatureType;
    private List<ecoDrop> drops;
    private double coinMin;
    private double coinMax;
    private double coinPercentage;
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

    public void setCreatureType(CreatureType creatureType)
    {
        this.creatureType = creatureType;
    }

    public CreatureType getCreatureType()
    {
        return creatureType;
    }

    public List<ecoDrop> getDrops()
    {
        return drops;
    }

    public void setDrops(List<ecoDrop> drops)
    {
        this.drops = drops;
    }

    public double getCoinMin()
    {
        return coinMin;
    }

    public void setCoinMin(double coinMin)
    {
        this.coinMin = coinMin;
    }

    public double getCoinMax()
    {
        return coinMax;
    }

    public void setCoinMax(double coinMax)
    {
        this.coinMax = coinMax;
    }

    public double getCoinPercentage()
    {
        return coinPercentage;
    }

    public void setCoinPercentage(double coinPercentage)
    {
        this.coinPercentage = coinPercentage;
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
            for (ecoDrop drop : drops) {
                ItemStack itemStack = drop.computeItemStack();
                if (itemStack != null) {
                    stack.add(itemStack);
                }
            }
        }

        return stack;
    }

    @Override
    public String toString()
    {
        return "ecoReward [rewardName=" + rewardName + ", creatureType=" + creatureType + ", drops=" + drops + ", coinMin=" + coinMin + ", coinMax=" + coinMax + ", coinPercentage=" + coinPercentage + ", noRewardMessage=" + noRewardMessage
                + ", rewardMessage=" + rewardMessage + ", penaltyMessage=" + penaltyMessage + "]";
    }
}
