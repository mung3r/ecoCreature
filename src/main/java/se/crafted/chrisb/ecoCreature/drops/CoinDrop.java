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
package se.crafted.chrisb.ecoCreature.drops;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;

public class CoinDrop extends AbstractDrop
{
    private static final String PARTY_REWARD_MESSAGE = "&7Party awarded &6<amt>&7.";
    private static final String PARTY_PENALTY_MESSAGE = "&Party penalized &6<amt>&7.";

    private static final double IDENTITY = 1.0;
    private static final double ZERO = 0.0;

    private double gain;
    private Collection<UUID> party;
    private boolean integerCurrency;

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

    public double getCoin()
    {
        return coin;
    }

    public void setCoin(double coin)
    {
        this.coin = coin;
    }

    @Override
    public void deliver(Player player)
    {
        if (!DependencyUtils.hasEconomy() || player == null) {
            return;
        }

        double amount = calculateAmount();

        if (Math.abs(amount) > 0.0) {

            for (UUID memberId : createParty(player.getUniqueId())) {
                registerAmount(memberId, amount);

                Message message = memberId.equals(player.getName()) ? getMessage() : getPartyMessage(amount);
                addParameter(MessageToken.PLAYER, Bukkit.getOfflinePlayer(memberId).getName()).addParameter(MessageToken.AMOUNT, DependencyUtils.getEconomy().format(Math.abs(amount)));

                MessageHandler handler = new MessageHandler(message, getParameters());
                handler.send(memberId);
            }
        }
    }

    private Collection<UUID> createParty(UUID playerId)
    {
        Collection<UUID> party = new ArrayList<>();
        party.add(playerId);
        party.addAll(getParty());
        return party;
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
        BigDecimal bd = new BigDecimal(unrounded);
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
}
