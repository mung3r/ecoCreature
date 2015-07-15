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
package se.crafted.chrisb.ecoCreature.drops.gain;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.quartz.CronExpression;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

public class CronGain extends AbstractPlayerGain<String>
{
    public CronGain(Map<String, Double> multipliers)
    {
        super(multipliers, "gain.cron");
    }

    @Override
    public double getGain(Player player)
    {
        double multiplier = NO_GAIN;
        
        for (Entry<String, Double> entry: getMultipliers().entrySet()) {
            if (isCronSatisfied(entry.getKey())) {
                multiplier *= entry.getValue();
            }
        }

        LoggerUtil.getInstance().debug("Gain: " + multiplier);
        return multiplier;
    }

    private boolean isCronSatisfied(String cronExpression)
    {
        boolean satisfied = false;

        try {
            Class.forName("org.quartz.CronExpression");
            CronExpression cron = new CronExpression(cronExpression);
            satisfied = cron.isSatisfiedBy(new Date());
        }
        catch (ClassNotFoundException e) {
            LoggerUtil.getInstance().debug("quartz jar not installed");
        }
        catch (ParseException e) {
            LoggerUtil.getInstance().warning("cron expression syntax error: " + cronExpression);
        }

        return satisfied;
    }

    public static Collection<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Collection<PlayerGain> gain = Collections.emptyList();

        if (config != null) {
            gain = new ArrayList<>();
            gain.add(new CronGain(parseMultipliers(config)));
        }

        return gain;
    }
}