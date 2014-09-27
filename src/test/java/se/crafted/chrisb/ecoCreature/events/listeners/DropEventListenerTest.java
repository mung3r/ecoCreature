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
package se.crafted.chrisb.ecoCreature.events.listeners;

import junit.framework.Assert;

import org.junit.Test;

public class DropEventListenerTest
{

    @Test
    public void testRounding()
    {
        double round1 = DropEventListener.round(0.4999, 0);
        double round2 = DropEventListener.round(0.5000, 0);
        double round3 = DropEventListener.round(0.5049, 2);
        double round4 = DropEventListener.round(0.5050, 2);
        Assert.assertTrue(round1 == 0.0);
        Assert.assertTrue(round2 > 0.0);
        Assert.assertTrue(round3 == 0.5);
        Assert.assertTrue(round4 > 0.5);
        
        double round5 = DropEventListener.round(-0.4999, 0);
        double round6 = DropEventListener.round(-0.5000, 0);
        double round7 = DropEventListener.round(-0.5049, 2);
        double round8 = DropEventListener.round(-0.5050, 2);
        Assert.assertTrue(round5 == 0.0);
        Assert.assertTrue(round6 < 0.0);
        Assert.assertTrue(round7 == -0.5);
        Assert.assertTrue(round8 < 0.5);
    }

}
