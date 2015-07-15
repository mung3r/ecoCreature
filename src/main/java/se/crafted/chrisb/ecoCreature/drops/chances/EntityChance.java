/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
 * ecoCreature is licensed under the GNU Lesser General Public License.
 *
 * ecoCreature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ecoCreature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.crafted.chrisb.ecoCreature.drops.chances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.EntityDrop;

public class EntityChance extends AbstractChance implements DropChance
{
    private final EntityType type;

    public EntityChance(EntityType type, NumberRange range, double percentage)
    {
        this.type = type;
        setRange(range);
        setPercentage(percentage);
    }

    public Collection<EntityType> nextEntityTypes()
    {
        Collection<EntityType> types = new ArrayList<>();
        int amount = nextIntAmount();

        for (int i = 0; i < amount; i++) {
            types.add(type);
        }

        return types;
    }

    @Override
    public AbstractDrop nextDrop(String name, Location location, int lootLevel)
    {
        EntityDrop drop = new EntityDrop(name, location);
        drop.setEntityTypes(nextEntityTypes());
        return drop;
    }

    public static Collection<DropChance> parseConfig(ConfigurationSection config)
    {
        Collection<DropChance> chances = Collections.emptyList();

        if (config != null) {
            chances = new ArrayList<>();

            if (config.getList("Drops") != null) {
                Collection<String> dropsList = config.getStringList("Drops");
                chances.addAll(parseChances(dropsList));
            }
            else {
                chances.addAll(parseChances(config.getString("Drops")));
            }

            // NOTE: backward compatibility
            EntityChance chance = parseExpChance(config);
            if (chance != null) {
                chances.add(chance);
            }
        }

        return chances;
    }

    private static EntityChance parseExpChance(ConfigurationSection config)
    {
        EntityChance chance = null;

        if (config != null && config.contains("ExpMin") && config.contains("ExpMax") && config.contains("ExpPercent")) {
            chance = new EntityChance(EntityType.EXPERIENCE_ORB, new NumberRange(config.getInt("ExpMin", 0), config.getInt("ExpMax", 0)), config.getDouble(
                    "ExpPercent", 0.0D));
        }

        return chance;
    }

    private static Collection<EntityChance> parseChances(String dropsString)
    {
        Collection<EntityChance> chances = Collections.emptyList();

        if (dropsString != null && !dropsString.isEmpty()) {
            chances = parseChances(Arrays.asList(dropsString.split(";")));
        }

        return chances;
    }

    private static Collection<EntityChance> parseChances(Collection<String> dropsList)
    {
        Collection<EntityChance> chances = new ArrayList<>();

        for (String dropString : dropsList) {
            EntityChance chance = createEntityChance(dropString);
            if (chance != null) {
                chances.add(chance);
            }
        }

        return chances;
    }

    private static EntityChance createEntityChance(String dropString)
    {
        EntityChance chance = null;

        EntityType type = parseType(dropString);
        if (type != null) {
            chance = new EntityChance(type, parseRange(dropString), parsePercentage(dropString));
        }

        return chance;
    }

    protected static boolean isNotAmbiguous(EntityType type)
    {
        return type != null && Material.matchMaterial(type.getName()) == null;
    }

    protected static EntityType parseType(String dropString)
    {
        EntityType type = null;

        if (dropString != null) {
            String[] dropParts = dropString.split(":");
            String[] itemParts = dropParts[0].split(",");
            String[] itemSubParts = itemParts[0].split("\\.");

            type = EntityType.fromName(itemSubParts[0]);
        }

        return type;
    }

    protected static NumberRange parseRange(String dropString)
    {
        String[] dropParts = dropString.split(":");
        String[] amountRange = dropParts[1].split("-");

        int min = 0;
        int max;

        if (amountRange.length == 2) {
            min = Integer.parseInt(amountRange[0]);
            max = Integer.parseInt(amountRange[1]);
        }
        else {
            max = Integer.parseInt(dropParts[1]);
        }

        return new NumberRange(min, max);
    }

    protected static double parsePercentage(String dropString)
    {
        String[] dropParts = dropString.split(":");

        return Double.parseDouble(dropParts[2]);
    }
}
