package se.crafted.chrisb.ecoCreature.rewards.models;

import se.crafted.chrisb.ecoCreature.messages.Message;

public interface CoinReward
{
    boolean hasCoin();

    CoinDrop getCoin();

    void setCoin(CoinDrop coin);

    Message getNoCoinRewardMessage();

    void setNoCoinRewardMessage(Message noCoinRewardMessage);

    Message getCoinRewardMessage();

    void setCoinRewardMessage(Message coinRewardMessage);

    Message getCoinPenaltyMessage();

    void setCoinPenaltyMessage(Message coinPenaltyMessage);

    Boolean isIntegerCurrency();

    void setIntegerCurrency(Boolean integerCurrency);
}
