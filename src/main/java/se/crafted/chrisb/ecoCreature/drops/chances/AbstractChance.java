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

    public NumberRange getRange()
    {
        return range;
    }

    public void setRange(NumberRange range)
    {
        this.range = range;
    }

    public double getPercentage()
    {
        return percentage;
    }

    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
    }

    @Override
    public boolean nextWinner()
    {
        return random.nextDouble() < getPercentage() / 100.0D;
    }

    @Override
    public int nextIntAmount()
    {
        int amount;

        if (nextWinner()) {
            if (getRange().getMinimumInteger() == getRange().getMaximumInteger()) {
                amount = getRange().getMinimumInteger();
            }
            else if (getRange().getMinimumInteger() > getRange().getMaximumInteger()) {
                amount = getRange().getMinimumInteger();
            }
            else {
                amount = getRange().getMinimumInteger() + random.nextInt(getRange().getMaximumInteger() - getRange().getMinimumInteger() + 1);
            }
        }
        else {
            amount = 0;
        }

        return amount;
    }

    @Override
    public int getFixedAmount()
    {
        return getRange().getMinimumInteger() > getRange().getMaximumInteger() ? getRange().getMinimumInteger() : getRange().getMaximumInteger();
    }

    @Override
    public double nextDoubleAmount()
    {
        double amount;

        if (nextWinner()) {
            if (getRange().getMinimumDouble() == getRange().getMaximumDouble()) {
                amount = getRange().getMaximumDouble();
            }
            else if (getRange().getMinimumDouble() > getRange().getMaximumDouble()) {
                amount = getRange().getMinimumDouble();
            }
            else {
                amount = getRange().getMinimumDouble() + random.nextDouble() * (getRange().getMaximumDouble() - getRange().getMinimumDouble());
            }
        }
        else {
            amount = 0.0D;
        }

        return amount;
    }
}
