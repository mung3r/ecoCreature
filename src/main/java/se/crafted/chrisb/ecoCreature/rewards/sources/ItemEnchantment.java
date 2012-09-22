package se.crafted.chrisb.ecoCreature.rewards.sources;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;

public class ItemEnchantment
{
    private final Random random = new Random();

    private Enchantment enchantment;
    private int minLevel;
    private int maxLevel;

    public Enchantment getEnchantment()
    {
        return enchantment;
    }

    public void setEnchantment(Enchantment enchantment)
    {
        this.enchantment = enchantment;
    }

    public int getMinLevel()
    {
        return minLevel;
    }

    public void setMinLevel(int minLevel)
    {
        this.minLevel = minLevel;
    }

    public int getMaxLevel()
    {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel)
    {
        this.maxLevel = maxLevel;
    }

    public int getLevel()
    {
        return minLevel + random.nextInt(Math.abs(maxLevel - minLevel + 1));
    }

    public static Map<Enchantment, Integer> getOutcome(Set<ItemEnchantment> enchantments)
    {
        Map<Enchantment, Integer> enchantmentMap = new HashMap<Enchantment, Integer>();

        for (ItemEnchantment enchantment : enchantments) {
            int level = enchantment.getLevel();
            if (level > 0) {
                enchantmentMap.put(enchantment.getEnchantment(), Integer.valueOf(level));
            }
        }

        return enchantmentMap;
    }
}
