/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature.rewards.gain;

import java.util.Map;

import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

public abstract class AbstractFactionsGain<T> extends AbstractPlayerGain<T>
{
    public AbstractFactionsGain(Map<T, Double> multipliers)
    {
        super(multipliers, "gain.factions");
    }

    @Override
    public boolean hasPermission(Player player)
    {
        return super.hasPermission(player) && DependencyUtils.hasFactions();
    }

    @Override
    public double getMultiplier(Player player)
    {
        double multiplier = NO_GAIN;

        FPlayer fPlayer = FPlayers.i.get(player);
        if (fPlayer != null && getMultipliers().containsKey(fPlayer.getRelationToLocation())) {
            multiplier = getMultipliers().get(fPlayer.getRelationToLocation());
            LoggerUtil.getInstance().debug(this.getClass(), "Factions multiplier: " + multiplier);
        }

        return multiplier;
    }
}
