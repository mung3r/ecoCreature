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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.CoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.messages.NoCoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.rewards.rules.AbstractRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.BattleArenaRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.CreativeModeRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.HeroClassNoDropRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.MobArenaRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.MurderedPetRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.ProjectileRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.Rule;
import se.crafted.chrisb.ecoCreature.rewards.rules.SpawnerDistanceRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.SpawnerMobRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.TamedCreatureRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.TownyRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.UnderSeaLevelRule;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;

public abstract class AbstractRewardSettings<T>
{
    private Map<T, List<AbstractRewardSource>> sources;

    private static Random random = new Random();

    public AbstractRewardSettings(Map<T, List<AbstractRewardSource>> sources)
    {
        this.sources = sources;
    }

    public Map<T, List<AbstractRewardSource>> getSources()
    {
        return sources;
    }

    public abstract boolean hasRewardSource(Event event);

    public abstract AbstractRewardSource getRewardSource(Event event);

    protected boolean hasRewardSource(T type)
    {
        return type != null && getSources().containsKey(type) && !getSources().get(type).isEmpty();
    }

    protected AbstractRewardSource getRewardSource(T type)
    {
        AbstractRewardSource source = hasRewardSource(type) ? getSources().get(type).get(nextInt(getSources().get(type).size())) : null;
        LoggerUtil.getInstance().debugTrue("No reward defined for type: " + type, source == null);

        return source;
    }

    protected boolean isRuleBroken(EntityKilledEvent event, Collection<Rule> rules)
    {
        for (Rule rule : rules) {
            if (rule.isBroken(event)) {
                if (rule.isClearDrops()) {
                    event.getDrops().clear();
                }
                if (rule.isClearDrops() || rule.isClearExpOrbs()) {
                    event.setDroppedExp(0);
                }

                Map<MessageToken, String> parameters = Collections.emptyMap();
                MessageHandler message = new MessageHandler(rule.getMessage(), parameters);
                message.send(event.getKiller());

                return true;
            }
        }

        return false;
    }

    protected static int nextInt(int n)
    {
        return random.nextInt(n);
    }

    protected static AbstractRewardSource mergeSets(AbstractRewardSource source, String rewardSection, ConfigurationSection config)
    {
        AbstractRewardSource newSource = source;
        ConfigurationSection rewardConfig = config.getConfigurationSection(rewardSection);
        ConfigurationSection rewardSets = config.getConfigurationSection("RewardSets");
        List<String> sets = rewardConfig.getStringList("Sets");

        if (!sets.isEmpty() && rewardSets != null) {
            for (String setName : sets) {
                if (rewardSets.getConfigurationSection(setName) != null) {
                    AbstractRewardSource setSource = RewardSourceFactory.createSetSource(setName, rewardSets);
                    setSource.setHuntingRules(loadHuntingRules(rewardSets.getConfigurationSection(setName)));
                    setSource.merge(newSource);
                    newSource = setSource;
                }
            }
        }

        return newSource;
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

    protected static Map<Class<? extends AbstractRule>, Rule> loadHuntingRules(ConfigurationSection config)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = new HashMap<Class<? extends AbstractRule>, Rule>();

        rules.putAll(CreativeModeRule.parseConfig(config));
        rules.putAll(MobArenaRule.parseConfig(config));
        rules.putAll(BattleArenaRule.parseConfig(config));
        rules.putAll(MurderedPetRule.parseConfig(config));
        rules.putAll(ProjectileRule.parseConfig(config));
        rules.putAll(SpawnerDistanceRule.parseConfig(config));
        rules.putAll(SpawnerMobRule.parseConfig(config));
        rules.putAll(TamedCreatureRule.parseConfig(config));
        rules.putAll(UnderSeaLevelRule.parseConfig(config));
        rules.putAll(HeroClassNoDropRule.parseConfig(config));

        return rules;
    }

    protected static Map<Class<? extends AbstractRule>, Rule> loadGainRules(ConfigurationSection config)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = new HashMap<Class<? extends AbstractRule>, Rule>();

        rules.putAll(TownyRule.parseConfig(config));

        return rules;
    }
}
