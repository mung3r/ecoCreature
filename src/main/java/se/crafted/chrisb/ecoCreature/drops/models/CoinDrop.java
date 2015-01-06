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
package se.crafted.chrisb.ecoCreature.drops.models;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.configuration.ConfigurationSection;

public class CoinDrop extends AbstractDrop
{
    private final double multiplier;

    public CoinDrop(NumberRange range, double percentage, double multiplier)
    {
        setRange(range);
        setPercentage(percentage);
        this.multiplier = multiplier;
    }

    @Override
    public double nextDoubleAmount()
    {
        return super.nextDoubleAmount() * multiplier;
    }

    public static CoinDrop parseConfig(ConfigurationSection config)
    {
        CoinDrop coin = null;

        if (config != null && config.contains("Coin_Maximum") && config.contains("Coin_Minimum") && config.contains("Coin_Percent")) {
            coin = new CoinDrop(new NumberRange(config.getDouble("Coin_Minimum", 0), config.getDouble("Coin_Maximum", 0)),
                    config.getDouble("Coin_Percent", 0.0D), config.getDouble("Coin_Gain", 1.0D));
        }

        return coin;
    }
}
