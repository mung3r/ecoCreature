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
import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.CoinDrop;
import se.crafted.chrisb.ecoCreature.drops.CustomEntityDrop;
import se.crafted.chrisb.ecoCreature.drops.EntityDrop;
import se.crafted.chrisb.ecoCreature.drops.ItemDrop;
import se.crafted.chrisb.ecoCreature.drops.JockeyDrop;
import se.crafted.chrisb.ecoCreature.drops.chances.AbstractChance;
import se.crafted.chrisb.ecoCreature.drops.chances.BookChance;
import se.crafted.chrisb.ecoCreature.drops.chances.CoinChance;
import se.crafted.chrisb.ecoCreature.drops.chances.CustomEntityChance;
import se.crafted.chrisb.ecoCreature.drops.chances.EntityChance;
import se.crafted.chrisb.ecoCreature.drops.chances.ItemChance;
import se.crafted.chrisb.ecoCreature.drops.chances.JockeyChance;
import se.crafted.chrisb.ecoCreature.drops.chances.LoreChance;
import se.crafted.chrisb.ecoCreature.drops.rules.AbstractRule;
import se.crafted.chrisb.ecoCreature.drops.rules.Rule;
import se.crafted.chrisb.ecoCreature.messages.CoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCoinMessageDecorator;

public abstract class AbstractDropSource extends AbstractChance
{
    private static final String NO_COIN_REWARD_MESSAGE = "&7You slayed a &5<crt>&7 using a &3<itm>&7.";
    private static final String COIN_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for slaying a &5<crt>&7.";
    private static final String COIN_PENALTY_MESSAGE = "&7You are penalized &6<amt>&7 for slaying a &5<crt>&7.";

    private String name;

    private Collection<AbstractChance> chances;

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

        chances = new ArrayList<>();
        chances.addAll(ItemChance.parseConfig(dropConfig));
        chances.addAll(BookChance.parseConfig(dropConfig));
        chances.addAll(LoreChance.parseConfig(dropConfig));
        chances.addAll(CustomEntityChance.parseConfig(dropConfig));
        chances.addAll(EntityChance.parseConfig(dropConfig));
        chances.addAll(JockeyChance.parseConfig(dropConfig));
        chances.addAll(CoinChance.parseConfig(dropConfig));

        coinRewardMessage = new CoinMessageDecorator(new DefaultMessage(dropConfig.getString("Reward_Message", config.getString("System.Messages.Reward_Message", COIN_REWARD_MESSAGE))));
        coinPenaltyMessage = new CoinMessageDecorator(new DefaultMessage(dropConfig.getString("Penalty_Message", config.getString("System.Messages.Penalty_Message", COIN_PENALTY_MESSAGE))));
        noCoinRewardMessage = new NoCoinMessageDecorator(new DefaultMessage(dropConfig.getString("NoReward_Message", config.getString("System.Messages.NoReward_Message", NO_COIN_REWARD_MESSAGE))));

        addToInventory = dropConfig.getBoolean("AddItemsToInventory");
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

    public Collection<AbstractDrop> assembleDrops(Event event)
    {
        Collection<AbstractDrop> drops = new ArrayList<>();
        int amount = nextIntAmount();

        for (int i = 0; i < amount; i++) {
            drops.addAll(assembleDrop(event));
        }

        return drops;
    }

    protected Collection<AbstractDrop> assembleDrop(Event event)
    {
        Collection<AbstractDrop> drops = new ArrayList<>();

        for (AbstractChance chance : chances) {
            if (chance instanceof JockeyChance) {
                JockeyDrop drop = new JockeyDrop(name, getLocation(event));
                drop.setEntityTypes(((JockeyChance) chance).nextEntityTypes());
                drops.add(drop);
            }
            else if (chance instanceof CustomEntityChance) {
                CustomEntityDrop drop = new CustomEntityDrop(name, getLocation(event));
                drop.setCustomEntityTypes(((CustomEntityChance) chance).nextEntityTypes());
                drops.add(drop);
            }
            else if (chance instanceof EntityChance) {
                EntityDrop drop = new EntityDrop(name, getLocation(event));
                drop.setEntityTypes(((EntityChance) chance).nextEntityTypes());
                drops.add(drop);
            }
            else if (chance instanceof ItemChance) {
                ItemDrop drop = new ItemDrop(name, getLocation(event));
                drop.getItems().add(((ItemChance) chance).nextItemStack(fixedAmount));
                drop.setAddToInventory(addToInventory);
                drops.add(drop);
            }
            else if (chance instanceof CoinChance) {
                CoinChance coin = (CoinChance) chance;

                CoinDrop drop = new CoinDrop(name, getLocation(event));
                drop.setCoin(coin.nextDoubleAmount());

                if (drop.getCoin() > 0.0) {
                    drop.setMessage(coinRewardMessage);
                }
                else if (drop.getCoin() < 0.0) {
                    drop.setMessage(coinPenaltyMessage);
                }
                else {
                    drop.setMessage(noCoinRewardMessage);
                }

                drop.setIntegerCurrency(integerCurrency);
                drops.add(drop);
            }
        }

        return drops;
    }

    protected abstract Location getLocation(Event event);
}
