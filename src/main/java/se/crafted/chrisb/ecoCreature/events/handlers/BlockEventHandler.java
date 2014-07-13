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

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public class BlockEventHandler extends AbstractEventHandler
{
    public BlockEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean isRewardSource(Event event)
    {
        return event instanceof BlockBreakEvent;
    }

    @Override
    public Collection<RewardEvent> createRewardEvents(Event event)
    {
        return event instanceof BlockBreakEvent ? createRewardEvents((BlockBreakEvent) event) : EMPTY_COLLECTION;
    }

    private Collection<RewardEvent> createRewardEvents(BlockBreakEvent event)
    {
        final Player player = event.getPlayer();
        final WorldSettings settings = getSettings(player.getWorld());

        Collection<Reward> rewards = Collections2.transform(settings.createRewards(event), new Function<Reward, Reward>() {

            @Override
            public Reward apply(Reward reward)
            {
                reward.setGain(settings.getGainMultiplier(player));
                reward.addParameter(MessageToken.ITEM, EntityUtils.getItemNameInHand(player))
                    .addParameter(MessageToken.CREATURE, reward.getName());
                return reward;
            }
        });

        return Lists.newArrayList(new RewardEvent(player, rewards));
    }
}
