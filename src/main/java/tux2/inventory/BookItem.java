/*
 * Copyright (C) 2012  Joshua Reetz

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tux2.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.apache.commons.lang.StringUtils;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class BookItem
{
    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_PAGES = "pages";
    private static final NBTTagString BLANK_PAGE = new NBTTagString("1", "");

    private final CraftItemStack stack;

    public BookItem(ItemStack item)
    {
        this(new CraftItemStack(item));
    }

    public BookItem(CraftItemStack item)
    {
        stack = item;
    }

    public List<String> getPages()
    {
        NBTTagList pages = getItemTag().getList(TAG_PAGES);
        List<String> pageList = new ArrayList<String>(pages.size());
        for (int i = 0; i < pages.size(); i++) {
            pageList.add(pages.get(i).toString());
        }
        return pageList;
    }

    public String getAuthor()
    {
        return getItemTag().getString(TAG_AUTHOR);
    }

    public String getTitle()
    {
        return getItemTag().getString(TAG_TITLE);
    }

    public void setPages(List<String> pages)
    {
        getItemTag().set(TAG_PAGES, new NBTTagList(TAG_PAGES));
        addPages(pages);
    }

    public void addPages(List<String> pages)
    {
        NBTTagList newPages = getItemTag().getList(TAG_PAGES);
        if (newPages.size() == 0) {
            newPages = new NBTTagList(TAG_PAGES);
            getItemTag().set(TAG_PAGES, newPages);
        }
        // we don't want to throw any errors if the book is blank!
        if (pages == null || pages.isEmpty()) {
            newPages.add(BLANK_PAGE);
        }
        else {
            int pageNum = newPages.size();
            for (String page : pages) {
                newPages.add(new NBTTagString(String.valueOf(pageNum), page));
                pageNum++;
            }
        }
    }

    public void setAuthor(String author)
    {
        if (StringUtils.isNotEmpty(author)) {
            getItemTag().setString(TAG_AUTHOR, author);
        }
    }

    public void setTitle(String title)
    {
        if (StringUtils.isNotEmpty(title)) {
            getItemTag().setString(TAG_TITLE, title);
        }
    }

    public ItemStack getItemStack()
    {
        return stack;
    }

    private NBTTagCompound getItemTag()
    {
        NBTTagCompound tag = stack.getHandle().getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.getHandle().setTag(tag);
        }
        return tag;
    }
}