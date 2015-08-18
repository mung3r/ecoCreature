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
package se.crafted.chrisb.ecoCreature.drops.chances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.CoinDrop;
import se.crafted.chrisb.ecoCreature.messages.CoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCoinMessageDecorator;

public class CoinChance extends AbstractChance implements DropChance
{
    private static final String NO_COIN_REWARD_MESSAGE = "&7You slayed a &5<crt>&7 using a &3<itm>&7.";
    private static final String COIN_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for slaying a &5<crt>&7.";
    private static final String COIN_PENALTY_MESSAGE = "&7You are penalized &6<amt>&7 for slaying a &5<crt>&7.";

    private static final String PARTY_NO_REWARD_MESSAGE = "&7<plr> slayed a &5<crt>&7 using a &3<itm>&7.";
    private static final String PARTY_REWARD_MESSAGE = "&7Party awarded &6<amt>&7.";
    private static final String PARTY_PENALTY_MESSAGE = "&7Party penalized &6<amt>&7.";

    private Message noCoinRewardMessage;
    private Message coinRewardMessage;
    private Message coinPenaltyMessage;

    private Message partyNoRewardMessage;
    private Message partyRewardMessage;
    private Message partyPenaltyMessage;

    private boolean integerCurrency;

    private final double multiplier;

    public CoinChance(NumberRange range, double percentage, double multiplier)
    {
        setRange(range);
        setPercentage(percentage);
        this.multiplier = multiplier;
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

    public Message getPartyNoRewardMessage()
    {
        return partyNoRewardMessage;
    }

    public void setPartyNoRewardMessage(Message partyNoRewardMessage)
    {
        this.partyNoRewardMessage = partyNoRewardMessage;
    }

    public Message getPartyRewardMessage()
    {
        return partyRewardMessage;
    }

    public void setPartyRewardMessage(Message partyRewardMessage)
    {
        this.partyRewardMessage = partyRewardMessage;
    }

    public Message getPartyPenaltyMessage()
    {
        return partyPenaltyMessage;
    }

    public void setPartyPenaltyMessage(Message partyPenaltyMessage)
    {
        this.partyPenaltyMessage = partyPenaltyMessage;
    }

    public Boolean isIntegerCurrency()
    {
        return integerCurrency;
    }

    public void setIntegerCurrency(Boolean integerCurrency)
    {
        this.integerCurrency = integerCurrency;
    }

    @Override
    public double nextDoubleAmount()
    {
        return super.nextDoubleAmount() * multiplier;
    }

    @Override
    public AbstractDrop nextDrop(String name, Location location, int lootLevel)
    {
        CoinDrop drop = new CoinDrop(name, location);
        drop.setCoin(nextDoubleAmount());

        if (drop.getCoin() > 0.0) {
            drop.setMessage(coinRewardMessage);
            drop.setPartyMessage(partyRewardMessage);
        }
        else if (drop.getCoin() < 0.0) {
            drop.setMessage(coinPenaltyMessage);
            drop.setPartyMessage(partyPenaltyMessage);
        }
        else {
            drop.setMessage(noCoinRewardMessage);
            drop.setPartyMessage(partyNoRewardMessage);
        }

        drop.setIntegerCurrency(integerCurrency);

        return drop;
    }

    public static Collection<CoinChance> parseConfig(String section, ConfigurationSection config)
    {
        ConfigurationSection dropConfig = config.getConfigurationSection(section);
        Collection<CoinChance> chances = Collections.emptyList();

        if (dropConfig != null && dropConfig.contains("Coin_Maximum") && dropConfig.contains("Coin_Minimum") && dropConfig.contains("Coin_Percent")) {
            CoinChance chance = new CoinChance(new NumberRange(dropConfig.getDouble("Coin_Minimum", 0), dropConfig.getDouble("Coin_Maximum", 0)), dropConfig.getDouble(
                    "Coin_Percent", 0.0D), dropConfig.getDouble("Coin_Gain", 1.0D));

            CoinMessageDecorator rewardMessage = new CoinMessageDecorator(new DefaultMessage(dropConfig.getString("Reward_Message", config.getString("System.Messages.Reward_Message", COIN_REWARD_MESSAGE)), config.getBoolean("System.Messages.Output")));
            rewardMessage.setLoggingEnabled(config.getBoolean("System.Messages.LogCoinRewards", true));
            chance.setCoinRewardMessage(rewardMessage);

            CoinMessageDecorator penaltyMessage = new CoinMessageDecorator(new DefaultMessage(dropConfig.getString("Penalty_Message", config.getString("System.Messages.Penalty_Message", COIN_PENALTY_MESSAGE)), config.getBoolean("System.Messages.Output")));
            penaltyMessage.setLoggingEnabled(config.getBoolean("System.Messages.LogCoinRewards", true));
            chance.setCoinPenaltyMessage(penaltyMessage);

            NoCoinMessageDecorator noRewardMessage = new NoCoinMessageDecorator(new DefaultMessage(dropConfig.getString("NoReward_Message", config.getString("System.Messages.NoReward_Message", NO_COIN_REWARD_MESSAGE)), config.getBoolean("System.Messages.Output")));
            noRewardMessage.setNoRewardMessageEnabled(config.getBoolean("System.Messages.NoReward"));
            chance.setNoCoinRewardMessage(noRewardMessage);

            CoinMessageDecorator partyPenaltyMessage = new CoinMessageDecorator(new DefaultMessage(PARTY_PENALTY_MESSAGE, config.getBoolean("System.Messages.Output")));
            partyPenaltyMessage.setLoggingEnabled(config.getBoolean("System.Messages.LogCoinRewards", true));
            chance.setPartyRewardMessage(partyPenaltyMessage);

            CoinMessageDecorator partyRewardMessage = new CoinMessageDecorator(new DefaultMessage(PARTY_REWARD_MESSAGE, config.getBoolean("System.Messages.Output")));
            partyRewardMessage.setLoggingEnabled(config.getBoolean("System.Messages.LogCoinRewards", true));
            chance.setPartyRewardMessage(partyRewardMessage);

            NoCoinMessageDecorator partyNoRewardMessage = new NoCoinMessageDecorator(new DefaultMessage(PARTY_NO_REWARD_MESSAGE, config.getBoolean("System.Messages.Output")));
            partyNoRewardMessage.setNoRewardMessageEnabled(config.getBoolean("System.Messages.NoReward"));
            chance.setPartyNoRewardMessage(partyNoRewardMessage);

            chance.setIntegerCurrency(config.getBoolean("System.Economy.IntegerCurrency"));

            chances = new ArrayList<>();
            chances.add(chance);
        }

        return chances;
    }
}
