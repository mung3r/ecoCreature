package se.crafted.chrisb.ecoCreature.models;

import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoDrop
{
    private Material item;
    private int amount;
    private Double percentage;
    private Boolean isFixedDrops;

    public Material getItem()
    {
        return item;
    }

    public void setItem(Material item)
    {
        this.item = item;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public Double getPercentage()
    {
        return percentage;
    }

    public void setPercentage(Double percentage)
    {
        this.percentage = percentage;
    }

    public Boolean getIsFixedDrops()
    {
        return isFixedDrops;
    }

    public void setIsFixedDrops(Boolean isFixedDrops)
    {
        this.isFixedDrops = isFixedDrops;
    }

    public ItemStack computeItemStack()
    {
        if (Math.floor(Math.random() * 100.0D) < percentage) {
            int dropAmount = isFixedDrops ? amount : (new Random()).nextInt(amount) + 1;
            if (dropAmount > 0) {
                ItemStack itemStack = new ItemStack(item, dropAmount);
                if (itemStack.getAmount() > 0) {
                    return itemStack;
                }

                // TODO: Why is this a concern?
                ecoCreature.logger.log(Level.SEVERE, "[ecoCreature] Item stack for drop is zero.");
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return "ecoDrop [item=" + item + ", amount=" + amount + ", percentage=" + percentage + "]";
    }
}
