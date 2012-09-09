package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.rewards.Reward;

public class PVPRewardSource extends DefaultRewardSource
{
    private static final String PVP_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for murdering &5<crt>&7.";

    private boolean percentReward;
    private double rewardAmount;

    public boolean isPercentReward()
    {
        return percentReward;
    }

    public void setPercentReward(boolean percentReward)
    {
        this.percentReward = percentReward;
    }

    public double getRewardAmount()
    {
        return rewardAmount;
    }

    public void setRewardAmount(double rewardAmount)
    {
        this.rewardAmount = rewardAmount;
    }

    @Override
    public Reward getOutcome(Location location)
    {
        Reward reward = new Reward(location);
        reward.setGain(percentReward ? rewardAmount / 100.0 : 1.0);

        reward.setType(getType());
        reward.setName(getName());
        reward.setCoin(isPercentReward() ? 0.0 : rewardAmount);
        reward.setMessage(getCoinRewardMessage());
        reward.setIntegerCurrency(isIntegerCurrency());

        return reward;
    }

    public static RewardSource parseConfig(RewardSourceType type, ConfigurationSection config)
    {
        PVPRewardSource source = null;

        if (config != null) {
            source = new PVPRewardSource();
            source.setName(type.getName());
            source.setType(type);
            source.setPercentReward(config.getBoolean("System.Hunting.PVPRewardType", true));
            source.setRewardAmount(config.getDouble("System.Hunting.PVPRewardAmount", 0.05D));
            source.setCoinRewardMessage(new DefaultMessage(config.getString("System.Messages.PVPRewardMessage", PVP_REWARD_MESSAGE)));
        }

        return source;
    }
}
