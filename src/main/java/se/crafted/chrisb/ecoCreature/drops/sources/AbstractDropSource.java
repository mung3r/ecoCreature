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
package se.crafted.chrisb.ecoCreature.drops.sources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.drops.AssembledDrop;
import se.crafted.chrisb.ecoCreature.drops.models.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.models.BookDrop;
import se.crafted.chrisb.ecoCreature.drops.models.CoinDrop;
import se.crafted.chrisb.ecoCreature.drops.models.CustomEntityDrop;
import se.crafted.chrisb.ecoCreature.drops.models.EntityDrop;
import se.crafted.chrisb.ecoCreature.drops.models.ItemDrop;
import se.crafted.chrisb.ecoCreature.drops.models.JockeyDrop;
import se.crafted.chrisb.ecoCreature.drops.models.LoreDrop;
import se.crafted.chrisb.ecoCreature.drops.rules.AbstractRule;
import se.crafted.chrisb.ecoCreature.drops.rules.Rule;
import se.crafted.chrisb.ecoCreature.messages.CoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCoinMessageDecorator;

public abstract class AbstractDropSource extends AbstractDrop
{
    private static final String NO_COIN_REWARD_MESSAGE = "&7You slayed a &5<crt>&7 using a &3<itm>&7.";
    private static final String COIN_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for slaying a &5<crt>&7.";
    private static final String COIN_PENALTY_MESSAGE = "&7You are penalized &6<amt>&7 for slaying a &5<crt>&7.";

    private String name;

    private Collection<AbstractDrop> drops;

    private Message noCoinRewardMessage;
    private Message coinRewardMessage;
    private Message coinPenaltyMessage;

    private boolean fixedAmount;
    private boolean integerCurrency;
    private boolean addToInventory;

    private Map<Class<? extends AbstractRule>, Rule> huntingRules;

    public AbstractDropSource()
    {
        huntingRules = Collections.emptyMap();
    }

    public AbstractDropSource(String section, ConfigurationSection config)
    {
        this();

        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        ConfigurationSection dropConfig = config.getConfigurationSection(section);
        name = dropConfig.getName();

        drops = new ArrayList<>();
        drops.addAll(ItemDrop.parseConfig(dropConfig));
        drops.addAll(BookDrop.parseConfig(dropConfig));
        drops.addAll(LoreDrop.parseConfig(dropConfig));
        drops.addAll(CustomEntityDrop.parseConfig(dropConfig));
        drops.addAll(EntityDrop.parseConfig(dropConfig));
        drops.addAll(JockeyDrop.parseConfig(dropConfig));
        drops.addAll(CoinDrop.parseConfig(dropConfig));

        coinRewardMessage = new CoinMessageDecorator(new DefaultMessage(dropConfig.getString("Reward_Message", config.getString("System.Messages.Reward_Message", COIN_REWARD_MESSAGE))));
        coinPenaltyMessage = new CoinMessageDecorator(new DefaultMessage(dropConfig.getString("Penalty_Message", config.getString("System.Messages.Penalty_Message", COIN_PENALTY_MESSAGE))));
        noCoinRewardMessage = new NoCoinMessageDecorator(new DefaultMessage(dropConfig.getString("NoReward_Message", config.getString("System.Messages.NoReward_Message", NO_COIN_REWARD_MESSAGE))));

        addToInventory = dropConfig.getBoolean("AddItemsToInventory", false);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean hasPermission(Player player)
    {
        return DependencyUtils.hasPermission(player, "reward." + name);
    }

    public Message getNoCoinRewardMessage()
    {
        return noCoinRewardMessage;
    }

    public void setNoCoinRewardMessage(Message noCoinRewardMessage)
    {
        this.noCoinRewardMessage = noCoinRewardMessage;
    }

    public Message getCoinRewardMessage()
    {
        return coinRewardMessage;
    }

    public void setCoinRewardMessage(Message coinRewardMessage)
    {
        this.coinRewardMessage = coinRewardMessage;
    }

    public Message getCoinPenaltyMessage()
    {
        return coinPenaltyMessage;
    }

    public void setCoinPenaltyMessage(Message coinPenaltyMessage)
    {
        this.coinPenaltyMessage = coinPenaltyMessage;
    }

    public Boolean isFixedAmount()
    {
        return fixedAmount;
    }

    public void setFixedAmount(Boolean fixedAmount)
    {
        this.fixedAmount = fixedAmount;
    }

    public Boolean isIntegerCurrency()
    {
        return integerCurrency;
    }

    public void setIntegerCurrency(Boolean integerCurrency)
    {
        this.integerCurrency = integerCurrency;
    }

    public Boolean isAddToInventory()
    {
        return addToInventory;
    }

    public void setAddToInventory(Boolean addToInventory)
    {
        this.addToInventory = addToInventory;
    }

    public Map<Class<? extends AbstractRule>, Rule> getHuntingRules()
    {
        return huntingRules;
    }

    public void setHuntingRules(Map<Class<? extends AbstractRule>, Rule> huntingRules)
    {
        this.huntingRules = huntingRules;
    }

    public Collection<AssembledDrop> assembleDrops(Event event)
    {
        Collection<AssembledDrop> drops = new ArrayList<>();
        int amount = nextIntAmount();

        for (int i = 0; i < amount; i++) {
            drops.add(assembleDrop(event));
        }

        return drops;
    }

    protected AssembledDrop assembleDrop(Event event)
    {
        AssembledDrop assembledDrop = new AssembledDrop(getLocation(event));

        assembledDrop.setName(name);

        for (AbstractDrop drop : drops) {
            if (drop instanceof JockeyDrop) {
                assembledDrop.getJockeyTypes().addAll(((JockeyDrop) drop).nextEntityTypes());
            }
            else if (drop instanceof CustomEntityDrop) {
                assembledDrop.getCustomEntityTypes().addAll(((CustomEntityDrop) drop).nextEntityTypes());
            }
            else if (drop instanceof EntityDrop) {
                assembledDrop.getEntityTypes().addAll(((EntityDrop) drop).nextEntityTypes());
            }
            else if (drop instanceof ItemDrop) {
                assembledDrop.getItems().add(((ItemDrop) drop).nextItemStack(fixedAmount));
            }
            else if (drop instanceof CoinDrop) {
                CoinDrop coin = (CoinDrop) drop;

                assembledDrop.setCoin(coin.nextDoubleAmount());

                if (assembledDrop.getCoin() > 0.0) {
                    assembledDrop.setMessage(coinRewardMessage);
                }
                else if (assembledDrop.getCoin() < 0.0) {
                    assembledDrop.setMessage(coinPenaltyMessage);
                }
                else {
                    assembledDrop.setMessage(noCoinRewardMessage);
                }
            }
        }

        assembledDrop.setIntegerCurrency(integerCurrency);
        assembledDrop.setAddToInventory(addToInventory);

        return assembledDrop;
    }

    protected abstract Location getLocation(Event event);
}
