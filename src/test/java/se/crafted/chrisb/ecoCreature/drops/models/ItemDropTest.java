package se.crafted.chrisb.ecoCreature.drops.models;

import junit.framework.Assert;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class ItemDropTest
{

    @Test
    public void testItemDrop()
    {
        Number samples = 1000000;
        ItemDrop drop = new ItemDrop(Material.GHAST_TEAR);
        drop.setRange(new NumberRange(1, 1));
        drop.setPercentage(20);
        int amount = 0;

        for (int i = 0; i < samples.intValue(); i++) {
            ItemStack stack = drop.nextItemStack(false);
            if (Material.AIR.equals(stack.getData().getItemType())) {
                continue;
            }
            amount += stack.getAmount();
        }

        double chance = amount / samples.doubleValue();
        double delta = Math.abs(drop.getPercentage() / 100D - chance);
        Assert.assertTrue(delta < 0.01);
    }
}
