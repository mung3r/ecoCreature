package se.crafted.chrisb.ecoCreature.drops.chances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.Message;
import nl.arfie.bukkit.attributes.Attribute;
import nl.arfie.bukkit.attributes.AttributeType;

public class AttributeChance extends AbstractChance
{

    private static final String ATTACK_DAMAGE_MESSAGE = "&6<amt>&7 Damage";
    private static final String FOLLOW_RANGE_MESSAGE = "&6<amt>&7 Follow Range";
    private static final String JUMP_STRENGTH_MESSAGE = "&6<amt>&7 Jump";
    private static final String KNOCKBACK_RESISTANCE_MESSAGE = "&6<amt>&7 Knockback Resist";
    private static final String MAX_HEALTH_MESSAGE = "&6<amt>&7 Health";
    private static final String MOVEMENT_SPEED_MESSAGE = "&6<amt>&7 Movement Speed";
    private static final String SPAWN_REINFORCEMENTS_MESSAGE = "&6<amt>&7 Reinforcements";

    public static final Map<AttributeType, Message> LORE_MAP = new HashMap<>();

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

    public static Collection<AttributeChance> parseConfig(String section, ConfigurationSection config)
    {
        Collection<AttributeChance> chances = Collections.emptyList();

        LORE_MAP.put(AttributeType.ATTACK_DAMAGE, new DefaultMessage(config.getString("System.Messages.Attributes.ATTACK_DAMAGE", ATTACK_DAMAGE_MESSAGE)));
        LORE_MAP.put(AttributeType.FOLLOW_RANGE, new DefaultMessage(config.getString("System.Messages.Attributes.FOLLOW_RANGE", FOLLOW_RANGE_MESSAGE)));
        LORE_MAP.put(AttributeType.JUMP_STRENGTH, new DefaultMessage(config.getString("System.Messages.Attributes.JUMP_STRENGTH", JUMP_STRENGTH_MESSAGE)));
        LORE_MAP.put(AttributeType.KNOCKBACK_RESISTANCE, new DefaultMessage(config.getString("System.Messages.Attributes.KNOCKBACK_RESISTANCE", KNOCKBACK_RESISTANCE_MESSAGE)));
        LORE_MAP.put(AttributeType.MAX_HEALTH, new DefaultMessage(config.getString("System.Messages.Attributes.MAX_HEALTH", MAX_HEALTH_MESSAGE)));
        LORE_MAP.put(AttributeType.MOVEMENT_SPEED, new DefaultMessage(config.getString("System.Messages.Attributes.MOVEMENT_SPEED", MOVEMENT_SPEED_MESSAGE)));
        LORE_MAP.put(AttributeType.SPAWN_REINFORCEMENTS, new DefaultMessage(config.getString("System.Messages.Attributes.SPAWN_REINFORCEMENTS", SPAWN_REINFORCEMENTS_MESSAGE)));

        return chances;
    }
}
