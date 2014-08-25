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
package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import se.crafted.chrisb.ecoCreature.PluginConfig;
import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public abstract class AbstractEventHandler implements RewardEventCreator
{
    protected static final Collection<RewardEvent> EMPTY_COLLECTION = Collections.emptyList();

    private final ecoCreature plugin;

    public AbstractEventHandler(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public abstract boolean isRewardSource(Event event);

    @Override
    public abstract Collection<RewardEvent> createRewardEvents(Event event);

    @Override
    public WorldSettings getSettings(World world)
    {
        return getWorldSettings(world);
    }

    protected PluginConfig getPluginConfig()
    {
        return plugin.getPluginConfig();
    }

    protected WorldSettings getWorldSettings(World world)
    {
        return getPluginConfig().getWorldSettings(world);
    }

    protected static void addPlayerSkullToEvent(Reward reward, Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            PlayerKilledEvent playerKilledEvent = (PlayerKilledEvent) event;

            if (reward != null && !reward.getItemDrops().isEmpty()) {
                Collection<ItemStack> itemDrops = new ArrayList<ItemStack>();

                for (ItemStack itemStack : reward.getItemDrops()) {
                    if (itemStack.getType() == Material.SKULL_ITEM) {
                        playerKilledEvent.getDrops().add(createSkullItem(playerKilledEvent.getVictim().getName()));
                    }
                    else {
                        itemDrops.add(itemStack);
                    }
                }

                reward.setItemDrops(itemDrops);
            }
        }
    }

    private static ItemStack createSkullItem(String playerName)
    {
        ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwner(playerName);
        skullItem.setItemMeta(skullMeta);
        return skullItem;
    }
}
