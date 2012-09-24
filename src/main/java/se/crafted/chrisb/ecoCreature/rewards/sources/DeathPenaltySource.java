package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.commons.CustomType;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.rewards.Reward;

public class DeathPenaltySource extends AbstractRewardSource
{
    private static final String DEATH_PENALTY_MESSAGE = "&7You wake up to find &6<amt>&7 missing from your pockets!";

    private boolean percentPenalty;
    private double penaltyAmount;

    public DeathPenaltySource(ConfigurationSection config)
    {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }

        setName(CustomType.DEATH_PENALTY.getName());
        percentPenalty = config.getBoolean("System.Hunting.PenalizeType", true);
        penaltyAmount = config.getDouble("System.Hunting.PenalizeAmount", 0.05D);
        setCoinPenaltyMessage(new DefaultMessage(config.getString("System.Messages.DeathPenaltyMessage", DEATH_PENALTY_MESSAGE)));
    }

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
    protected Location getLocation(Event event)
    {
        if (event instanceof PlayerDeathEvent) {
            return ((PlayerDeathEvent) event).getEntity().getLocation();
        }
        else {
            throw new IllegalArgumentException();
        }
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
}
