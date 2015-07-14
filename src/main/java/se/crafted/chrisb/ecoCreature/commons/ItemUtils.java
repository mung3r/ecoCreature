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
package se.crafted.chrisb.ecoCreature.commons;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R3.NBTTagCompound;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

public class ItemUtils
{

    public static org.bukkit.inventory.ItemStack setUnbreakable(org.bukkit.inventory.ItemStack item)
    {
        if (!(item instanceof CraftItemStack)) {
            item = CraftItemStack.asCraftCopy(item);
        }
        NBTTagCompound tag = getTag(item);
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        tag.setByte("Unbreakable", (byte) 1);
        return setTag(item, tag);
    }

    public static org.bukkit.inventory.ItemStack setHideFlags(org.bukkit.inventory.ItemStack item)
    {
        if (!(item instanceof CraftItemStack)) {
            item = CraftItemStack.asCraftCopy(item);
        }
        NBTTagCompound tag = getTag(item);
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        tag.setInt("HideFlags", 63);
        return setTag(item, tag);
    }

    private static NBTTagCompound getTag(org.bukkit.inventory.ItemStack item)
    {
        if ((item instanceof CraftItemStack)) {
            try {
                Field field = CraftItemStack.class.getDeclaredField("handle");
                field.setAccessible(true);
                return ((net.minecraft.server.v1_8_R3.ItemStack) field.get(item)).getTag();
            }
            catch (Exception ignored) {
            }
        }
        return null;
    }

    private static org.bukkit.inventory.ItemStack setTag(org.bukkit.inventory.ItemStack item, NBTTagCompound tag)
    {
        CraftItemStack craftItem = (item instanceof CraftItemStack) ? (CraftItemStack) item : CraftItemStack.asCraftCopy(item);

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = null;
        try {
            Field field = CraftItemStack.class.getDeclaredField("handle");
            field.setAccessible(true);
            nmsItem = (net.minecraft.server.v1_8_R3.ItemStack) field.get(item);
        }
        catch (Exception ignored) {
        }

        if (nmsItem == null) {
            nmsItem = CraftItemStack.asNMSCopy(craftItem);
        }

        nmsItem.setTag(tag);
        try {
            Field field = CraftItemStack.class.getDeclaredField("handle");
            field.setAccessible(true);
            field.set(craftItem, nmsItem);
        }
        catch (Exception ignored) {
        }

        return craftItem;
    }

}
