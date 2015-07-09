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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.EntityType;

import se.crafted.chrisb.ecoCreature.drops.AbstractDrop;
import se.crafted.chrisb.ecoCreature.drops.JockeyDrop;

public class JockeyChance extends EntityChance
{
    private EntityType passenger;

    public JockeyChance(EntityType passenger, EntityType vehicle, NumberRange range, double percentage)
    {
        super(vehicle, range, percentage);
        this.passenger = passenger;
    }

    @Override
    public AbstractDrop nextDrop(String name, Location location, int lootLevel)
    {
        JockeyDrop drop = new JockeyDrop(name, location);
        drop.setEntityTypes(nextEntityTypes());
        return drop;
    }

    public static Collection<DropChance> parseConfig(ConfigurationSection config)
    {
        Collection<DropChance> chances = new ArrayList<>();

        if (config != null && config.getList("Drops") != null) {

            for (Object obj : config.getList("Drops")) {
                if (obj instanceof LinkedHashMap) {
                    ConfigurationSection memoryConfig = createMemoryConfig(obj);
                    String passengerString = memoryConfig.getString("passenger");
                    String vehicleString = memoryConfig.getString("vehicle");
                    JockeyChance chance = createJockeyChance(passengerString, vehicleString);
                    if (chance != null) {
                        chances.add(chance);
                    }
                }
            }
        }

        return chances;
    }

    @Override
    public Collection<EntityType> nextEntityTypes()
    {
        Collection<EntityType> types = new ArrayList<>();
        for (EntityType vehicle : super.nextEntityTypes()) {
            types.add(vehicle);
            types.add(passenger);
        }
        return types;
    }

    private static JockeyChance createJockeyChance(String passengerString, String vehicleString)
    {
        JockeyChance chance = null;

        EntityType passengerType = parseType(passengerString);
        EntityType vehicleType = parseType(vehicleString);

        if (isNotAmbiguous(vehicleType) && isNotAmbiguous(passengerType)) {
            chance = new JockeyChance(passengerType, vehicleType, parseRange(passengerString), parsePercentage(passengerString));
        }

        return chance;
    }

    @SuppressWarnings("unchecked")
    private static ConfigurationSection createMemoryConfig(Object obj)
    {
        MemoryConfiguration memoryConfig = new MemoryConfiguration();
        memoryConfig.addDefaults((Map<String, Object>) obj);
        return memoryConfig;
    }
}
