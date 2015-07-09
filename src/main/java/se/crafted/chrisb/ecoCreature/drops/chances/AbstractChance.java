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
package se.crafted.chrisb.ecoCreature.drops.chances;

import java.util.Random;

import org.apache.commons.lang.math.NumberRange;

public abstract class AbstractChance implements Chance
{
    private NumberRange range;
    private double percentage;
    private final Random random;

    public AbstractChance()
    {
        range = new NumberRange(1, 1);
        percentage = 100.0D;
        random = new Random();
    }

    public void setRange(NumberRange range)
    {
        this.range = range;
    }

    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
    }

    public double getChance()
    {
        return percentage / 100.0D;
    }

    @Override
    public boolean nextWinner()
    {
        return random.nextDouble() < getChance();
    }

    @Override
    public int nextIntAmount(int lootLevel)
    {
        int amount;

        if (nextWinner()) {
            if (range.getMinimumInteger() == range.getMaximumInteger()) {
                amount = range.getMinimumInteger();
            }
            else if (range.getMinimumInteger() > range.getMaximumInteger()) {
                amount = range.getMinimumInteger();
            }
            else {
                amount = range.getMinimumInteger() + random.nextInt(range.getMaximumInteger() + lootLevel - range.getMinimumInteger() + 1);
            }
        }
        else {
            amount = 0;
        }

        return amount;
    }

    @Override
    public int nextIntAmount()
    {
        return nextIntAmount(0);
    }

    @Override
    public int nextFixedAmount()
    {
        return range.getMinimumInteger() > range.getMaximumInteger() ? range.getMinimumInteger() : range.getMaximumInteger();
    }

    @Override
    public double nextDoubleAmount()
    {
        double amount;

        if (nextWinner()) {
            if (range.getMinimumDouble() == range.getMaximumDouble()) {
                amount = range.getMaximumDouble();
            }
            else if (range.getMinimumDouble() > range.getMaximumDouble()) {
                amount = range.getMinimumDouble();
            }
            else {
                amount = range.getMinimumDouble() + random.nextDouble() * (range.getMaximumDouble() - range.getMinimumDouble());
            }
        }
        else {
            amount = 0.0D;
        }

        return amount;
    }
}
