package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.rewards.Reward;

public class DeathPenaltySource extends DefaultRewardSource
{
    private static final String DEATH_PENALTY_MESSAGE = "&7You wake up to find &6<amt>&7 missing from your pockets!";

    private boolean percentPenalty;
    private double penaltyAmount;

    public boolean isPercentPenalty()
    {
        return percentPenalty;
    }

    public void setPercentPenalty(boolean percentPenalty)
    {
        this.percentPenalty = percentPenalty;
    }

    public double getPenaltyAmount()
    {
        return penaltyAmount;
    }

    public void setPenaltyAmount(double penaltyAmount)
    {
        this.penaltyAmount = penaltyAmount;
    }

    @Override
    public Reward getOutcome(Location location)
    {
        Reward reward = new Reward(location);
        reward.setGain(percentPenalty ? -penaltyAmount / 100.0 : -1.0);

        reward.setType(getType());
        reward.setName(getName());
        reward.setCoin(percentPenalty ? 0.0 : penaltyAmount);
        reward.setMessage(getCoinPenaltyMessage());
        reward.setIntegerCurrency(isIntegerCurrency());

        return reward;
    }

    public static RewardSource parseConfig(RewardSourceType type, ConfigurationSection config)
    {
        DeathPenaltySource source = null;

        if (config != null) {
            source = new DeathPenaltySource();
            source.setName(type.getName());
            source.setType(type);
            source.setPercentPenalty(config.getBoolean("System.Hunting.PenalizeType", true));
            source.setPenaltyAmount(config.getDouble("System.Hunting.PenalizeAmount", 0.05D));
            source.setCoinPenaltyMessage(new DefaultMessage(config.getString("System.Messages.DeathPenaltyMessage", DEATH_PENALTY_MESSAGE)));
        }

        return source;
    }
}
