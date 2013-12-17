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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;

public class LoreDrop extends ItemDrop
{
    public LoreDrop(Material material)
    {
        super(material);
    }

    private String displayName;
    private List<String> lore;

    public String getTitle()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public List<String> getLore()
    {
        return lore;
    }

    public void setLore(List<String> lore)
    {
        this.lore = lore;
    }

    @Override
    public ItemStack getOutcome(boolean isFixedDrops)
    {
        ItemStack itemStack = super.getOutcome(isFixedDrops);

        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(displayName);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    public static List<AbstractItemDrop> parseConfig(ConfigurationSection config)
    {
        List<AbstractItemDrop> drops = Collections.emptyList();

        if (config != null && config.getList("Drops") != null) {
            drops = new ArrayList<AbstractItemDrop>();

            for (Object obj : config.getList("Drops")) {
                if (obj instanceof LinkedHashMap) {
                    ConfigurationSection itemConfig = createItemConfig(obj);
                    Material material = parseMaterial(itemConfig.getString("item"));

                    if (material != Material.WRITTEN_BOOK) {
                        LoreDrop drop = new LoreDrop(material);
                        drop.setDisplayName(DefaultMessage.convertMessage(itemConfig.getString("displayname")));
                        drop.setLore(DefaultMessage.convertMessages(itemConfig.getStringList("lore")));
                        populateItemDrop(drop, itemConfig.getString("item"));

                        drops.add(drop);
                    }
                }
            }
        }

        return drops;
    }

    @SuppressWarnings("unchecked")
    private static ConfigurationSection createItemConfig(Object obj)
    {
        MemoryConfiguration itemConfig = new MemoryConfiguration();
        itemConfig.addDefaults((Map<String, Object>) obj);
        return itemConfig;
    }
}
