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
package se.crafted.chrisb.ecoCreature.events.listeners;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.drops.Drop;
import se.crafted.chrisb.ecoCreature.events.DropEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;

public class DropEventListener implements Listener
{
    private static final String PARTY_REWARD_MESSAGE = "&7Party awarded &6<amt>&7.";
    private static final String PARTY_PENALTY_MESSAGE = "&Party penalized &6<amt>&7.";

    private ecoCreature plugin;

    public DropEventListener(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDropEvent(DropEvent event)
    {
        if (!event.isCancelled()) {
            Collection<Drop> drops = event.getDrops();
            Player player = event.getPlayer();

            for (Drop drop : drops) {
                if (player != null) { // TODO: fix this upstream for citizens2
                    dropCoin(player, drop);
                    dropItems(player, drop);
                }
                dropEntities(drop);
                dropJockeys(drop);
    
                plugin.getMetrics().addCount(drop.getName());
            }
        }
    }

    private void dropCoin(Player player, Drop drop)
    {
        if (!DependencyUtils.hasEconomy()) {
            return;
        }

        double amount = calculateAmount(drop);

        if (Math.abs(amount) > 0.0) {

            for (String member : createParty(player.getName(), drop)) {
                registerAmount(member, amount);

                Message message = member.equals(player.getName()) ? drop.getMessage() : getPartyMessage(amount);
                drop.addParameter(MessageToken.PLAYER, member).addParameter(MessageToken.AMOUNT, DependencyUtils.getEconomy().format(Math.abs(amount)));

                MessageHandler handler = new MessageHandler(message, drop.getParameters());
                handler.send(member);
            }
        }
    }

    private Collection<String> createParty(String player, Drop drop)
    {
        Collection<String> party = new ArrayList<String>();
        party.add(player);
        party.addAll(drop.getParty());
        return party;
    }

    private double calculateAmount(Drop drop)
    {
        LoggerUtil.getInstance().debug("===== START: coin calculation for " + drop.getName());
        LoggerUtil.getInstance().debug("Initial amount: " + drop.getCoin());
        LoggerUtil.getInstance().debug("Gain: " + drop.getGain());
        double amount = drop.getCoin() * drop.getGain();
        LoggerUtil.getInstance().debug("Initial amount * gain: " + amount);

        if (drop.getParty().size() > 1) {
            LoggerUtil.getInstance().debug("Party size: " + drop.getParty().size());
            amount /= drop.getParty().size();
            LoggerUtil.getInstance().debug("Party amount: " + amount);
        }

        if (drop.isIntegerCurrency()) {
            amount = round(amount, 0, BigDecimal.ROUND_HALF_UP);
            LoggerUtil.getInstance().debug("Rounded integer amount: " + amount);
        }
        else {
            amount = round(amount, 2, BigDecimal.ROUND_HALF_UP);
            LoggerUtil.getInstance().debug("Rounded decimal amount: " + amount);
        }
        LoggerUtil.getInstance().debug("===== END: amount is " + amount);
        return amount;
    }

    public static double round(double unrounded, int precision, int roundingMode)
    {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    private void registerAmount(String member, double amount)
    {
        if (!DependencyUtils.hasEconomy()) {
            return;
        }

        if (amount > 0.0) {
            DependencyUtils.getEconomy().depositPlayer(member, amount);
        }
        else if (amount < 0.0) {
            DependencyUtils.getEconomy().withdrawPlayer(member, Math.abs(amount));
        }
    }

    private Message getPartyMessage(double amount)
    {
        Message message = new DefaultMessage("");

        if (amount > 0.0) {
            message = new DefaultMessage(PARTY_REWARD_MESSAGE);
        }
        else if (amount < 0.0) {
            message = new DefaultMessage(PARTY_PENALTY_MESSAGE);
        }

        return message;
    }

    private void dropItems(Player player, Drop drop)
    {
        drop.addParameter(MessageToken.PLAYER, player.getName());

        for (ItemStack stack : drop.getItemDrops()) {
            ItemMeta itemMeta = stack.getItemMeta();
            if (itemMeta.hasDisplayName()) {
                String displayName = getAssembledMessage(itemMeta.getDisplayName(), drop);
                itemMeta.setDisplayName(displayName);
            }

            if (itemMeta.hasLore()) {
                List<String> lore = new ArrayList<String>();
                for (String loreLine : itemMeta.getLore()) {
                    lore.add(getAssembledMessage(loreLine, drop));
                }
                itemMeta.setLore(lore);
            }
            stack.setItemMeta(itemMeta);

            if (drop.isAddItemsToInventory()) {
                Map<Integer, ItemStack> leftOver = player.getInventory().addItem(stack);
                for (Map.Entry<Integer, ItemStack> entry : leftOver.entrySet()) {
                    drop.getWorld().dropItemNaturally(drop.getLocation(), entry.getValue());
                }
            } else {
                drop.getWorld().dropItemNaturally(drop.getLocation(), stack);
            }
        }
    }

    private String getAssembledMessage(String template, Drop drop)
    {
        Message message = new DefaultMessage(template);
        return message.getAssembledMessage(drop.getParameters());
    }

    private void dropEntities(Drop drop)
    {
        for (EntityType type : drop.getEntityDrops()) {
            Entity entity = drop.getWorld().spawn(drop.getLocation(), type.getEntityClass());
            if (entity instanceof ExperienceOrb) {
                ((ExperienceOrb) entity).setExperience(1);
            }
        }
    }

    private void dropJockeys(Drop drop)
    {
        Iterator<EntityType> typeIterator = drop.getJockeyDrops().iterator();
        while (typeIterator.hasNext()) {
            EntityType vehicleType = typeIterator.next();
            Entity vehicle = drop.getWorld().spawn(drop.getLocation(), vehicleType.getEntityClass());
            EntityType passengerType = typeIterator.next();
            Entity passenger = drop.getWorld().spawn(drop.getLocation(), passengerType.getEntityClass());
            vehicle.setPassenger(passenger);
        }
    }
}
