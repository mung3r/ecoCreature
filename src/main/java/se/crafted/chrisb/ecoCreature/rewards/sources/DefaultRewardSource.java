package se.crafted.chrisb.ecoCreature.rewards.sources;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;

import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.events.PlayerKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCoinRewardMessage;
import se.crafted.chrisb.ecoCreature.rewards.Reward;

public class DefaultRewardSource implements RewardSource
{
    protected static final String NO_COIN_REWARD_MESSAGE = "&7You slayed a &5<crt>&7 using a &3<itm>&7.";
    protected static final String COIN_REWARD_MESSAGE = "&7You are awarded &6<amt>&7 for slaying a &5<crt>&7.";
    protected static final String COIN_PENALTY_MESSAGE = "&7You are penalized &6<amt>&7 for slaying a &5<crt>&7.";

    private String name;
    private Coin coin;
    private List<Drop> drops;
    private Exp exp;

    private Message noCoinRewardMessage;
    private Message coinRewardMessage;
    private Message coinPenaltyMessage;

    private boolean fixedDrops;
    private boolean integerCurrency;
    private boolean overrideDrops;

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public boolean hasCoin()
    {
        return coin != null;
    }

    @Override
    public Coin getCoin()
    {
        return coin;
    }

    @Override
    public void setCoin(Coin coin)
    {
        this.coin = coin;
    }

    @Override
    public boolean hasDrops()
    {
        return drops != null;
    }

    @Override
    public List<Drop> getDrops()
    {
        return drops;
    }

    @Override
    public void setDrops(List<Drop> drops)
    {
        this.drops = drops;
    }

    @Override
    public boolean hasExp()
    {
        return exp != null;
    }

    @Override
    public Exp getExp()
    {
        return exp;
    }

    @Override
    public void setExp(Exp exp)
    {
        this.exp = exp;
    }

    @Override
    public Message getNoCoinRewardMessage()
    {
        return noCoinRewardMessage;
    }

    @Override
    public void setNoCoinRewardMessage(Message noCoinRewardMessage)
    {
        this.noCoinRewardMessage = noCoinRewardMessage;
    }

    @Override
    public Message getCoinRewardMessage()
    {
        return coinRewardMessage;
    }

    @Override
    public void setCoinRewardMessage(Message coinRewardMessage)
    {
        this.coinRewardMessage = coinRewardMessage;
    }

    @Override
    public Message getCoinPenaltyMessage()
    {
        return coinPenaltyMessage;
    }

    @Override
    public void setCoinPenaltyMessage(Message coinPenaltyMessage)
    {
        this.coinPenaltyMessage = coinPenaltyMessage;
    }

    @Override
    public Boolean isFixedDrops()
    {
        return fixedDrops;
    }

    @Override
    public void setFixedDrops(Boolean fixedDrops)
    {
        this.fixedDrops = fixedDrops;
    }

    @Override
    public Boolean isIntegerCurrency()
    {
        return integerCurrency;
    }

    @Override
    public void setIntegerCurrency(Boolean integerCurrency)
    {
        this.integerCurrency = integerCurrency;
    }

    @Override
    public boolean isOverrideDrops()
    {
        return overrideDrops;
    }

    @Override
    public void setOverrideDrops(boolean overrideDrops)
    {
        this.overrideDrops = overrideDrops;
    }

    @Override
    public Reward getOutcome(Event event)
    {
        Reward reward = new Reward(getLocation(event));

        reward.setName(name);
        reward.setDrops(getDropOutcomes(fixedDrops));

        if (hasCoin()) {
            reward.setCoin(coin.getOutcome());

            if (reward.getCoin() > 0.0) {
                reward.setMessage(coinRewardMessage);
            }
            else if (reward.getCoin() < 0.0) {
                reward.setMessage(coinPenaltyMessage);
            }
            else {
                reward.setMessage(noCoinRewardMessage);
            }
        }

        if (hasExp()) {
            reward.setExp(exp.getOutcome());
        }

        reward.setIntegerCurrency(integerCurrency);

        return reward;
    }

    protected static Location getLocation(Event event)
    {
        if (event instanceof BlockBreakEvent) {
            return ((BlockBreakEvent) event).getBlock().getLocation();
        }
        else if (event instanceof EntityKilledEvent) {
            return ((EntityKilledEvent) event).getEntity().getLocation();
        }
        else if (event instanceof HeroChangeLevelEvent) {
            return ((HeroChangeLevelEvent) event).getHero().getPlayer().getLocation();
        }
        else if (event instanceof PlayerKilledEvent) {
            return ((PlayerKilledEvent) event).getVictim().getLocation();
        }
        else if (event instanceof PlayerDeathEvent) {
            return ((PlayerDeathEvent) event).getEntity().getLocation();
        }
        else if (event instanceof DeathStreakEvent) {
            return ((DeathStreakEvent) event).getPlayer().getLocation();
        }
        else if (event instanceof KillStreakEvent) {
            return ((KillStreakEvent) event).getPlayer().getLocation();
        }

        return null;
    }

    private List<ItemStack> getDropOutcomes(boolean isFixedDrops)
    {
        List<ItemStack> stacks = new ArrayList<ItemStack>();

        if (drops != null) {
            for (Drop drop : drops) {
                ItemStack itemStack = drop.getOutcome(isFixedDrops);
                if (itemStack != null) {
                    stacks.add(itemStack);
                }
            }
        }

        return stacks;
    }

    public static RewardSource parseConfig(ConfigurationSection config)
    {
        DefaultRewardSource source = new DefaultRewardSource();

        if (config != null) {
            source.setName(config.getName());

            source.setDrops(Drop.parseConfig(config));
            source.setCoin(Coin.parseConfig(config));
            source.setExp(Exp.parseConfig(config));

            source.setCoinRewardMessage(new DefaultMessage(config.getString("Reward_Message", COIN_REWARD_MESSAGE)));
            source.setCoinPenaltyMessage(new DefaultMessage(config.getString("Penalty_Message", COIN_PENALTY_MESSAGE)));
            source.setNoCoinRewardMessage(new NoCoinRewardMessage(config.getString("NoReward_Message", NO_COIN_REWARD_MESSAGE)));
        }

        return source;
    }
}
