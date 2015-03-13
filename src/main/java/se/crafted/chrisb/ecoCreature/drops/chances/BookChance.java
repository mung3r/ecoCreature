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
import org.bukkit.inventory.meta.BookMeta;

import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;

public class BookChance extends ItemChance
{
    public BookChance()
    {
        super(Material.WRITTEN_BOOK);
    }

    private String title;
    private String author;
    private List<String> pages;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public List<String> getPages()
    {
        return pages;
    }

    public void setPages(List<String> pages)
    {
        this.pages = pages;
    }

    @Override
    public ItemStack nextItemStack(boolean fixedAmount)
    {
        ItemStack itemStack = super.nextItemStack(fixedAmount);

        if (itemStack != null && itemStack.getItemMeta() instanceof BookMeta) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            bookMeta.setTitle(title);
            bookMeta.setAuthor(author);
            bookMeta.setPages(pages);
            itemStack.setItemMeta(bookMeta);
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

                    if (material != null && material == Material.WRITTEN_BOOK) {
                        BookChance chance = new BookChance();
                        chance.setTitle(DefaultMessage.convertMessage(itemConfig.getString("title")));
                        chance.setAuthor(DefaultMessage.convertMessage(itemConfig.getString("author")));
                        chance.setPages(DefaultMessage.convertMessages(itemConfig.getStringList("pages")));
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