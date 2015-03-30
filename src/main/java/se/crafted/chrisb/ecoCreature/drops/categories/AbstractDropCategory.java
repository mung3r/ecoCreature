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
package se.crafted.chrisb.ecoCreature.drops.categories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.drops.rules.AbstractRule;
import se.crafted.chrisb.ecoCreature.drops.rules.BattleArenaRule;
import se.crafted.chrisb.ecoCreature.drops.rules.CreativeModeRule;
import se.crafted.chrisb.ecoCreature.drops.rules.HeroesRule;
import se.crafted.chrisb.ecoCreature.drops.rules.MobArenaRule;
import se.crafted.chrisb.ecoCreature.drops.rules.MurderedPetRule;
import se.crafted.chrisb.ecoCreature.drops.rules.ProjectileRule;
import se.crafted.chrisb.ecoCreature.drops.rules.Rule;
import se.crafted.chrisb.ecoCreature.drops.rules.SimpleClansRule;
import se.crafted.chrisb.ecoCreature.drops.rules.SpawnerDistanceRule;
import se.crafted.chrisb.ecoCreature.drops.rules.SpawnerMobRule;
import se.crafted.chrisb.ecoCreature.drops.rules.TamedCreatureRule;
import se.crafted.chrisb.ecoCreature.drops.rules.TownyRule;
import se.crafted.chrisb.ecoCreature.drops.rules.UnderSeaLevelRule;
import se.crafted.chrisb.ecoCreature.drops.sources.AbstractDropSource;
import se.crafted.chrisb.ecoCreature.drops.sources.DropSourceFactory;
import se.crafted.chrisb.ecoCreature.messages.CoinMessageDecorator;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageHandler;
import se.crafted.chrisb.ecoCreature.messages.MessageToken;
import se.crafted.chrisb.ecoCreature.messages.NoCoinMessageDecorator;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

public abstract class AbstractDropCategory<T>
{
    private Map<T, Collection<AbstractDropSource>> dropSourceMap = Collections.emptyMap();

    public AbstractDropCategory(Map<T, Collection<AbstractDropSource>> dropSourceMap)
    {
        if (dropSourceMap != null) {
            this.dropSourceMap = dropSourceMap;
        }
    }

    public Collection<AbstractDropSource> getDropSources(final Event event)
    {
        Collection<AbstractDropSource> dropSources = Collections.emptyList();

        if (isValidEvent(event)) {
            dropSources = Collections2.filter(getDropSources(extractType(event)), new Predicate<AbstractDropSource>() {

                @Override
                public boolean apply(AbstractDropSource source)
                {
                    return source.hasPermission(extractPlayer(event)) && isNotRuleBroken(event, source);
                }

            });
        }

        return dropSources;
    }

    protected abstract boolean isValidEvent(Event event);

    protected abstract T extractType(Event event);

    protected abstract Player extractPlayer(Event event);

    private boolean hasDropSource(T type)
    {
        return type != null && dropSourceMap.containsKey(type) && !dropSourceMap.get(type).isEmpty();
    }

    private Collection<AbstractDropSource> getDropSources(T type)
    {
        Collection<AbstractDropSource> dropSources = Collections.emptyList();

        if (hasDropSource(type)) {
            dropSources = dropSourceMap.get(type);
        }

        if (type != null) {
            LoggerUtil.getInstance().debugTrue("No reward defined for type: " + type, dropSources.isEmpty());
        }
        return dropSources;
    }

    private boolean isNotRuleBroken(final Event event, AbstractDropSource dropSource)
    {
        return !Iterables.any(dropSource.getHuntingRules().values(), new Predicate<Rule>() {

            @Override
            public boolean apply(Rule rule)
            {
                if (rule.isBroken(event)) {
                    rule.handleDrops(event);

                    Map<MessageToken, String> parameters = Collections.emptyMap();
                    MessageHandler message = new MessageHandler(rule.getMessage(), parameters);
                    message.send(rule.getKiller(event));

                    LoggerUtil.getInstance().debug("Rule " + rule.getClass().getSimpleName() + " broken");
                    return true;
                }

                return false;
            }
        });
    }

