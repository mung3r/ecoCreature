package se.crafted.chrisb.ecoCreature.models;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ecoDrop
{
    private Material item;
    private byte data;
    private int maxAmount;
    private int minAmount;
    private Double percentage;
    private Boolean isFixedDrops;
    private Set<ecoEnchantment> enchantments;
    private Random random = new Random();

    public ecoDrop()
    {
        this.enchantments = new HashSet<ecoEnchantment>();
    }

    class ecoEnchantment
    {
        private Enchantment enchantment;
        private int level;

        public Enchantment getEnchantment()
        {
            return enchantment;
        }

        public void setEnchantment(Enchantment enchantment)
        {
            this.enchantment = enchantment;
        }

        public int getLevel()
        {
            return level;
        }

        public void setLevel(int level)
        {
            this.level = level;
        }
    }

    public Material getItem()
    {
        return item;
    }

    public void setItem(Material item)
    {
        this.item = item;
    }

    public int getData()
    {
        return data;
    }

    public void setData(byte data)
    {
        this.data = data;
    }

    public int getMaxAmount()
    {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount)
    {
        this.maxAmount = maxAmount;
    }

    public int getMinAmount()
    {
        return minAmount;
    }

    public void setMinAmount(int minAmount)
    {
        this.minAmount = minAmount;
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

    public void addEnchantment(Enchantment enchantment, int level)
    {
        if (enchantment == null) {
            throw new IllegalArgumentException();
        }
        ecoEnchantment e = new ecoEnchantment();
        e.setEnchantment(enchantment);
        e.setLevel(level);
        enchantments.add(e);
    }

    public ItemStack computeItemStack()
    {
        if (random.nextDouble() * 100.0D + 1 < percentage) {
            int dropAmount = isFixedDrops ? maxAmount : minAmount + random.nextInt(Math.abs(maxAmount - minAmount + 1));

            if (dropAmount > 0) {
                ItemStack itemStack;
                if (data == 0) {
                    itemStack = new ItemStack(item, dropAmount);
                }
                else {
                    MaterialData materialData = new MaterialData(item, data);
                    itemStack = materialData.toItemStack(dropAmount);
                }
                for (ecoEnchantment e : enchantments) {
                    itemStack.addEnchantment(e.getEnchantment(), e.getLevel());
                }
                if (itemStack.getAmount() > 0) {
                    return itemStack;
                }
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return "ecoDrop [item=" + item + ", amount=" + maxAmount + ", percentage=" + percentage + "]";
    }
}
