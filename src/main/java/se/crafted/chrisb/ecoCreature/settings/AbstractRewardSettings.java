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
package se.crafted.chrisb.ecoCreature.settings;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.messages.CoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.rewards.rules.CreativeModeRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.MobArenaRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.MurderedPetRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.ProjectileRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.Rule;
import se.crafted.chrisb.ecoCreature.rewards.rules.SpawnerDistanceRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.SpawnerMobRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.TamedCreatureRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.UnderSeaLevelRule;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.CustomRewardType;

public abstract class AbstractRewardSettings
{
    private static Random random = new Random();

    public int nextInt(int n)
    {
        return random.nextInt(n);
    }

    public abstract boolean hasRewardSource(Event event);

    public abstract AbstractRewardSource getRewardSource(Event event);

    protected static AbstractRewardSource mergeSets(AbstractRewardSource source, ConfigurationSection rewardConfig, ConfigurationSection rewardSets)
    {
        List<String> sets = rewardConfig.getStringList("Sets");

        if (!sets.isEmpty() && rewardSets != null) {
            for (String setName : sets) {
                if (rewardSets.getConfigurationSection(setName) != null) {
                    AbstractRewardSource setSource = RewardSourceFactory.createSource(CustomRewardType.SET.getName(), rewardSets.getConfigurationSection(setName));
                    source = mergeRewardSource(source, setSource);
                }
            }
        }

        return source;
    }

    private static AbstractRewardSource mergeRewardSource(AbstractRewardSource from, AbstractRewardSource to)
    {
        to.setItemDrops(from.hasItemDrops() ? from.getItemDrops() : to.getItemDrops());
        to.setEntityDrops(from.hasEntityDrops() ? from.getEntityDrops() : to.getEntityDrops());
        to.setCoin(from.hasCoin() ? from.getCoin() : to.getCoin());

        to.setNoCoinRewardMessage(from.getNoCoinRewardMessage() != null ? from.getNoCoinRewardMessage() : to.getNoCoinRewardMessage());
        to.setCoinRewardMessage(from.getCoinRewardMessage() != null ? from.getCoinRewardMessage() : to.getCoinRewardMessage());
        to.setCoinPenaltyMessage(from.getCoinPenaltyMessage() != null ? from.getCoinPenaltyMessage() : to.getCoinPenaltyMessage());

        return to;
    }

    protected static AbstractRewardSource configureRewardSource(AbstractRewardSource source, ConfigurationSection config)
    {
        if (source != null && config != null) {
            source.setIntegerCurrency(config.getBoolean("System.Economy.IntegerCurrency", false));
            source.setFixedDrops(config.getBoolean("System.Hunting.FixedDrops", false));

            source.setCoinRewardMessage(configureMessage(source.getCoinRewardMessage(), config));
            source.setCoinPenaltyMessage(configureMessage(source.getCoinPenaltyMessage(), config));
            source.setNoCoinRewardMessage(configureMessage(source.getNoCoinRewardMessage(), config));
        }

        return source;
    }

    private static Message configureMessage(Message message, ConfigurationSection config)
    {
        if (message != null && config != null) {
            message.setMessageOutputEnabled(config.getBoolean("System.Messages.Output", true));
            if (message instanceof CoinMessageDecorator) {
                ((CoinMessageDecorator) message).setCoinLoggingEnabled(config.getBoolean("System.Messages.LogCoinRewards", true));
            }
            if (message instanceof NoCoinMessageDecorator) {
                ((NoCoinMessageDecorator) message).setNoRewardMessageEnabled(config.getBoolean("System.Messages.NoReward", false));
            }
        }

        return message;
    }

    protected static Set<Rule> loadHuntingRules(ConfigurationSection config)
    {
        Set<Rule> rules = new HashSet<Rule>();

        rules.addAll(CreativeModeRule.parseConfig(config));
        rules.addAll(MobArenaRule.parseConfig(config));
        rules.addAll(MurderedPetRule.parseConfig(config));
        rules.addAll(ProjectileRule.parseConfig(config));
        rules.addAll(SpawnerDistanceRule.parseConfig(config));
        rules.addAll(SpawnerMobRule.parseConfig(config));
        rules.addAll(TamedCreatureRule.parseConfig(config));
        rules.addAll(UnderSeaLevelRule.parseConfig(config));

        return rules;
    }
}
