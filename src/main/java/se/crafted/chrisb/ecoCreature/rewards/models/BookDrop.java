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

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.commons.BookItem;

public class BookDrop extends ItemDrop
{
    public BookDrop(Material material)
    {
        super(material);
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
    public ItemStack getOutcome(boolean isFixedDrops)
    {
        ItemStack itemStack = super.getOutcome(isFixedDrops);

        if (itemStack != null) {
            BookItem book = new BookItem(itemStack);
            book.setTitle(title);
            book.setAuthor(author);
            book.setPages(pages.toArray(new String[pages.size()]));
            return book.getItemStack();
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
                    LinkedHashMap<?, ?> itemConfig = (LinkedHashMap<?, ?>) obj;

                    String dropString = (String) itemConfig.get("item");
                    if (parseMaterial(dropString).equals(Material.WRITTEN_BOOK)) {
                        BookDrop drop = new BookDrop(parseMaterial(dropString));
                        drop.setTitle((String) itemConfig.get("title"));
                        drop.setAuthor((String) itemConfig.get("author"));
                        drop.setPages((List<String>) itemConfig.get("pages"));
                        populateItemDrop(drop, dropString);
                        drops.add(drop);
                    }
                }
            }
        }

        return drops;
    }
}
