package se.crafted.chrisb.ecoCreature.rewards;

import org.bukkit.configuration.ConfigurationSection;

public class Coin
{
    private double min;
    private double max;
    private double percentage;

    public double getMin()
    {
        return min;
    }

    public void setMin(double min)
    {
        this.min = min;
    }

    public double getMax()
    {
        return max;
    }

    public void setMax(double max)
    {
        this.max = max;
    }

    public double getPercentage()
    {
        return percentage;
    }

    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
    }

    public double getAmount()
    {
        double amount;

        if (Math.random() > percentage / 100.0D) {
            amount = 0.0D;
        }
        else {
            if (min == max) {
                amount = max;
            }
            else if (min > max) {
                amount = min;
            }
            else {
                amount = min + Math.random() * (max - min);
            }
        }

        return amount;
    }

    public static Coin parseConfig(ConfigurationSection config)
    {
        Coin coin = null;

        if (config.contains("Coin_Maximum") && config.contains("Coin_Minimum") && config.contains("Coin_Percent")) {
            coin = new Coin();
            coin.setMax(config.getDouble("Coin_Maximum", 0));
            coin.setMin(config.getDouble("Coin_Minimum", 0));
            coin.setPercentage(config.getDouble("Coin_Percent", 0.0D));
        }

        return coin;
    }
}
