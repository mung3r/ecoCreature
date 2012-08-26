package se.crafted.chrisb.ecoCreature.rewards;

import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

public class Exp
{
    private static Random random = new Random();

    private int min;
    private int max;
    private double percentage;

    public int getMin()
    {
        return min;
    }

    public void setMin(int min)
    {
        this.min = min;
    }

    public int getMax()
    {
        return max;
    }

    public void setMax(int max)
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

    public int getAmount()
    {
        int amount;

        if (Math.random() > percentage / 100.0D) {
            amount = 0;
        }
        else {
            if (min == max) {
                amount = min;
            }
            else if (min > max) {
                amount = min;
            }
            else {
                amount = min + random.nextInt(max - min + 1);
            }
        }

        return amount;
    }

    public static Exp parseConfig(ConfigurationSection config)
    {
        Exp exp = null;

        if (config.contains("ExpMin") && config.contains("ExpMax") && config.contains("ExpPercent")) {
            exp = new Exp();
            exp.setMin(config.getInt("ExpMin", 0));
            exp.setMax(config.getInt("ExpMax", 0));
            exp.setPercentage(config.getDouble("ExpPercent", 0.0D));
        }

        return exp;
    }
}
