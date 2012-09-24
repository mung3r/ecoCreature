package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.CustomType;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.rewards.Reward;

public class PVPRewardSource extends RewardSource
{
    private static final String PVP_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for murdering &5<crt>&7.";

    private boolean percentReward;
    private double rewardAmount;

    public PVPRewardSource(ConfigurationSection config)
    {
        name = CustomType.LEGACY_PVP.getName();
        percentReward = config.getBoolean("System.Hunting.PVPRewardType", true);
        rewardAmount = config.getDouble("System.Hunting.PVPRewardAmount", 0.05D);
        coinRewardMessage = new DefaultMessage(config.getString("System.Messages.PVPRewardMessage", PVP_REWARD_MESSAGE));
    }

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
    protected Location getLocation(Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            return ((PlayerKilledEvent) event).getVictim().getLocation();
        }
        else {
            throw new IllegalArgumentException("Unrecognized event");
        }
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
}
