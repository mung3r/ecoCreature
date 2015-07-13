package se.crafted.chrisb.ecoCreature.drops.chances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.arfie.bukkit.attributes.Attribute;
import nl.arfie.bukkit.attributes.AttributeType;

public class AttributeChance extends AbstractChance
{
    private final AttributeType type;

    public AttributeChance(AttributeType type)
    {
        this.type = type;
    }

    private AttributeType getType()
    {
        return type;
    }

    public static List<Attribute> nextAttributes(Collection<AttributeChance> chances)
    {
        List<Attribute> attributes = new ArrayList<>();

        for (AttributeChance chance : chances) {
            attributes.add(new Attribute(chance.getType(), chance.nextDoubleAmount()));
        }

        return attributes;
    }
}
