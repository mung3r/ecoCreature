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
package se.crafted.chrisb.ecoCreature.drops.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.drops.categories.Bonus;

@Aspect
public class BonusChanceAspect
{
    private static Bonus bonus = new Bonus(1.0, 0);

    @Around("execution(double se.crafted.chrisb.ecoCreature.drops.models.AbstractDrop+.getPercentage())")
    public double applyBonusMultiplierAspect(ProceedingJoinPoint pjp) throws Throwable
    {
        double percentage = (double) pjp.proceed();
        if (bonus.isValid()) {
            LoggerUtil.getInstance().debug("applying bonus multiplier " + bonus.getMultiplier() + " to " + percentage);
            percentage *=  bonus.getMultiplier();
        }
        return percentage;
    }

    public static synchronized Bonus getBonus()
    {
        return bonus;
    }

    public static synchronized void setBonus(Bonus bonus)
    {
        BonusChanceAspect.bonus = bonus;
    }
}