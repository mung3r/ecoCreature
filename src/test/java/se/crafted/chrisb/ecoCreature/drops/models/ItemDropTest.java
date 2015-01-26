/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2014, R. Ramos <http://github.com/mung3r/>
 * ecoCreature is licensed under the GNU Lesser General Public License.
 *
 * ecoCreature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ecoCreature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
