package se.crafted.chrisb.ecoCreature.drops.models;

import org.apache.commons.lang.math.NumberRange;

public interface Drop
{
    NumberRange getRange();

    void setRange(NumberRange range);

    double getPercentage();

    void setPercentage(double percentage);

    boolean nextWinner();

    int nextIntAmount();

    int getFixedAmount();

    double nextDoubleAmount();
}
