package se.crafted.chrisb.ecoCreature.drops.models;

import java.util.Random;

import org.apache.commons.lang.math.NumberRange;

public interface Drop
{
    NumberRange getRange();

    void setRange(NumberRange range);

    double getPercentage();

    void setPercentage(double percentage);

    double getChance();

    Random getRandom();
}