    protected static Collection<AbstractDropSource> parseSets(String rewardSection, ConfigurationSection config)
    {
        Collection<AbstractDropSource> dropSources = new ArrayList<>();
        ConfigurationSection rewardConfig = config.getConfigurationSection(rewardSection);
        ConfigurationSection rewardSets = config.getConfigurationSection("RewardSets");
        Collection<String> sets = rewardConfig.getStringList("Sets");

        if (!sets.isEmpty() && rewardSets != null) {
            for (String setName : sets) {
                String name = setName.split(":")[0];

                if (rewardSets.getConfigurationSection(name) != null) {
                    Map<Class<? extends AbstractRule>, Rule> huntingRules = loadHuntingRules(rewardSets.getConfigurationSection(name));
                    NumberRange range = parseRange(setName);
                    double percentage = parsePercentage(setName);

                    for (AbstractDropSource dropSource : DropSourceFactory.createSetSources(name, rewardSets)) {
                        dropSource.setHuntingRules(huntingRules);
                        dropSource.setRange(range);
                        dropSource.setPercentage(percentage);
                        dropSources.add(dropSource);
                    }
                }
            }
        }

        return dropSources;
    }

    private static NumberRange parseRange(String dropString)
    {
        NumberRange range = new NumberRange(1, 1);

        String[] dropParts = dropString.split(":");

        if (dropParts.length > 1) {
            String[] rangeParts = dropParts[1].split("-");

            if (rangeParts.length == 2) {
                range = new NumberRange(Integer.parseInt(rangeParts[0]), Integer.parseInt(rangeParts[1]));
            }
            else {
                range = new NumberRange(0, Integer.parseInt(dropParts[1]));
            }
        }

        return range;
    }

    private static double parsePercentage(String dropString)
    {
        double percentage = 100.0D;

        String[] dropParts = dropString.split(":");

        if (dropParts.length > 2) {
            percentage = Double.parseDouble(dropParts[2]);
        }

        return percentage;
    }

    protected static Collection<AbstractDropSource> configureDropSources(Collection<AbstractDropSource> dropSources, ConfigurationSection config)
    {
        if (dropSources != null && config != null) {
            for (AbstractDropSource dropSource : dropSources) {
                dropSource.setIntegerCurrency(config.getBoolean("System.Economy.IntegerCurrency"));
                dropSource.setFixedAmount(config.getBoolean("System.Hunting.FixedDrops"));

                dropSource.setCoinRewardMessage(configureMessage(dropSource.getCoinRewardMessage(), config));
                dropSource.setCoinPenaltyMessage(configureMessage(dropSource.getCoinPenaltyMessage(), config));
                dropSource.setNoCoinRewardMessage(configureMessage(dropSource.getNoCoinRewardMessage(), config));
            }
        }

        return dropSources;
    }

    private static Message configureMessage(Message message, ConfigurationSection config)
    {
        if (message != null && config != null) {
            message.setEnabled(config.getBoolean("System.Messages.Output", true));
            if (message instanceof CoinMessageDecorator) {
                ((CoinMessageDecorator) message).setLoggingEnabled(config.getBoolean("System.Messages.LogCoinRewards", true));
            }
            if (message instanceof NoCoinMessageDecorator) {
                ((NoCoinMessageDecorator) message).setNoRewardMessageEnabled(config.getBoolean("System.Messages.NoReward"));
            }
        }

        return message;
    }

    protected static Map<Class<? extends AbstractRule>, Rule> loadHuntingRules(ConfigurationSection config)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = new HashMap<>();

        rules.putAll(CreativeModeRule.parseConfig(config));
        rules.putAll(MobArenaRule.parseConfig(config));
        rules.putAll(BattleArenaRule.parseConfig(config));
        rules.putAll(MurderedPetRule.parseConfig(config));
        rules.putAll(ProjectileRule.parseConfig(config));
        rules.putAll(SpawnerDistanceRule.parseConfig(config));
        rules.putAll(SpawnerMobRule.parseConfig(config));
        rules.putAll(TamedCreatureRule.parseConfig(config));
        rules.putAll(UnderSeaLevelRule.parseConfig(config));
        rules.putAll(HeroesRule.parseConfig(config));
        rules.putAll(SimpleClansRule.parseConfig(config));

        return rules;
    }

    protected static Map<Class<? extends AbstractRule>, Rule> loadGainRules(ConfigurationSection config)
    {
        Map<Class<? extends AbstractRule>, Rule> rules = new HashMap<>();

        rules.putAll(TownyRule.parseConfig(config));

        return rules;
    }
}
