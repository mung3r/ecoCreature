package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.CustomType;
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
    public Reward getOutcome(Event event)
    {
        Reward reward = new Reward(getLocation(event));
        reward.setGain(percentReward ? rewardAmount / 100.0 : 1.0);

        reward.setName(getName());
        reward.setCoin(isPercentReward() ? 0.0 : rewardAmount);
        reward.setMessage(getCoinRewardMessage());
        reward.setIntegerCurrency(isIntegerCurrency());

        return reward;
    }

    public static RewardSource parseConfig(ConfigurationSection config)
    {
        PVPRewardSource source = null;

        if (config != null) {
            source = new PVPRewardSource();
            source.setName(CustomType.LEGACY_PVP.getName());
            source.setPercentReward(config.getBoolean("System.Hunting.PVPRewardType", true));
            source.setRewardAmount(config.getDouble("System.Hunting.PVPRewardAmount", 0.05D));
            source.setCoinRewardMessage(new DefaultMessage(config.getString("System.Messages.PVPRewardMessage", PVP_REWARD_MESSAGE)));
        }

        return source;
    }
}
