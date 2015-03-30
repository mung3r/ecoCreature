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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.massivecore.ps.PS;

public class FactionsGain extends AbstractPlayerGain<Rel>
{
    public FactionsGain(Map<Rel, Double> multipliers)
    {
        super(multipliers, "gain.factions");
    }

    @Override
    public boolean hasPermission(Player player)
    {
        return super.hasPermission(player) && DependencyUtils.hasFactions();
    }

    @Override
    public double getGain(Player player)
    {
        MPlayer uPlayer = MPlayer.get(player);
        Faction faction = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
        Rel rel = RelationUtil.getRelationOfThatToMe(faction, uPlayer);
        double multiplier = uPlayer != null && getMultipliers().containsKey(rel) ?
                getMultipliers().get(rel) : NO_GAIN;
        LoggerUtil.getInstance().debug("Gain: " + multiplier);
        return multiplier;
    }

    public static Collection<PlayerGain> parseConfig(ConfigurationSection config)
    {
        Collection<PlayerGain> gain = Collections.emptyList();

        if (config != null && DependencyUtils.hasFactions()) {
            Map<Rel, Double> multipliers = new HashMap<>();
            for (String relation : config.getKeys(false)) {
                try {
                    multipliers.put(Rel.valueOf(relation), config.getConfigurationSection(relation).getDouble(AMOUNT_KEY, NO_GAIN));
                }
                catch (IllegalArgumentException e) {
                    LoggerUtil.getInstance().warning("Unrecognized Factions relation: " + relation);
                }
            }
            gain = new ArrayList<>();
            gain.add(new FactionsGain(multipliers));
        }

        return gain;
    }
}
