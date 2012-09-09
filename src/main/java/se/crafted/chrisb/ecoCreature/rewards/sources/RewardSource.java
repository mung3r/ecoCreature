package se.crafted.chrisb.ecoCreature.rewards.sources;

import java.util.List;

import org.bukkit.Location;

import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.rewards.Reward;

public interface RewardSource
{
    String getName();

    void setName(String name);

    void setType(RewardSourceType type);

    RewardSourceType getType();

    boolean hasCoin();

    Coin getCoin();

    void setCoin(Coin coin);

    boolean hasDrops();

    List<Drop> getDrops();

    void setDrops(List<Drop> drops);

    boolean hasExp();

    Exp getExp();

    void setExp(Exp exp);

    Message getNoCoinRewardMessage();

    void setNoCoinRewardMessage(Message noCoinRewardMessage);

    Message getCoinRewardMessage();

    void setCoinRewardMessage(Message coinRewardMessage);

    Message getCoinPenaltyMessage();

    void setCoinPenaltyMessage(Message coinPenaltyMessage);

    Boolean isFixedDrops();

    void setFixedDrops(Boolean fixedDrops);

    Boolean isIntegerCurrency();

    void setIntegerCurrency(Boolean integerCurrency);

    boolean isOverrideDrops();

    void setOverrideDrops(boolean overrideDrops);

    Reward getOutcome(Location location);
}
