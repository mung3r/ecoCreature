package se.crafted.chrisb.ecoCreature.drops.models;

import junit.framework.Assert;

import org.apache.commons.lang.math.NumberRange;
import org.junit.Test;

public class DropTest
{

    class TestDrop extends AbstractDrop
    {

        public TestDrop(NumberRange range, double percentage)
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

        Drop drop = new TestDrop(new NumberRange(1, 1), percent.doubleValue());
        for (int i = 0; i < 10; i++) {
            double winnerCount = 0;
            for (int j = 0; j < samples.intValue(); j++) {
                if (drop.nextWinner()) {
                    winnerCount++;
                }
            }
            double chance = winnerCount / samples.doubleValue();
            double delta = Math.abs(percent.doubleValue() / 100D - chance);
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
        Drop drop = new TestDrop(new NumberRange(min, max), 100);

        for (int i = 0; i < samples; i++) {
            int amount = drop.nextIntAmount();
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
