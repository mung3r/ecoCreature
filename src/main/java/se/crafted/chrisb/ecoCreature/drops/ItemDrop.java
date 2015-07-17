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
package se.crafted.chrisb.ecoCreature.drops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;

public class ItemDrop extends AbstractDrop
{
    private boolean addToInventory;

    private Collection<ItemStack> items;

    public ItemDrop(String name, Location location)
    {
        super(name, location);

        items = new ArrayList<>();
    }

    public boolean isAddToInventory()
    {
        return addToInventory;
    }

    public void setAddToInventory(boolean addToInventory)
    {
        this.addToInventory = addToInventory;
    }

    public Collection<ItemStack> getItems()
    {
        return items;
    }

    public void setItems(Collection<ItemStack> items)
    {
        this.items = items;
    }

    @Override
    public boolean deliver(Player player)
    {
        boolean success = false;

        if (player != null) {
            addParameter(MessageToken.PLAYER, player.getName());
        }

        for (ItemStack stack : getItems()) {
            if (stack.getAmount() < 1) {
                continue;
            }

            ItemMeta itemMeta = stack.getItemMeta();
            if (itemMeta.hasDisplayName()) {
                String displayName = new DefaultMessage(itemMeta.getDisplayName()).assembleMessage(getParameters());
                itemMeta.setDisplayName(displayName);
            }

            if (itemMeta.hasLore()) {
                List<String> lore = new ArrayList<>();
                for (String loreLine : itemMeta.getLore()) {
                    lore.add(new DefaultMessage(loreLine).assembleMessage(getParameters()));
                }
                itemMeta.setLore(lore);
            }
            stack.setItemMeta(itemMeta);

            if (isAddToInventory() && player != null) {
                Map<Integer, ItemStack> leftOver = player.getInventory().addItem(stack);
                for (Map.Entry<Integer, ItemStack> entry : leftOver.entrySet()) {
                    getWorld().dropItemNaturally(getLocation(), entry.getValue());
                }
            } else {
                getWorld().dropItemNaturally(getLocation(), stack);
            }
            success = true;
        }

        return success;
    }
}
