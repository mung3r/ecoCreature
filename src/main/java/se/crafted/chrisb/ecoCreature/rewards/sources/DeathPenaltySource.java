package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

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
    public Reward getOutcome(Event event)
    {
        Reward reward = new Reward(getLocation(event));
        reward.setGain(percentPenalty ? -penaltyAmount / 100.0 : -1.0);

        reward.setName(getName());
        reward.setCoin(percentPenalty ? 0.0 : penaltyAmount);
        reward.setMessage(getCoinPenaltyMessage());
        reward.setIntegerCurrency(isIntegerCurrency());

        return reward;
    }

    public static RewardSource parseConfig(ConfigurationSection config)
    {
        DeathPenaltySource source = null;

        if (config != null) {
            source = new DeathPenaltySource();
            source.setName(CustomType.DEATH_PENALTY.getName());
            source.setPercentPenalty(config.getBoolean("System.Hunting.PenalizeType", true));
            source.setPenaltyAmount(config.getDouble("System.Hunting.PenalizeAmount", 0.05D));
            source.setCoinPenaltyMessage(new DefaultMessage(config.getString("System.Messages.DeathPenaltyMessage", DEATH_PENALTY_MESSAGE)));
        }

        return source;
    }
}
