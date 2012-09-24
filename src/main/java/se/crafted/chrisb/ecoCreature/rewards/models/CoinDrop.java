package se.crafted.chrisb.ecoCreature.rewards.models;

import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.configuration.ConfigurationSection;

public class CoinDrop
{
    private DoubleRange range;
    private double percentage;

    public DoubleRange getRange()
    {
        return range;
    }

    public void setRange(DoubleRange range)
    {
        this.range = range;
    }

    public double getPercentage()
    {
        return percentage;
    }

    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
    }

    public double getOutcome()
    {
        double amount;

        if (Math.random() > percentage / 100.0D) {
            amount = 0.0D;
        }
        else {
            if (range.getMinimumDouble() == range.getMaximumDouble()) {
                amount = range.getMaximumDouble();
            }
            else if (range.getMinimumDouble() > range.getMaximumDouble()) {
                amount = range.getMinimumDouble();
            }
            else {
                amount = range.getMinimumDouble() + Math.random() * (range.getMaximumDouble() - range.getMinimumDouble());
            }
        }

        return amount;
    }

    public static CoinDrop parseConfig(ConfigurationSection config)
    {
        CoinDrop coin = null;

        if (config != null && config.contains("Coin_Maximum") && config.contains("Coin_Minimum") && config.contains("Coin_Percent")) {
            coin = new CoinDrop();
            coin.setRange(new DoubleRange(config.getDouble("Coin_Minimum", 0), config.getDouble("Coin_Maximum", 0)));
            coin.setPercentage(config.getDouble("Coin_Percent", 0.0D));
        }

        return coin;
    }
}
