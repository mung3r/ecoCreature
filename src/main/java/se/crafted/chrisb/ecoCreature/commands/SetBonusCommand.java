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
package se.crafted.chrisb.ecoCreature.commands;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import se.crafted.chrisb.ecoCreature.drops.aspects.BonusMultiplier;
import se.crafted.chrisb.ecoCreature.drops.aspects.BonusMultiplierAspect;

public class SetBonusCommand extends BasicCommand
{
    public SetBonusCommand()
    {
        super("SetBonus");
        setDescription("Set loot bonus multiplier");
        setUsage("/ecoc setbonus <multiplier> <duration> <message...>");
        setArgumentRange(3, 255);
        setIdentifiers("setbonus");
        setPermission("ecocreature.command.setbonus");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (args != null && args.length > 2) {
            try {
                double multiplier = Double.parseDouble(args[0]);
                long endTimeInMillis = getTimeInMillis(args[1]);
                BonusMultiplier bonusMultiplier = new BonusMultiplier(multiplier, endTimeInMillis);
                BonusMultiplierAspect.setBonusMultiplier(bonusMultiplier);

                StringBuilder message = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    message.append(args[i]);
                    if (i + 1 < args.length) {
                        message.append(" ");
                    }
                }
                sender.sendMessage("Loot bonus multiplier set for " + bonusMultiplier.getMultiplier() + ", ending at " + new Date(bonusMultiplier.getEndTimeInMillis()));
            }
            catch (IllegalArgumentException e) {
                sender.sendMessage(e.getMessage());
            }
        }
        else {
            sender.sendMessage(getUsage());
        }
        return true;
    }

    private long getTimeInMillis(String arg)
    {
        final Calendar cal = new GregorianCalendar();
        Integer field = null;
        final Pattern pattern = Pattern.compile("(?i)(\\d+)(.+)", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(arg);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Syntax Error");
        }

        int amount = Integer.parseInt(matcher.group(1));
        arg = matcher.group(2);
        if (arg.equalsIgnoreCase("min")) {
            field = Calendar.MINUTE;
        }
        else if (arg.equalsIgnoreCase("s")) {
            field = Calendar.SECOND;
        }
        else if (arg.equalsIgnoreCase("h")) {
            field = Calendar.HOUR_OF_DAY;
        }
        else if (arg.equalsIgnoreCase("d")) {
            field = Calendar.DAY_OF_YEAR;
        }
        else if (arg.equalsIgnoreCase("w")) {
            field = Calendar.WEEK_OF_YEAR;
        }
        else if (arg.equalsIgnoreCase("m")) {
            field = Calendar.MONTH;
        }
        else {
            throw new IllegalArgumentException("Invalid time-type given, use: min - Minutes, s - Seconds, h - Hours, d - Days, w - Weeks, m - Month");
        }

        cal.add(field, amount);
        return cal.getTimeInMillis();
    }
}
