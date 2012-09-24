package se.crafted.chrisb.ecoCreature.rewards.models;

import java.util.List;


public interface ItemReward
{
    boolean hasItemDrops();

    List<ItemDrop> getItemDrops();

    void setItemDrops(List<ItemDrop> itemDrops);

    Boolean isFixedDrops();

    void setFixedDrops(Boolean fixedDrops);
}
