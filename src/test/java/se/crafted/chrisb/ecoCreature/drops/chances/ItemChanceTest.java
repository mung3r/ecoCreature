/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature.drops.chances;

import junit.framework.Assert;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class ItemChanceTest
{

    @Test
    public void testItemDrop()
    {
        Number samples = 1000000;
        ItemChance chance = new ItemChance(Material.GHAST_TEAR);
        chance.setRange(new NumberRange(1));
        chance.setPercentage(20);
        chance.setFixedAmount(false);
        int amount = 0;

        for (int i = 0; i < samples.intValue(); i++) {
            ItemStack stack = chance.nextItemStack(0);
            if (stack.getAmount() < 1) {
                continue;
            }
            amount += stack.getAmount();
        }

        double idealChance = amount / samples.doubleValue();
        double delta = Math.abs(chance.getChance() - idealChance);
        Assert.assertTrue(delta < 0.01);
    }
}
