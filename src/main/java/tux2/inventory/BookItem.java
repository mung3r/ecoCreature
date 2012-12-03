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
import java.util.Collections;
import java.util.List;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class BookItem
{
    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_PAGES = "pages";
    private static final NBTTagString BLANK_PAGE = new NBTTagString("1", "");

    private net.minecraft.server.ItemStack item = null;
    private CraftItemStack stack = null;

    public BookItem(ItemStack item)
    {
        stack = new CraftItemStack(item);
        this.item = stack.getHandle();
    }

    public BookItem(CraftItemStack item)
    {
        stack = (CraftItemStack) item;
        this.item = stack.getHandle();
    }

    public List<String> getPages()
    {
        List<String> pagestrings = Collections.emptyList();
        if (item.getTag() != null) {
            NBTTagList pages = item.getTag().getList(TAG_PAGES);
            pagestrings = new ArrayList<String>(pages.size());
            for (int i = 0; i < pages.size(); i++) {
                pagestrings.add(pages.get(i).toString());
            }
        }
        return pagestrings;
    }

    public String getAuthor()
    {
        String author = null;
        if (item.getTag() != null) {
            author = item.getTag().getString(TAG_AUTHOR);
        }
        return author;
    }

    public String getTitle()
    {
        String title = null;
        if (item.getTag() != null) {
            title = item.getTag().getString(TAG_TITLE);
        }
        return title;
    }

    public void setPages(List<String> newpages)
    {
        NBTTagList pages = new NBTTagList(TAG_PAGES);
        // we don't want to throw any errors if the book is blank!
        if (newpages.isEmpty()) {
            pages.add(BLANK_PAGE);
        }
        else {
            int pageNum = 0;
            for (String newpage : newpages) {
                pages.add(new NBTTagString(String.valueOf(pageNum), newpage));
                pageNum++;
            }
        }
        getItemTag().set(TAG_PAGES, pages);
    }

    public void addPages(List<String> newpages)
    {
        NBTTagCompound tag = getItemTag();
        NBTTagList pages;
        if (getPages() == null) {
            pages = new NBTTagList(TAG_PAGES);
        }
        else {
            pages = tag.getList(TAG_PAGES);
        }
        // we don't want to throw any errors if the book is blank!
        if (newpages.isEmpty() && pages.size() == 0) {
            pages.add(BLANK_PAGE);
        }
        else {
            for (String newpage : newpages) {
                pages.add(new NBTTagString(String.valueOf(pages.size()), newpage));
            }
        }
        tag.set(TAG_PAGES, pages);
    }

    public void setAuthor(String author)
    {
        if (author != null && !author.isEmpty()) {
            getItemTag().setString(TAG_AUTHOR, author);
        }
    }

    public void setTitle(String title)
    {
        if (title != null && !title.isEmpty()) {
            getItemTag().setString(TAG_TITLE, title);
        }
    }

    public ItemStack getItemStack()
    {
        return stack;
    }

    private NBTTagCompound getItemTag()
    {
        if (item.getTag() == null) {
            item.setTag(new NBTTagCompound());
        }
        return item.getTag();
    }
}