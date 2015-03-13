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
package se.crafted.chrisb.ecoCreature.drops.chances;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentChance extends AbstractChance
{
    private final Enchantment enchantment;

    public EnchantmentChance(Enchantment enchantment)
    {
        this.enchantment = enchantment;
    }

    public Enchantment getEnchantment()
    {
        return enchantment;
    }

    public static Map<Enchantment, Integer> nextEnchantments(Collection<EnchantmentChance> enchantments)
    {
        Map<Enchantment, Integer> enchantmentMap = new HashMap<>();

        for (EnchantmentChance enchantment : enchantments) {
            int level = enchantment.nextIntAmount();
            if (level > 0) {
                enchantmentMap.put(enchantment.getEnchantment(), level);
            }
        }

        return enchantmentMap;
    }
}