package se.crafted.chrisb.ecoCreature.rewards.models;

import java.util.List;


public interface EntityReward
{
    boolean hasEntityDrops();

    List<EntityDrop> getEntityDrops();

    void setEntityDrops(List<EntityDrop> entityDrops);
}
