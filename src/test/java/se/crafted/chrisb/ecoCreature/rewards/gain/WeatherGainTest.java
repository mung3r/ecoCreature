package se.crafted.chrisb.ecoCreature.rewards.gain;

import junit.framework.Assert;

import org.junit.Test;

import se.crafted.chrisb.ecoCreature.commons.WeatherType;

public class WeatherGainTest
{

    @Test
    public void testValueOf()
    {
        WeatherType type = null;

        try {
            type = WeatherType.valueOf("foo");
        }
        catch (IllegalArgumentException e) {
            Assert.assertNull(type);
        }

        type = WeatherType.valueOf("SUNNY");
        Assert.assertEquals(WeatherType.SUNNY, type);
    }
}
