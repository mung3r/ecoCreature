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
import java.util.Collection;
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

public class LoreChance extends ItemChance
{
    public LoreChance(Material material)
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
    public ItemStack nextItemStack(boolean fixedAmount, double lootBonus)
    {
        ItemStack itemStack = super.nextItemStack(fixedAmount, lootBonus);

        if (itemStack != null && itemStack.getItemMeta() != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(displayName);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    public static Collection<AbstractChance> parseConfig(ConfigurationSection config)
    {
        Collection<AbstractChance> chances = Collections.emptyList();

        if (config != null && config.getList("Drops") != null) {
            chances = new ArrayList<>();

            for (Object obj : config.getList("Drops")) {
                if (obj instanceof LinkedHashMap) {
                    ConfigurationSection itemConfig = createItemConfig(obj);
                    Material material = parseMaterial(itemConfig.getString("item"));

                    if (material != null && material != Material.WRITTEN_BOOK) {
                        LoreChance chance = new LoreChance(material);
                        chance.setDisplayName(DefaultMessage.convertTemplate(itemConfig.getString("displayname")));
                        chance.setLore(DefaultMessage.convertTemplates(itemConfig.getStringList("lore")));
                        populateItemChance(chance, itemConfig.getString("item"));

                        chances.add(chance);
                    }
                }
            }
        }

        return chances;
    }

    @SuppressWarnings("unchecked")
    private static ConfigurationSection createItemConfig(Object obj)
    {
        MemoryConfiguration itemConfig = new MemoryConfiguration();
        itemConfig.addDefaults((Map<String, Object>) obj);
        return itemConfig;
    }
}
