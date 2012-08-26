package se.crafted.chrisb.ecoCreature.rewards;

import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class Exp
{
    private static Random random = new Random();

    private Integer min;
    private Integer max;
    private Double percentage;

    public Integer getMin()
    {
        return min;
    }

    public void setMin(Integer min)
    {
        this.min = min;
    }

    public Integer getMax()
    {
        return max;
    }

    public void setMax(Integer exp)
    {
        this.max = exp;
    }

    public Double getPercentage()
    {
        return percentage;
    }

    public void setPercentage(Double percentage)
    {
        this.percentage = percentage;
    }

    public Integer getAmount()
    {
        Integer amount;
        if (min == null || max == null || percentage == null) {
            amount = null;
        }
        else {
            if (Math.random() > percentage / 100.0D) {
                amount = 0;
            }
            else {
                if (min.equals(max)) {
                    amount = min;
                }
                else if (min > max) {
                    amount = min;
                }
                else {
                    amount = min + random.nextInt(max - min + 1);
                }
            }
        }

        return amount;
    }

    public static Exp parseConfig(ConfigurationSection config)
    {
        Exp exp = null;

        if (config.contains("ExpMin") && config.contains("ExpMax") && config.contains("ExpPercent")) {
            try {
                exp = new Exp();
                exp.setMin(Integer.parseInt(config.getString("ExpMin")));
                exp.setMax(Integer.parseInt(config.getString("ExpMax")));
                exp.setPercentage(Double.parseDouble(config.getString("ExpPercent")));
            }
            catch (NumberFormatException e) {
                ECLogger.getInstance().warning("Could not parse exp for " + config.getName());
            }
        }

        return exp;
    }
}
