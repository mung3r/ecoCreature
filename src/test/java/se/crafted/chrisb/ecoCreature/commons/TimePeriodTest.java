package se.crafted.chrisb.ecoCreature.commons;

import junit.framework.Assert;

import org.junit.Test;

public class TimePeriodTest
{
    private static final long TEST_DAY = 6500;
    private static final long TEST_SUNSET = 13250;
    private static final long TEST_DUSK = 13750;
    private static final long TEST_NIGHT = 18000;
    private static final long TEST_DAWN = 22500;
    private static final long TEST_SUNRISE = 23500;
    private static final long TEST_NONE = 30000;

    @Test
    public void testFromTime()
    {
        Assert.assertEquals(TimePeriod.DAY, TimePeriod.fromTime(TEST_DAY));
        Assert.assertEquals(TimePeriod.SUNSET, TimePeriod.fromTime(TEST_SUNSET));
        Assert.assertEquals(TimePeriod.DUSK, TimePeriod.fromTime(TEST_DUSK));
        Assert.assertEquals(TimePeriod.NIGHT, TimePeriod.fromTime(TEST_NIGHT));
        Assert.assertEquals(TimePeriod.DAWN, TimePeriod.fromTime(TEST_DAWN));
        Assert.assertEquals(TimePeriod.SUNRISE, TimePeriod.fromTime(TEST_SUNRISE));
        Assert.assertEquals(TimePeriod.NONE, TimePeriod.fromTime(TEST_NONE));
    }

}
