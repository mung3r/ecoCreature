package se.crafted.chrisb.ecoCreature.drops.chances;

import org.bukkit.Location;

import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;

public interface DropChance extends Chance
{
    AbstractDrop nextDrop(String name, Location location, int lootLevel);
}
