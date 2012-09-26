package se.crafted.chrisb.ecoCreature.rewards.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;

public class EntityDrop
{
    private static Random random = new Random();

    private EntityType type;
    private IntRange range;
    private double percentage;

    public EntityType getType()
    {
        return type;
    }

    public void setType(EntityType type)
    {
        this.type = type;
    }

    public IntRange getRange()
    {
        return range;
    }

    public void setRange(IntRange range)
    {
        this.range = range;
    }

    public double getPercentage()
    {
        return percentage;
    }

    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
    }

    public List<EntityType> getOutcome()
    {
        List<EntityType> types = new ArrayList<EntityType>();

        for (int i = 0; i < nextAmount(); i++) {
            types.add(type);
        }

        return types;
    }

    private int nextAmount()
    {
        int amount;

        if (Math.random() > percentage / 100.0D) {
            amount = 0;
        }
        else {
            if (range.getMinimumInteger() == range.getMaximumInteger()) {
                amount = range.getMinimumInteger();
            }
            else if (range.getMinimumInteger() > range.getMaximumInteger()) {
                amount = range.getMinimumInteger();
            }
            else {
                amount = range.getMinimumInteger() + random.nextInt(range.getMaximumInteger() - range.getMinimumInteger() + 1);
            }
        }

        return amount;
    }

    public static List<EntityDrop> parseConfig(ConfigurationSection config)
    {
        List<EntityDrop> drops = Collections.emptyList();

        if (config != null) {
            drops = new ArrayList<EntityDrop>();

            if (config.getList("Drops") != null) {
                List<String> dropsList = config.getStringList("Drops");
                drops.addAll(EntityDrop.parseDrops(dropsList));
            }
            else {
                drops.addAll(EntityDrop.parseDrops(config.getString("Drops")));
            }

            // NOTE: backward compatibility
            EntityDrop exp = parseExpConfig(config);
            if (exp != null) {
                drops.add(exp);
            }
        }

        return drops;
    }

    private static EntityDrop parseExpConfig(ConfigurationSection config)
    {
        EntityDrop exp = null;

        if (config != null && config.contains("ExpMin") && config.contains("ExpMax") && config.contains("ExpPercent")) {
            exp = new EntityDrop();
            exp.setType(EntityType.EXPERIENCE_ORB);
            exp.setRange(new IntRange(config.getInt("ExpMin", 0), config.getInt("ExpMax", 0)));
            exp.setPercentage(config.getDouble("ExpPercent", 0.0D));
        }

        return exp;
    }

    private static List<EntityDrop> parseDrops(String dropsString)
    {
        List<EntityDrop> drops = Collections.emptyList();

        if (dropsString != null && !dropsString.isEmpty()) {
            drops = parseDrops(Arrays.asList(dropsString.split(";")));
        }

        return drops;
    }

    private static List<EntityDrop> parseDrops(List<String> dropsList)
    {
        List<EntityDrop> drops = Collections.emptyList();

        for (String dropString : dropsList) {
            EntityType type = parseType(dropString);
            if (type != null && !isAmbiguous(type)) {
                EntityDrop drop = new EntityDrop();
                drop.setType(type);
                drop.setRange(parseRange(dropString));
                drop.setPercentage(parsePercentage(dropString));
                drops = new ArrayList<EntityDrop>();
                drops.add(drop);
            }
        }

        return drops;
    }

    private static boolean isAmbiguous(EntityType type)
    {
        return Material.matchMaterial(type.getName()) != null;
    }

    private static EntityType parseType(String dropString)
    {
        String[] dropParts = dropString.split(":");
        String[] itemParts = dropParts[0].split(",");
        String[] itemSubParts = itemParts[0].split("\\.");

        EntityType type = EntityType.fromName(itemSubParts[0]);
        if (type == null) {
            ECLogger.getInstance().debug("Invalid entity type: " + itemParts[0]);
        }

        return type;
    }

    private static IntRange parseRange(String dropString)
    {
        String[] dropParts = dropString.split(":");
        String[] amountRange = dropParts[1].split("-");

        int min = 0;
        int max = 0;

        if (amountRange.length == 2) {
            min = Integer.parseInt(amountRange[0]);
            max = Integer.parseInt(amountRange[1]);
        }
        else {
            max = Integer.parseInt(dropParts[1]);
        }

        return new IntRange(min, max);
    }

    private static double parsePercentage(String dropString)
    {
        String[] dropParts = dropString.split(":");

        return Double.parseDouble(dropParts[2]);
    }
}
