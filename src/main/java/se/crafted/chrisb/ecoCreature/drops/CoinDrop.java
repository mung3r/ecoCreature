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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;

public class CoinDrop extends AbstractDrop
{
    private static final double IDENTITY = 1.0;
    private static final double ZERO = 0.0;

    private double gain;
    private Collection<UUID> party;
    private boolean integerCurrency;
    private Message partyMessage;

    private double coin;

    public CoinDrop(String name, Location location)
    {
        super(name, location);

        gain = IDENTITY;
        party = Collections.emptyList();
        integerCurrency = false;
        coin = ZERO;
    }

    public double getGain()
    {
        return gain;
    }

    public void setGain(double gain)
    {
        this.gain = gain;
    }

    public boolean hasParty()
    {
        return party.size() > 0;
    }

    public Collection<UUID> getParty()
    {
        return party;
    }

    public void setParty(Collection<UUID> party)
    {
        this.party = party;
    }

    public boolean isIntegerCurrency()
    {
        return integerCurrency;
    }

    public void setIntegerCurrency(boolean integerCurrency)
    {
        this.integerCurrency = integerCurrency;
    }

    public Message getPartyMessage()
    {
        return partyMessage;
    }

    public void setPartyMessage(Message partyMessage)
    {
        this.partyMessage = partyMessage;
    }

    public double getCoin()
    {
        return coin;
    }

    public void setCoin(double coin)
    {
        this.coin = coin;
    }

    @Override
    public boolean deliver(Player player)
    {
        if (!DependencyUtils.hasEconomy() || player == null) {
            return false;
        }

        double amount = calculateAmount();
        boolean success = false;

        if (Math.abs(amount) > 0.0) {

            for (UUID memberId : getMembers(player.getUniqueId())) {
                registerAmount(memberId, amount);
                success = true;

                Message message = memberId.equals(player.getUniqueId()) ? getMessage() : getPartyMessage();
                addParameter(MessageToken.PLAYER, Bukkit.getOfflinePlayer(memberId).getName()).addParameter(MessageToken.AMOUNT, DependencyUtils.getEconomy().format(Math.abs(amount)));

                MessageHandler handler = new MessageHandler(message, getParameters());
                handler.send(memberId);
            }
        }

        return success;
    }

    private Set<UUID> getMembers(UUID playerId)
    {
        Set<UUID> members = new HashSet<>();
        members.add(playerId);
        members.addAll(getParty());
        return members;
    }

    private double calculateAmount()
    {
        LoggerUtil.getInstance().debug("===== START: coin calculation for " + getName());
        LoggerUtil.getInstance().debug("Initial amount: " + getCoin());
        LoggerUtil.getInstance().debug("Gain: " + getGain());
        double amount = getCoin() * getGain();
        LoggerUtil.getInstance().debug("Initial amount * gain: " + amount);

        if (getParty().size() > 1) {
            LoggerUtil.getInstance().debug("Party size: " + getParty().size());
            amount /= getParty().size();
            LoggerUtil.getInstance().debug("Party amount: " + amount);
        }

        if (isIntegerCurrency()) {
            amount = round(amount, 0);
            LoggerUtil.getInstance().debug("Rounded integer amount: " + amount);
        }
        else {
            amount = round(amount, 2);
            LoggerUtil.getInstance().debug("Rounded decimal amount: " + amount);
        }
        LoggerUtil.getInstance().debug("===== END: amount is " + amount);
        return amount;
    }

    public static double round(double unrounded, int precision)
    {
        BigDecimal bd = BigDecimal.valueOf(unrounded);
        BigDecimal rounded = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
        return rounded.doubleValue();
    }

    private void registerAmount(UUID memberId, double amount)
    {
        if (!DependencyUtils.hasEconomy()) {
            return;
        }

        if (amount > 0.0) {
            DependencyUtils.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(memberId), amount);
        }
        else if (amount < 0.0) {
            DependencyUtils.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(memberId), Math.abs(amount));
        }
    }
}
