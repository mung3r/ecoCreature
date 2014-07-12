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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public class PlayerKilledEventHandler extends AbstractEventHandler
{
    public PlayerKilledEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean isRewardSource(Event event)
    {
        return event instanceof PlayerKilledEvent;
    }

    @Override
    public Set<RewardEvent> createRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        if (event instanceof PlayerKilledEvent) {
            events = new HashSet<RewardEvent>();
            events.addAll(createRewardEvents((PlayerKilledEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> createRewardEvents(PlayerKilledEvent event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        Player killer = event.getKiller();
        Player victim = event.getVictim();
        WorldSettings settings = getSettings(killer.getWorld());

        for (Reward killerReward : createWinnerReward(event)) {
            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(killer, killerReward));

            PlayerDeathEvent deathEvent = new PlayerDeathEvent(event.getEntity(), event.getDrops(), event.getDroppedExp(), event.getNewExp(),
                    event.getNewTotalExp(), event.getNewLevel(), event.getDeathMessage());
            for (Reward penalty : settings.createReward(deathEvent)) {
                penalty.setCoin(killerReward.getCoin());
                penalty.setGain(-killerReward.getGain());

                events.add(new RewardEvent(victim, penalty));
            }
        }

        return events;
    }

    private List<Reward> createWinnerReward(PlayerKilledEvent event)
    {
        List<Reward> rewards = new ArrayList<Reward>();

        for (Reward reward : getSettings(event.getEntity().getWorld()).createReward(event)) {
            reward.addParameter(MessageToken.CREATURE, event.getVictim().getName());
    
            /*if ((settings.isOverrideDrops() && reward.hasDrops()) || (settings.isClearOnNoDrops() && !reward.hasDrops())) {
                event.getDrops().clear();
            }*/
    
            if (reward.getEntityDrops().contains(EntityType.EXPERIENCE_ORB)) {
                event.setDroppedExp(0);
            }
    
            //addPlayerSkullToEvent(reward, event);
            addBooksToEvent(reward, event);
            
            rewards.add(reward);
        }

        return rewards;
    }
}
