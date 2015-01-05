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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ItemDrop extends AbstractDrop
{
    private final Material material;
    private Byte data;
    private Short durability;
    private Collection<ItemEnchantment> enchantments;
    private boolean addItemsToInventory;

    public ItemDrop(Material material)
    {
        this.material = material;
        this.enchantments = Collections.emptyList();
    }

    public Material getMaterial()
    {
        return material;
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

    public Collection<ItemEnchantment> getEnchantments()
    {
        return enchantments;
    }

    public void setEnchantments(Collection<ItemEnchantment> enchantments)
    {
        this.enchantments = enchantments;
    }

    public boolean isAddItemsToInventory()
    {
        return addItemsToInventory;
    }

    public void setAddItemsToInventory(boolean addItemsToInventory)
    {
        this.addItemsToInventory = addItemsToInventory;
    }

    public ItemStack getOutcome(boolean isFixedDrops)
    {
        if (getRandom().nextDouble() * 100.0D < getPercentage() && material != null) {
            int dropAmount = isFixedDrops ? getRange().getMaximumInteger() : getRange().getMinimumInteger()
                    + getRandom().nextInt(Math.abs(getRange().getMaximumInteger() - getRange().getMinimumInteger() + 1));

            if (dropAmount > 0) {
                ItemStack itemStack;
                if (data == null && durability == null) {
                    itemStack = new ItemStack(material, dropAmount);
                }
                else {
                    MaterialData materialData = data == null ? new MaterialData(material) : new MaterialData(material, data);
                    itemStack = materialData.toItemStack(dropAmount);
                    if (durability != null) {
                        itemStack.setDurability(durability);
                    }
                }
                itemStack.addUnsafeEnchantments(ItemEnchantment.getOutcome(enchantments));
                if (itemStack.getAmount() > 0) {
                    return itemStack;
                }
            }
        }
        return null;
    }

    public static Collection<ItemDrop> parseConfig(ConfigurationSection config)
    {
        Collection<ItemDrop> drops = Collections.emptyList();

        if (config != null) {
            if (config.getList("Drops") != null) {
                Collection<String> dropsList = config.getStringList("Drops");
                drops = parseDrops(dropsList);
            }
            else {
                drops = parseDrops(config.getString("Drops"));
            }
        }

        return drops;
    }

    private static Collection<ItemDrop> parseDrops(String dropsString)
    {
        Collection<ItemDrop> drops = Collections.emptyList();

        if (dropsString != null && !dropsString.isEmpty()) {
            drops = parseDrops(Arrays.asList(dropsString.split(";")));
        }

        return drops;
    }

    private static Collection<ItemDrop> parseDrops(Collection<String> dropsList)
    {
        Collection<ItemDrop> drops = Collections.emptyList();

        if (dropsList != null && !dropsList.isEmpty()) {
            drops = new ArrayList<>();

            for (String dropString : dropsList) {
                drops.addAll(parseItem(dropString));
            }
        }

        return drops;
    }

    private static Collection<ItemDrop> parseItem(String dropString)
    {
        Collection<ItemDrop> drops = Collections.emptyList();

        if (parseMaterial(dropString) != null) {
            drops = new ArrayList<>();
            drops.add(populateItemDrop(new ItemDrop(parseMaterial(dropString)), dropString));
        }

        return drops;
    }

    protected static ItemDrop populateItemDrop(ItemDrop drop, String dropString)
    {
        drop.setEnchantments(parseEnchantments(dropString));
        drop.setData(parseData(dropString));
        drop.setDurability(parseDurability(dropString));
        drop.setRange(parseRange(dropString));
        drop.setPercentage(parsePercentage(dropString));

        return drop;
    }

    protected static Material parseMaterial(String dropString)
    {
        Material material = null;
        
        if (dropString != null) {
            String[] dropParts = dropString.split(":");
            String[] itemParts = dropParts[0].split(",");
            String[] itemSubParts = itemParts[0].split("\\.");
    
            material = Material.matchMaterial(itemSubParts[0]);
        }

        return material;
    }

    private static Collection<ItemEnchantment> parseEnchantments(String dropString)
    {
        Collection<ItemEnchantment> enchantments = Collections.emptyList();
        String[] dropParts = dropString.split(":");
        String[] itemParts = dropParts[0].split(",");

        // check for enchantment
        if (itemParts.length > 1) {
            enchantments = new ArrayList<>();

            for (int i = 1; i < itemParts.length; i++) {
                String[] enchantParts = itemParts[i].split("\\.");
                // check enchantment level and range
                if (enchantParts.length > 1) {
                    String[] levelRange = enchantParts[1].split("-");
                    int minLevel = Integer.parseInt(levelRange[0]);
                    int maxLevel = levelRange.length > 1 ? Integer.parseInt(levelRange[1]) : minLevel;
                    enchantments.add(createEnchantment(enchantParts[0], minLevel, maxLevel));
                }
                else {
                    enchantments.add(createEnchantment(enchantParts[0], 1, 1));
                }
            }
        }

        return enchantments;
    }

    private static ItemEnchantment createEnchantment(String name, int minLevel, int maxLevel)
    {
        if (name == null || Enchantment.getByName(name.toUpperCase()) == null) {
            throw new IllegalArgumentException("Unrecognized enchantment: " + name);
        }
        ItemEnchantment enchantment = new ItemEnchantment(Enchantment.getByName(name.toUpperCase()));
        enchantment.setLevelRange(new NumberRange(minLevel, maxLevel));

        return enchantment;
    }

    private static Byte parseData(String dropString)
    {
        String[] dropParts = dropString.split(":");
        String[] itemParts = dropParts[0].split(",");
        String[] itemSubParts = itemParts[0].split("\\.");

        return itemSubParts.length > 1 && !itemSubParts[1].isEmpty() ? Byte.parseByte(itemSubParts[1]) : null;
    }

    private static Short parseDurability(String dropString)
    {
        String[] dropParts = dropString.split(":");
        String[] itemParts = dropParts[0].split(",");
        String[] itemSubParts = itemParts[0].split("\\.");

        return itemSubParts.length > 2 && !itemSubParts[2].isEmpty() ? Short.parseShort(itemSubParts[2]) : null;
    }

    private static NumberRange parseRange(String dropString)
    {
        String[] dropParts = dropString.split(":");
        String[] amountRange = dropParts[1].split("-");

        int min = 0;
        int max;

        if (amountRange.length == 2) {
            min = Integer.parseInt(amountRange[0]);
            max = Integer.parseInt(amountRange[1]);
        }
        else {
            max = Integer.parseInt(dropParts[1]);
        }

        return new NumberRange(min, max);
    }

    private static double parsePercentage(String dropString)
    {
        String[] dropParts = dropString.split(":");

        return Double.parseDouble(dropParts[2]);
    }
}
