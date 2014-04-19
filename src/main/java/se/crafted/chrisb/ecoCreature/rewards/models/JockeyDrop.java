/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature.rewards.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.EntityType;

public class JockeyDrop extends EntityDrop
{
    private EntityType passenger;

    public JockeyDrop(EntityType vehicle)
    {
        super(vehicle);
    }

    public void setPassenger(EntityType passenger)
    {
        this.passenger = passenger;
    }

    public EntityType getPassenger()
    {
        return passenger;
    }

    public EntityType getVehicle()
    {
        return getType();
    }

    public static List<EntityDrop> parseConfig(ConfigurationSection config)
    {
        List<EntityDrop> drops = new ArrayList<EntityDrop>();

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
    public List<EntityType> getOutcome()
    {
        List<EntityType> types = new ArrayList<EntityType>();
        for (EntityType vehicle : super.getOutcome()) {
            types.add(vehicle);
            types.add(passenger);
        }
        return types;
    }

    private static JockeyDrop createJockeyDrop(String passengerString, String vehicleString)
    {
        JockeyDrop drop = null;

        EntityType vehicleType = parseType(vehicleString);
        if (vehicleType != null && !isAmbiguous(vehicleType)) {
            drop = new JockeyDrop(vehicleType);
            drop.setRange(parseRange(passengerString));
            drop.setPercentage(parsePercentage(passengerString));
            
            EntityType passengerType = parseType(passengerString);
            if (passengerType != null && !isAmbiguous(passengerType)) {
                drop.setPassenger(passengerType);
            }
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
