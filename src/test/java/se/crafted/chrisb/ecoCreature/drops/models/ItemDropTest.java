package se.crafted.chrisb.ecoCreature.drops.models;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class ItemDropTest
{
    Number sampleSize = 1000000;

    @Test
    public void testItemDrop()
    {
        ItemDrop drop = new ItemDrop(Material.GHAST_TEAR);
        drop.setRange(new NumberRange(1, 3));
        drop.setPercentage(20);
        int amount = 0;

        for (int i = 0; i < sampleSize.intValue(); i++) {
            ItemStack stack = drop.nextItemStack(false);
            if (Material.AIR.equals(stack.getData().getItemType())) {
                continue;
            }
            amount += stack.getAmount();
        }

        double chance = amount / sampleSize.doubleValue();
        System.out.println(String.format("change = %.2f", chance));
    }
}
