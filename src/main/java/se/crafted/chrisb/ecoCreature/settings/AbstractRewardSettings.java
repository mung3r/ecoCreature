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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.CoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.messages.NoCoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.rewards.rules.BattleArenaRule;
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

public abstract class AbstractRewardSettings<T>
{
    private Map<T, List<AbstractRewardSource>> sources;
    private Set<Rule> huntingRules;

    private static Random random = new Random();

    public AbstractRewardSettings(Map<T, List<AbstractRewardSource>> sources)
    {
        this.sources = sources;
        huntingRules = Collections.emptySet();
    }

    public Map<T, List<AbstractRewardSource>> getSources()
    {
        return sources;
    }

    public void setHuntingRules(Set<Rule> huntingRules)
    {
        this.huntingRules = huntingRules;
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

        if (source == null) {
            LoggerUtil.getInstance().debug("No reward defined for type: " + type);
        }

        return source;
    }

    protected boolean isRuleBroken(EntityKilledEvent event)
    {
        for (Rule rule : huntingRules) {
            if (rule.isBroken(event)) {
                if (rule.isClearDrops()) {
                    event.getDrops().clear();
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

    protected static AbstractRewardSource mergeSets(AbstractRewardSource source, ConfigurationSection rewardConfig, ConfigurationSection rewardSets)
    {
        AbstractRewardSource newSource = source;
        List<String> sets = rewardConfig.getStringList("Sets");

        if (!sets.isEmpty() && rewardSets != null) {
            for (String setName : sets) {
                if (rewardSets.getConfigurationSection(setName) != null) {
                    AbstractRewardSource setSource = RewardSourceFactory.createSource(CustomRewardType.SET.toString(), rewardSets.getConfigurationSection(setName));
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

    protected static Set<Rule> loadHuntingRules(ConfigurationSection config)
    {
        Set<Rule> rules = new HashSet<Rule>();

        rules.addAll(CreativeModeRule.parseConfig(config));
        rules.addAll(MobArenaRule.parseConfig(config));
        rules.addAll(BattleArenaRule.parseConfig(config));
        rules.addAll(MurderedPetRule.parseConfig(config));
        rules.addAll(ProjectileRule.parseConfig(config));
        rules.addAll(SpawnerDistanceRule.parseConfig(config));
        rules.addAll(SpawnerMobRule.parseConfig(config));
        rules.addAll(TamedCreatureRule.parseConfig(config));
        rules.addAll(UnderSeaLevelRule.parseConfig(config));

        return rules;
    }
}
