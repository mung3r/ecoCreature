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

import java.util.Random;

import org.apache.commons.lang.math.NumberRange;

public abstract class AbstractDrop implements Drop
{
    private NumberRange range;
    private double percentage;
    private final Random random = new Random();

    @Override
    public NumberRange getRange()
    {
        return range;
    }

    @Override
    public void setRange(NumberRange range)
    {
        this.range = range;
    }

    @Override
    public double getPercentage()
    {
        return percentage;
    }

    @Override
    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
    }

    @Override
    public double getChance() 
    {
        return percentage / 100.0D;
    }

    @Override
    public Random getRandom()
    {
        return random;
    }

    @Override
    public int nextAmount()
    {
        int amount;

        if (getRandom().nextDouble() < getChance()) {
            if (getRange().getMinimumInteger() == getRange().getMaximumInteger()) {
                amount = getRange().getMinimumInteger();
            }
            else if (getRange().getMinimumInteger() > getRange().getMaximumInteger()) {
                amount = getRange().getMinimumInteger();
            }
            else {
                amount = getRange().getMinimumInteger() + getRandom().nextInt(getRange().getMaximumInteger() - getRange().getMinimumInteger() + 1);
            }
        }
        else {
            amount = 0;
        }

        return amount;
    }
}
