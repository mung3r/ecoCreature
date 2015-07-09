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
package se.crafted.chrisb.ecoCreature.events.mappers;

import java.util.Collection;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.DropConfigLoader;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.CoinDrop;
import se.crafted.chrisb.ecoCreature.drops.EntityDrop;
import se.crafted.chrisb.ecoCreature.drops.ItemDrop;
import se.crafted.chrisb.ecoCreature.drops.sources.DropConfig;
import se.crafted.chrisb.ecoCreature.events.DropEvent;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class EntityKilledEventMapper extends AbstractEventMapper
{
    public EntityKilledEventMapper(DropConfigLoader dropConfigLoader)
    {
        super(dropConfigLoader);
    }

    @Override
    public boolean canMap(Event event)
    {
        return event instanceof EntityKilledEvent;
    }

    @Override
    public Collection<DropEvent> mapEvent(Event event)
    {
        return canMap(event) ? createDropEvents((EntityKilledEvent) event) : EMPTY_COLLECTION;
    }

    private Collection<DropEvent> createDropEvents(final EntityKilledEvent event)
    {
        final Player killer = event.getKiller();
        final DropConfig dropConfig = getDropConfig(killer.getWorld());
        event.setSpawnerMobTracker(dropConfig);

        Collection<AbstractDrop> drops = Collections2.transform(dropConfig.collectDrops(event), new Function<AbstractDrop, AbstractDrop>() {

            @Override
            public AbstractDrop apply(AbstractDrop drop)
            {
                if (drop instanceof CoinDrop) {
                    CoinDrop coinDrop = (CoinDrop) drop;
                    coinDrop.setGain(dropConfig.getGainMultiplier(killer));
                    coinDrop.setParty(dropConfig.getPartyMembers(killer));
                    coinDrop.addParameter(MessageToken.CREATURE, coinDrop.getName())
                    .addParameter(MessageToken.ITEM, event.getWeaponName());
                }

                if (drop instanceof ItemDrop) {
                    ItemDrop itemDrop = (ItemDrop) drop;
                    if (dropConfig.isOverrideDrops() && !itemDrop.getItems().isEmpty() || dropConfig.isClearOnNoDrops() && itemDrop.getItems().isEmpty()) {
                        event.getDrops().clear();
                    }
        
                    if (dropConfig.isClearEnchantedDrops()) {
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
                }

                if (drop instanceof EntityDrop) {
                    EntityDrop entityDrop = (EntityDrop) drop;
                    if (entityDrop.getEntityTypes().contains(EntityType.EXPERIENCE_ORB)) {
                        event.setDroppedExp(0);
                    }
                }

                return drop;
            }
        });

        return Lists.newArrayList(new DropEvent(killer, drops));
    }
}
