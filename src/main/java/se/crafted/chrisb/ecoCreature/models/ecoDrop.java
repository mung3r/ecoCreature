package se.crafted.chrisb.ecoCreature.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoDrop
{
    private Material item;
    private Byte data;
    private Short durability;
    private int maxAmount;
    private int minAmount;
    private double percentage;
    private boolean isFixedDrops;
    private Set<ecoEnchantment> enchantments;
    private final Random random = new Random();

    public ecoDrop()
    {
        this.enchantments = new HashSet<ecoEnchantment>();
    }

    private static class ecoEnchantment
    {
        private Enchantment enchantment;
        private int minLevel;
        private int maxLevel;
        private final Random random = new Random();

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
            return minLevel + random.nextInt(Math.abs(maxLevel - minLevel + 1));
        }

        public void setMinLevel(int minLevel)
        {
            this.minLevel = minLevel;
        }

        public void setMaxLevel(int maxLevel)
        {
            this.maxLevel = maxLevel;
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

    public Byte getData()
    {
        return data;
    }

    public void setData(Byte data)
    {
        this.data = data;
    }

    public Short getDurability()
    {
        return durability;
    }

    public void setDurability(Short durability)
    {
        this.durability = durability;
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

    public double getPercentage()
    {
        return percentage;
    }

    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
    }

    public boolean getIsFixedDrops()
    {
        return isFixedDrops;
    }

    public void setIsFixedDrops(boolean isFixedDrops)
    {
        this.isFixedDrops = isFixedDrops;
    }

    public void addEnchantment(Enchantment enchantment, int minLevel, int maxLevel)
    {
        if (enchantment == null) {
            throw new IllegalArgumentException();
        }
        ecoEnchantment e = new ecoEnchantment();
        e.setEnchantment(enchantment);
        e.setMinLevel(minLevel);
        e.setMaxLevel(maxLevel);
        enchantments.add(e);
    }

    public ItemStack computeItemStack()
    {
        if (Math.random() * 100.0D < percentage) {
            int dropAmount = isFixedDrops ? maxAmount : minAmount + random.nextInt(Math.abs(maxAmount - minAmount + 1));

            if (dropAmount > 0) {
                ItemStack itemStack;
                if (data == null) {
                    itemStack = new ItemStack(item, dropAmount);
                }
                else {
                    MaterialData materialData = new MaterialData(item, data);
                    itemStack = materialData.toItemStack(dropAmount);
                    if (durability != null) {
                        itemStack.setDurability(durability);
                    }
                }
                for (ecoEnchantment e : enchantments) {
                    int level = e.getLevel();
                    if (level > 0) {
                        itemStack.addEnchantment(e.getEnchantment(), level);
                    }
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
        return String.format("ecoDrop [item=%s, data=%s, durabilit=%s, maxAmount=%s, minAmount=%s, percentage=%s, isFixedDrops=%s, enchantments=%s, random=%s]",
                item, data, durability, maxAmount, minAmount, percentage, isFixedDrops, enchantments, random);
    }

    public static List<ecoDrop> parseDrops(String dropsConfig, boolean isFixedDrops)
    {
        List<String> drops = new ArrayList<String>();

        if (dropsConfig != null && !dropsConfig.isEmpty()) {
            drops.addAll(Arrays.asList(dropsConfig.split(";")));
        }

        return parseDrops(drops, isFixedDrops);
    }

    public static List<ecoDrop> parseDrops(List<String> dropsConfig, boolean isFixedDrops)
    {
        List<ecoDrop> drops = new ArrayList<ecoDrop>();

        if (dropsConfig != null && !dropsConfig.isEmpty()) {
            try {
                for (String dropString : dropsConfig) {
                    ecoDrop drop = new ecoDrop();
                    String[] dropParts = dropString.split(":");
                    String[] itemParts = dropParts[0].split(",");
                    // check for enchantment
                    if (itemParts.length > 1) {
                        for (int i = 1; i < itemParts.length; i++) {
                            String[] enchantParts = itemParts[i].split("\\.");
                            // check enchantment level and range
                            if (enchantParts.length > 1) {
                                String[] levelRange = enchantParts[1].split("-");
                                int minLevel = Integer.parseInt(levelRange[0]);
                                int maxLevel = levelRange.length > 1 ? Integer.parseInt(levelRange[1]) : minLevel;
                                drop.addEnchantment(Enchantment.getByName(enchantParts[0].toUpperCase()), minLevel, maxLevel);
                            }
                            else {
                                drop.addEnchantment(Enchantment.getByName(enchantParts[0].toUpperCase()), 1, 1);
                            }
                        }
                    }
                    // check for data id
                    String[] itemSubParts = itemParts[0].split("\\.");
                    drop.setItem(Material.matchMaterial(itemSubParts[0]));
                    if (drop.getItem() == null)
                        throw new Exception();
                    drop.setData(itemSubParts.length > 1 ? Byte.parseByte(itemSubParts[1]) : null);
                    drop.setDurability(itemSubParts.length > 2 ? Short.parseShort(itemSubParts[2]) : null);
                    // check for range on amount
                    String[] amountRange = dropParts[1].split("-");
                    if (amountRange.length == 2) {
                        drop.setMinAmount(Integer.parseInt(amountRange[0]));
                        drop.setMaxAmount(Integer.parseInt(amountRange[1]));
                    }
                    else {
                        drop.setMaxAmount(Integer.parseInt(dropParts[1]));
                    }
                    drop.setPercentage(Double.parseDouble(dropParts[2]));
                    drop.setIsFixedDrops(isFixedDrops);
                    drops.add(drop);
                }
            }
            catch (Exception exception) {
                ecoCreature.getEcoLogger().warning("Failed to parse drops: " + dropsConfig);
            }
        }

        return drops;
    }
}
