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
package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collection;

import net.minecraft.util.com.google.common.collect.Lists;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public class McMMOEventHandler extends AbstractEventHandler
{
    public McMMOEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean isRewardSource(Event event)
    {
        return event instanceof McMMOPlayerLevelUpEvent;
    }

    @Override
    public Collection<RewardEvent> createRewardEvents(Event event)
    {
        return event instanceof McMMOPlayerLevelUpEvent ? createRewardEvents((McMMOPlayerLevelUpEvent) event) : EMPTY_COLLECTION;
    }

    private Collection<RewardEvent> createRewardEvents(McMMOPlayerLevelUpEvent event)
    {
        Player player = event.getPlayer();
        WorldSettings settings = getSettings(player.getWorld());

        return Lists.newArrayList(new RewardEvent(player, settings.createRewards(event)));
    }
}
