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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.CoinDrop;
import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomDropType;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;

public class PVPDropSource extends AbstractDropSource
{
    private static final String PVP_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for murdering &5<crt>&7.";

    private Message coinRewardMessage;
    private boolean integerCurrency; 
    private boolean coinPercent;
    private double coinAmount;

    public PVPDropSource(ConfigurationSection config)
    {
        setName(CustomDropType.LEGACY_PVP.toString());
        coinRewardMessage = new DefaultMessage(config.getString("System.Messages.PVPRewardMessage", PVP_REWARD_MESSAGE), config.getBoolean("System.Messages.Output"));
        integerCurrency = config.getBoolean("System.Economy.IntegerCurrency");
        coinPercent = config.getBoolean("System.Hunting.PVPRewardType", true);
        coinAmount = config.getDouble("System.Hunting.PVPRewardAmount", 0.05D);
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
    protected int getLootLevel(Event event)
    {
        int lootLevel = 0;

        if (event instanceof PlayerKilledEvent) {
            ItemStack weapon = ((PlayerKilledEvent) event).getKiller().getItemInHand();

            if (weapon != null) {
                lootLevel = weapon.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
            }
        }

        return lootLevel;
    }

    @Override
    public Collection<AbstractDrop> collectDrop(Event event)
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

        drop.setMessage(coinRewardMessage);
        drop.setIntegerCurrency(integerCurrency);
        drops.add(drop);

        return drops;
    }
}
