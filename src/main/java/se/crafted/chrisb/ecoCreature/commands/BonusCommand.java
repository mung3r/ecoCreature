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
package se.crafted.chrisb.ecoCreature.commands;

import java.util.Date;

import org.bukkit.command.CommandSender;

import se.crafted.chrisb.ecoCreature.drops.aspects.BonusChanceAspect;
import se.crafted.chrisb.ecoCreature.drops.categories.Bonus;

public class BonusCommand extends BasicCommand
{
    public BonusCommand()
    {
        super("Bonus");
        setDescription("Show loot bonus info");
        setUsage("/ecoc bonus");
        setArgumentRange(0, 0);
        setIdentifiers("bonus");
        setPermission("ecocreature.command.bonus");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (args != null && args.length == 0) {
            Bonus bonus = BonusChanceAspect.getBonus();
            if (bonus != null && bonus.isValid()) {
                sender.sendMessage("Loot bonus multiplier set for " + bonus.getMultiplier() + ", ending at " + new Date(bonus.getEndTimeInMillis()));
            }
            else {
                sender.sendMessage("No active loot bonus");
            }
        }
        else {
            sender.sendMessage(getUsage());
        }
        return true;
    }
}