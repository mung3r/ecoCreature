/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature.rewards.models;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public abstract class AbstractItemDrop
{
    private Material material;
    private Byte data;
    private Short durability;
    private NumberRange range;
    private double percentage;
    private Set<ItemEnchantment> enchantments;
    private final Random random = new Random();

    public AbstractItemDrop(Material material)
    {
        this.material = material;
        this.enchantments = Collections.emptySet();
    }

    public Material getMaterial()
    {
        return material;
    }

    public void setMaterial(Material item)
    {
        this.material = item;
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

    public NumberRange getRange()
    {
        return range;
    }

    public void setRange(NumberRange range)
    {
        this.range = range;
    }

    public double getPercentage()
    {
        return percentage;
    }

    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
    }

    public Set<ItemEnchantment> getEnchantments()
    {
        return enchantments;
    }

    public void setEnchantments(Set<ItemEnchantment> enchantments)
    {
        this.enchantments = enchantments;
    }

    public ItemStack getOutcome(boolean isFixedDrops)
    {
        if (random.nextDouble() * 100.0D < percentage && material != null) {
            int dropAmount = isFixedDrops ? range.getMaximumInteger() : range.getMinimumInteger()
                    + random.nextInt(Math.abs(range.getMaximumInteger() - range.getMinimumInteger() + 1));

            if (dropAmount > 0) {
                ItemStack itemStack;
                if (data == null) {
                    itemStack = new ItemStack(material, dropAmount);
                }
                else {
                    MaterialData materialData = new MaterialData(material, data);
                    itemStack = materialData.toItemStack(dropAmount);
                    if (durability != null) {
                        itemStack.setDurability(durability);
                    }
                }
                itemStack.addEnchantments(ItemEnchantment.getOutcome(enchantments));
                if (itemStack.getAmount() > 0) {
                    return itemStack;
                }
            }
        }
        return null;
    }
}
