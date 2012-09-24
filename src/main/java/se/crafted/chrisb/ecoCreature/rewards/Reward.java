package se.crafted.chrisb.ecoCreature.rewards;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;

public class Reward
{
    private Location location;
    private double gain;
    private Set<String> party;
    private boolean integerCurrency;

    private String name;
    private double coin;
    private List<ItemStack> itemDrops;
    private List<EntityType> entityDrops;
    private Message message;

    public Reward(Location location)
    {
        this.location = location;
        gain = 1.0;
        party = Collections.emptySet();
        integerCurrency = false;

        name = "Unknown";
        coin = 0.0;
        itemDrops = Collections.emptyList();
        entityDrops = Collections.emptyList();
        message = new DefaultMessage();
    }

    public World getWorld()
    {
        World world = null;

        if (location != null) {
            world = location.getWorld();
        }

        return world;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public double getGain()
    {
        return gain;
    }

    public void setGain(double gain)
    {
        this.gain = gain;
    }

    public boolean hasParty()
    {
        return party.size() > 0;
    }

    public Set<String> getParty()
    {
        return party;
    }

    public void setParty(Set<String> party)
    {
        this.party = party;
    }

    public boolean isIntegerCurrency()
    {
        return integerCurrency;
    }

    public void setIntegerCurrency(boolean integerCurrency)
    {
        this.integerCurrency = integerCurrency;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getCoin()
    {
        return coin;
    }

    public void setCoin(double coin)
    {
        this.coin = coin;
    }

    public boolean hasDrops()
    {
        return !itemDrops.isEmpty() && !entityDrops.isEmpty();
    }

    public List<ItemStack> getItemDrops()
    {
        return itemDrops;
    }

    public void setItemDrops(List<ItemStack> itemDrops)
    {
        this.itemDrops = itemDrops;
    }

    public List<EntityType> getEntityDrops()
    {
        return entityDrops;
    }

    public void setEntityDrops(List<EntityType> entityDrops)
    {
        this.entityDrops = entityDrops;
    }

    public Message getMessage()
    {
        return message;
    }

    public void setMessage(Message message)
    {
        this.message = message;
    }
}
