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
import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.CustomEntityDrop;
import se.crafted.chrisb.ecoCreature.drops.categories.types.CustomEntityType;

public class CustomEntityChance extends AbstractChance implements DropChance
{
    private final CustomEntityType type;

    public CustomEntityChance(CustomEntityType type, NumberRange range, double percentage)
    {
        this.type = type;
        setRange(range);
        setPercentage(percentage);
    }

    public Collection<CustomEntityType> nextEntityTypes()
    {
        Collection<CustomEntityType> types = new ArrayList<>();
        int amount = nextIntAmount();

        for (int i = 0; i < amount; i++) {
            types.add(type);
        }

        return types;
    }

    @Override
    public AbstractDrop nextDrop(String name, Location location, int lootLevel)
    {
        CustomEntityDrop drop = new CustomEntityDrop(name, location);
        drop.setCustomEntityTypes(nextEntityTypes());
        return drop;
    }

    public static Collection<CustomEntityChance> parseConfig(ConfigurationSection config)
    {
        Collection<CustomEntityChance> chances = Collections.emptyList();

        if (config != null) {
            chances = new ArrayList<>();

            if (config.getList("Drops") != null) {
                Collection<String> dropsList = config.getStringList("Drops");
                chances.addAll(parseChances(dropsList));
            }
            else {
                chances.addAll(parseChances(config.getString("Drops")));
            }
        }

        return chances;
    }

    private static Collection<CustomEntityChance> parseChances(String dropsString)
    {
        Collection<CustomEntityChance> chances = Collections.emptyList();

        if (dropsString != null && !dropsString.isEmpty()) {
            chances = parseChances(Arrays.asList(dropsString.split(";")));
        }

        return chances;
    }

    private static Collection<CustomEntityChance> parseChances(Collection<String> dropsList)
    {
        Collection<CustomEntityChance> chances = new ArrayList<>();

        for (String dropString : dropsList) {
            CustomEntityChance chance = createEntityChance(dropString);
            if (chance != null) {
                chances.add(chance);
            }
        }

        return chances;
    }

    private static CustomEntityChance createEntityChance(String dropString)
    {
        CustomEntityChance chance = null;

        CustomEntityType type = parseType(dropString);
        if (type != null && type.isValid()) {
            chance = new CustomEntityChance(type, parseRange(dropString), parsePercentage(dropString));
        }

        return chance;
    }

    protected static CustomEntityType parseType(String dropString)
    {
        CustomEntityType type = null;

        if (dropString != null) {
            String[] dropParts = dropString.split(":");
            String[] itemParts = dropParts[0].split(",");
            String[] itemSubParts = itemParts[0].split("\\.");

            type = CustomEntityType.fromName(itemSubParts[0]);
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
