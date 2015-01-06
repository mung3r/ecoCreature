/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2014, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature.drops.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberRange;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.EntityType;

public class JockeyDrop extends EntityDrop
{
    private EntityType passenger;

    public JockeyDrop(EntityType passenger, EntityType vehicle, NumberRange range, double percentage)
    {
        super(vehicle, range, percentage);
        this.passenger = passenger;
    }

    public static Collection<EntityDrop> parseConfig(ConfigurationSection config)
    {
        Collection<EntityDrop> drops = new ArrayList<>();

        if (config != null && config.getList("Drops") != null) {

            for (Object obj : config.getList("Drops")) {
                if (obj instanceof LinkedHashMap) {
                    ConfigurationSection memoryConfig = createMemoryConfig(obj);
                    String passengerString = memoryConfig.getString("passenger");
                    String vehicleString = memoryConfig.getString("vehicle");
                    JockeyDrop drop = createJockeyDrop(passengerString, vehicleString);
                    if (drop != null) {
                        drops.add(drop);
                    }
                }
            }
        }

        return drops;
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

    private static JockeyDrop createJockeyDrop(String passengerString, String vehicleString)
    {
        JockeyDrop drop = null;

        EntityType passengerType = parseType(passengerString);
        EntityType vehicleType = parseType(vehicleString);

        if (isNotAmbiguous(vehicleType) && isNotAmbiguous(passengerType)) {
            drop = new JockeyDrop(passengerType, vehicleType, parseRange(passengerString), parsePercentage(passengerString));
        }

        return drop;
    }

    @SuppressWarnings("unchecked")
    private static ConfigurationSection createMemoryConfig(Object obj)
    {
        MemoryConfiguration memoryConfig = new MemoryConfiguration();
        memoryConfig.addDefaults((Map<String, Object>) obj);
        return memoryConfig;
    }
}
