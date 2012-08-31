package se.crafted.chrisb.ecoCreature.rewards;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Enchantment
{
    private org.bukkit.enchantments.Enchantment enchantment;
    private int minLevel;
    private int maxLevel;
    private final Random random = new Random();

    public org.bukkit.enchantments.Enchantment getEnchantment()
    {
        return enchantment;
    }

    public void setEnchantment(org.bukkit.enchantments.Enchantment enchantment)
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

    public static Map<org.bukkit.enchantments.Enchantment, Integer> getOutcome(Set<Enchantment> enchantments)
    {
        Map<org.bukkit.enchantments.Enchantment, Integer> enchantmentMap = new HashMap<org.bukkit.enchantments.Enchantment, Integer>();

        for (Enchantment enchantment : enchantments) {
            int level = enchantment.getLevel();
            if (level > 0) {
                enchantmentMap.put(enchantment.getEnchantment(), Integer.valueOf(level));
            }
        }

        return enchantmentMap;
    }
}
