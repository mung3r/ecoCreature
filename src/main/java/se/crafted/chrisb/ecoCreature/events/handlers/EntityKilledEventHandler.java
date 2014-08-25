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

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
    public Collection<RewardEvent> createRewardEvents(Event event)
    {
        return event instanceof EntityKilledEvent ? createRewardEvents((EntityKilledEvent) event) : EMPTY_COLLECTION;
    }

    private Collection<RewardEvent> createRewardEvents(final EntityKilledEvent event)
    {
        final Player killer = event.getKiller();
        final WorldSettings settings = getSettings(killer.getWorld());
        event.setSpawnerMobTracking(settings);

        Collection<Reward> rewards = Collections2.transform(settings.createRewards(event), new Function<Reward, Reward>() {

            @Override
            public Reward apply(Reward reward)
            {
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

                return reward;
            }
        });

        return Lists.newArrayList(new RewardEvent(killer, rewards));
    }
}
