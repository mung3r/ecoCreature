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
package se.crafted.chrisb.ecoCreature.drops.sources;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.CoinDrop;
import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomDropType;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;

public class PVPDropSource extends AbstractDropSource
{
    private static final String PVP_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for murdering &5<crt>&7.";

    private boolean coinPercent;
    private double coinAmount;

    public PVPDropSource(ConfigurationSection config)
    {
        setName(CustomDropType.LEGACY_PVP.toString());
        coinPercent = config.getBoolean("System.Hunting.PVPRewardType", true);
        coinAmount = config.getDouble("System.Hunting.PVPRewardAmount", 0.05D);
        setCoinRewardMessage(new DefaultMessage(config.getString("System.Messages.PVPRewardMessage", PVP_REWARD_MESSAGE)));
    }

    public boolean isCoinPercent()
    {
        return coinPercent;
    }

    public void setCoinPercent(boolean coinPercent)
    {
        this.coinPercent = coinPercent;
    }

    public double getCoinAmount()
    {
        return coinAmount;
    }

    public void setCoinAmount(double coinAmount)
    {
        this.coinAmount = coinAmount;
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            return ((PlayerKilledEvent) event).getVictim().getLocation();
        }
        else {
            throw new IllegalArgumentException("Unrecognized event");
        }
    }

    @Override
    public Collection<AbstractDrop> assembleDrop(Event event)
    {
        Collection<AbstractDrop> drops = new ArrayList<>();

        CoinDrop drop = new CoinDrop(getName(), getLocation(event));

        if (coinPercent && event instanceof PlayerKilledEvent && DependencyUtils.hasEconomy()) {
            Player victim = ((PlayerKilledEvent) event).getVictim();
            drop.setCoin(DependencyUtils.getEconomy().getBalance(victim));
            drop.setGain(coinAmount / 100.0);
        }
        else {
            drop.setCoin(coinAmount);
        }

        drop.setMessage(getCoinRewardMessage());
        drop.setIntegerCurrency(isIntegerCurrency());
        drops.add(drop);

        return drops;
    }
}
