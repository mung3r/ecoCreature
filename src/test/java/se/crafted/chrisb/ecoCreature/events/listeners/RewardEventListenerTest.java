package se.crafted.chrisb.ecoCreature.events.listeners;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

public class RewardEventListenerTest
{

    @Test
    public void testRounding()
    {
        double round1 = RewardEventListener.round(0.4999, 0, BigDecimal.ROUND_HALF_UP);
        double round2 = RewardEventListener.round(0.5000, 0, BigDecimal.ROUND_HALF_UP);
        double round3 = RewardEventListener.round(0.5049, 2, BigDecimal.ROUND_HALF_UP);
        double round4 = RewardEventListener.round(0.5050, 2, BigDecimal.ROUND_HALF_UP);
        Assert.assertTrue(round1 == 0.0);
        Assert.assertTrue(round2 > 0.0);
        Assert.assertTrue(round3 == 0.5);
        Assert.assertTrue(round4 > 0.5);
        
        double round5 = RewardEventListener.round(-0.4999, 0, BigDecimal.ROUND_HALF_UP);
        double round6 = RewardEventListener.round(-0.5000, 0, BigDecimal.ROUND_HALF_UP);
        double round7 = RewardEventListener.round(-0.5049, 2, BigDecimal.ROUND_HALF_UP);
        double round8 = RewardEventListener.round(-0.5050, 2, BigDecimal.ROUND_HALF_UP);
        Assert.assertTrue(round5 == 0.0);
        Assert.assertTrue(round6 < 0.0);
        Assert.assertTrue(round7 == -0.5);
        Assert.assertTrue(round8 < 0.5);
    }

}
