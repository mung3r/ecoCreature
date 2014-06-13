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
