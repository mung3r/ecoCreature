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
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.CoinDrop;
import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomDropType;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;

public class DeathPenaltyDropSource extends AbstractDropSource
{
    private static final String DEATH_PENALTY_MESSAGE = "&7You wake up to find &6<amt>&7 missing from your pockets!";

    private Message coinPenaltyMessage;
    private boolean integerCurrency; 
    private boolean percentPenalty;
    private double penaltyAmount;

    public DeathPenaltyDropSource(ConfigurationSection config)
    {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }

        setName(CustomDropType.DEATH_PENALTY.toString());
        coinPenaltyMessage = new DefaultMessage(config.getString("System.Messages.DeathPenaltyMessage", DEATH_PENALTY_MESSAGE), config.getBoolean("System.Messages.Output"));
        integerCurrency = config.getBoolean("System.Economy.IntegerCurrency");
        percentPenalty = config.getBoolean("System.Hunting.PenalizeType", true);
        penaltyAmount = config.getDouble("System.Hunting.PenalizeAmount", 0.05D);
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (event instanceof PlayerDeathEvent) {
            return ((PlayerDeathEvent) event).getEntity().getLocation();
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Collection<AbstractDrop> collectDrop(Event event)
    {
        Collection<AbstractDrop> drops = new ArrayList<>();
        CoinDrop drop = new CoinDrop(getName(), getLocation(event));

        if (percentPenalty && event instanceof PlayerDeathEvent && DependencyUtils.hasEconomy()) {
            Player player = ((PlayerDeathEvent) event).getEntity();
            drop.setCoin(DependencyUtils.getEconomy().getBalance(player));
            drop.setGain(-penaltyAmount / 100.0);
        }
        else {
            drop.setCoin(penaltyAmount);
            drop.setGain(-1.0);
        }

        drop.setMessage(coinPenaltyMessage);
        drop.setIntegerCurrency(integerCurrency);
        drops.add(drop);

        return drops;
    }
}
