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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.RewardEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public class EntityKilledEventHandler extends AbstractEventHandler
{
    public EntityKilledEventHandler(ecoCreature plugin)
    {
        super(plugin);
    }

    @Override
    public boolean isRewardSource(Event event)
    {
        return event instanceof EntityKilledEvent;
    }

    @Override
    public Set<RewardEvent> createRewardEvents(Event event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        if (event instanceof EntityKilledEvent) {
            events = new HashSet<RewardEvent>();
            events.addAll(createRewardEvents((EntityKilledEvent) event));
        }

        return events;
    }

    private Set<RewardEvent> createRewardEvents(EntityKilledEvent event)
    {
        Set<RewardEvent> events = Collections.emptySet();

        Player killer = event.getKiller();
        WorldSettings settings = getSettings(killer.getWorld());
        event.setSpawnerMobTracking(settings);

        if (settings.hasReward(event)) {
            Reward reward = settings.createReward(event);
            reward.setGain(settings.getGainMultiplier(killer));
            reward.setParty(settings.getPartyMembers(killer));
            reward.addParameter(MessageToken.CREATURE, reward.getName())
                .addParameter(MessageToken.ITEM, event.getWeaponName());

            if ((settings.isOverrideDrops() && reward.hasDrops()) || (settings.isClearOnNoDrops() && !reward.hasDrops())) {
                event.getDrops().clear();
            }

            if ((settings.isClearEnchantedDrops())) {
                Iterables.removeIf(event.getDrops(), new Predicate<ItemStack>() {

                    @Override
                    public boolean apply(ItemStack stack)
                    {
                        boolean notEmpty = !stack.getEnchantments().isEmpty();
                        LoggerUtil.getInstance().debugTrue("Cleared enchanted item: " + stack.getType(), notEmpty);
                        return notEmpty;
                    }
                });
            }

            if (reward.getEntityDrops().contains(EntityType.EXPERIENCE_ORB)) {
                event.setDroppedExp(0);
            }

            addPlayerSkullToEvent(reward, event);
            addBooksToEvent(reward, event);

            events = new HashSet<RewardEvent>();
            events.add(new RewardEvent(killer, reward));
        }

        return events;
    }
}
