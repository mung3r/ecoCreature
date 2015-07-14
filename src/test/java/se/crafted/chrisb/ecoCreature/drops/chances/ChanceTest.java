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

import junit.framework.Assert;

import org.apache.commons.lang.math.NumberRange;
import org.junit.Test;

public class ChanceTest
{

    class TestChance extends AbstractChance
    {

        public TestChance(NumberRange range, double percentage)
        {
            setRange(range);
            setPercentage(percentage);
        }

    }

    @Test
    public void testNextWinner()
    {
        Number samples = 100000;
        Number percent = 3;

        Chance chance = new TestChance(new NumberRange(1), percent.doubleValue());
        for (int i = 0; i < 10; i++) {
            double winnerCount = 0;
            for (int j = 0; j < samples.intValue(); j++) {
                if (chance.nextWinner()) {
                    winnerCount++;
                }
            }
            double idealChance = winnerCount / samples.doubleValue();
            double delta = Math.abs(percent.doubleValue() / 100D - idealChance);
            Assert.assertTrue(delta < 0.01);
        }
    }

    @Test
    public void testAmounts()
    {
        int samples = 10000000;

        int min = 1;
        int max = 10;
        int[] bucket = new int[max + 1];
        Chance chance = new TestChance(new NumberRange(min, max), 100);

        for (int i = 0; i < samples; i++) {
            int amount = chance.nextIntAmount();
            bucket[amount]++;
        }

        double idealDist = samples / (max - min + 1);
        int total = 0;
        double maxDelta = 0;
        for (int i = min; i < bucket.length; i++) {
            double delta = Math.abs(idealDist - (double) bucket[i]) / idealDist;
            total += bucket[i];
            if (delta > maxDelta) {
                maxDelta = delta;
            }
            Assert.assertTrue(delta < 0.01);
        }
        Assert.assertEquals(samples, total);
    }
}
