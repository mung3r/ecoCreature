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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nl.arfie.bukkit.attributes.Attribute;
import nl.arfie.bukkit.attributes.AttributeType;
import nl.arfie.bukkit.attributes.Attributes;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberRange;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.ItemDrop;

public class ItemChance extends AbstractChance implements DropChance
{
    private final Material material;
    private Byte data;
    private Short durability;
    private Collection<EnchantmentChance> enchantments;
    private List<Attribute> attributes;
    private boolean addToInventory;
    private boolean fixedAmount;

    public ItemChance(Material material)
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

    public Collection<EnchantmentChance> getEnchantments()
    {
        return enchantments;
    }

    public void setEnchantments(Collection<EnchantmentChance> enchantments)
    {
        this.enchantments = enchantments;
    }

    public List<Attribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes)
    {
        this.attributes = attributes;
    }

    public boolean isAddToInventory()
    {
        return addToInventory;
    }

    public void setAddToInventory(boolean addToInventory)
    {
        this.addToInventory = addToInventory;
    }

    public Boolean isFixedAmount()
    {
        return fixedAmount;
    }

    public void setFixedAmount(Boolean fixedAmount)
    {
        this.fixedAmount = fixedAmount;
    }

    public ItemStack nextItemStack(int lootLevel)
    {
        if (material != null) {
            int dropAmount = fixedAmount ? nextFixedAmount() : nextIntAmount(lootLevel);

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
                itemStack.addUnsafeEnchantments(EnchantmentChance.nextEnchantments(enchantments));
                Attributes.apply(itemStack, attributes, false);

                if (itemStack.getAmount() > 0) {
                    return itemStack;
                }
            }
        }
        return new ItemStack(Material.AIR, 0);
    }

    @Override
    public AbstractDrop nextDrop(String name, Location location, int lootLevel)
    {
        ItemDrop drop = new ItemDrop(name, location);
        drop.getItems().add(nextItemStack(lootLevel));
        drop.setAddToInventory(addToInventory);
        return drop;
    }

    public static Collection<ItemChance> parseConfig(String section, ConfigurationSection config)
    {
        ConfigurationSection dropConfig = config.getConfigurationSection(section);
        Collection<ItemChance> chances = Collections.emptyList();

        if (dropConfig != null) {
            chances = new ArrayList<>();

            if (dropConfig.getList("Drops") != null) {
                Collection<String> dropsList = dropConfig.getStringList("Drops");
                chances.addAll(parseChances(dropsList));
            }
            else {
                chances.addAll(parseChance(dropConfig.getString("Drops")));
            }
        }

        for (ItemChance chance : chances) {
            chance.setFixedAmount(config.getBoolean("System.Hunting.FixedDrops"));
            chance.setAddToInventory(dropConfig.getBoolean("AddItemsToInventory"));
        }

        return chances;
    }

    private static Collection<ItemChance> parseChance(String dropsString)
    {
        Collection<ItemChance> chances = Collections.emptyList();

        if (dropsString != null && !dropsString.isEmpty()) {
            chances = parseChances(Arrays.asList(dropsString.split(";")));
        }

        return chances;
    }

    private static Collection<ItemChance> parseChances(Collection<String> dropsList)
    {
        Collection<ItemChance> chances = Collections.emptyList();

        if (dropsList != null && !dropsList.isEmpty()) {
            chances = new ArrayList<>();

            for (String dropString : dropsList) {
                chances.addAll(parseItemChance(dropString));
            }
        }

        return chances;
    }

    private static Collection<ItemChance> parseItemChance(String dropString)
    {
        Collection<ItemChance> chances = Collections.emptyList();

        if (parseMaterial(dropString) != null) {
            chances = new ArrayList<>();
            chances.add(populateItemChance(new ItemChance(parseMaterial(dropString)), dropString));
        }

        return chances;
    }

    protected static ItemChance populateItemChance(ItemChance chance, String dropString)
    {
        chance.setEnchantments(parseEnchantments(dropString));
        chance.setData(parseData(dropString));
        chance.setDurability(parseDurability(dropString));
        chance.setRange(parseRange(dropString));
        chance.setPercentage(parsePercentage(dropString));

        return chance;
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

    private static Collection<EnchantmentChance> parseEnchantments(String dropString)
    {
        Collection<EnchantmentChance> enchantments = Collections.emptyList();
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

    private static EnchantmentChance createEnchantment(String name, int minLevel, int maxLevel)
    {
        if (name == null || Enchantment.getByName(name.toUpperCase()) == null) {
            throw new IllegalArgumentException("Unrecognized enchantment: " + name);
        }
        EnchantmentChance enchantment = new EnchantmentChance(Enchantment.getByName(name.toUpperCase()));
        enchantment.setRange(new NumberRange(minLevel, maxLevel));

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

    protected static List<Attribute> parseAttributes(List<String> attrStrings)
    {
        List<Attribute> attrList = new ArrayList<>();

        for (String attrString : attrStrings) {
            if (StringUtils.isNotEmpty(attrString)) {
                String[] attrParts = attrString.toUpperCase().split(":");
                AttributeType type = AttributeType.valueOf(attrParts[0]);
                if (type != null && NumberUtils.isNumber(attrParts[1])) {
                    attrList.add(new Attribute(type, Double.valueOf(attrParts[1])));
                }
            }
        }

        return attrList;
    }
}
