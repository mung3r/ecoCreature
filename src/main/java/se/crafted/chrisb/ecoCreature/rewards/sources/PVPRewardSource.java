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
package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.settings.types.CustomRewardType;

public class PVPRewardSource extends AbstractRewardSource
{
    private static final String PVP_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for murdering &5<crt>&7.";

    private boolean percentReward;
    private double rewardAmount;

    public PVPRewardSource(ConfigurationSection config)
    {
        setName(CustomRewardType.LEGACY_PVP.getName());
        percentReward = config.getBoolean("System.Hunting.PVPRewardType", true);
        rewardAmount = config.getDouble("System.Hunting.PVPRewardAmount", 0.05D);
        setCoinRewardMessage(new DefaultMessage(config.getString("System.Messages.PVPRewardMessage", PVP_REWARD_MESSAGE)));
    }

    public boolean isPercentReward()
    {
        return percentReward;
    }

    public void setPercentReward(boolean percentReward)
    {
        this.percentReward = percentReward;
    }

    public double getRewardAmount()
    {
        return rewardAmount;
    }

    public void setRewardAmount(double rewardAmount)
    {
        this.rewardAmount = rewardAmount;
    }

    @Override
    protected Location getLocation(Event event)
    {
        if (event instanceof PlayerKilledEvent) {
            return ((PlayerKilledEvent) event).getVictim().getLocation();
        }
        else {
            throw new IllegalArgumentException("Unrecognized event");
        }
    }

    @Override
    public Reward createReward(Event event)
    {
        Reward reward = new Reward(getLocation(event));

        reward.setName(getName());

        if (percentReward && event instanceof PlayerKilledEvent && DependencyUtils.hasEconomy()) {
            Player victim = ((PlayerKilledEvent) event).getVictim();
            reward.setCoin(DependencyUtils.getEconomy().getBalance(victim.getName()));
            reward.setGain(rewardAmount / 100.0);
        }
        else {
            reward.setCoin(rewardAmount);
        }

        reward.setMessage(getCoinRewardMessage());
        reward.setIntegerCurrency(isIntegerCurrency());

        return reward;
    }
}
