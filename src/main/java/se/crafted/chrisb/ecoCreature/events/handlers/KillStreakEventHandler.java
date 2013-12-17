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
package se.crafted.chrisb.ecoCreature.events.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public class KillStreakEventHandler extends AbstractEventHandler
{
    public KillStreakEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean isRewardSource(Event event)
    {
        return event instanceof KillStreakEvent;
    }

    @Override
    public Set<RewardEvent> createRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        if (event instanceof KillStreakEvent) {
            events = new HashSet<RewardEvent>();
            events.addAll(createRewardEvents((KillStreakEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> createRewardEvents(KillStreakEvent event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        Player player = event.getPlayer();
        int kills = event.getKills();
        WorldSettings settings = getSettings(player.getWorld());

        if (settings.hasReward(event)) {
            Reward reward = settings.createReward(event);
            reward.setGain(kills);

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(player, reward));
        }

        return events;
    }
}
