package se.crafted.chrisb.ecoCreature.rewards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Reward
{
    private String name;
    private RewardType type;
    private Coin coin;
    private List<Drop> drops;
    private Exp exp;

    private String noRewardMessage;
    private String rewardMessage;
    private String penaltyMessage;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setType(RewardType type)
    {
        this.type = type;
    }

    public RewardType getType()
    {
        return type;
    }

    public boolean hasCoin()
    {
        return coin != null;
    }

    public Coin getCoin()
    {
        return coin;
    }

    public void setCoin(Coin coin)
    {
        this.coin = coin;
    }

    public boolean hasDrops()
    {
        return drops != null;
    }

    public List<Drop> getDrops()
    {
        return drops;
    }

    public void setDrops(List<Drop> drops)
    {
        this.drops = drops;
    }

    public boolean hasExp()
    {
        return exp != null;
    }

    public Exp getExp()
    {
        return exp;
    }

    public void setExp(Exp exp)
    {
        this.exp = exp;
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

    public List<ItemStack> getDropOutcomes(boolean isFixedDrops)
    {
        List<ItemStack> stacks = new ArrayList<ItemStack>();

        if (drops != null) {
            for (Drop drop : drops) {
                ItemStack itemStack = drop.getOutcome(isFixedDrops);
                if (itemStack != null) {
                    stacks.add(itemStack);
                }
            }
        }

        return stacks;
    }

    @Override
    public String toString()
    {
        return String.format("Reward [name=%s, type=%s, drops=%s, noRewardMessage=%s, rewardMessage=%s, penaltyMessage=%s]",
                name, type, drops, noRewardMessage, rewardMessage, penaltyMessage);
    }
}
