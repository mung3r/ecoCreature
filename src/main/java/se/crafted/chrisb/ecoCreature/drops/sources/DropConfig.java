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
package se.crafted.chrisb.ecoCreature.drops.sources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.drops.AssembledDrop;
import se.crafted.chrisb.ecoCreature.drops.SpawnerMobTag;
import se.crafted.chrisb.ecoCreature.drops.categories.AbstractDropCategory;
import se.crafted.chrisb.ecoCreature.drops.gain.PlayerGain;
import se.crafted.chrisb.ecoCreature.drops.parties.Party;

public class DropConfig implements SpawnerMobTag
{
    public static final String SPAWNERMOB_TAG_MDID = "ecoCreature.spawnerMob";
    public static final String SPAWNERLOC_TAG_MDID = "ecoCreature.spawnerLoc";

    private boolean clearEnchantedDrops;
    private boolean clearOnNoDrops;
    private boolean overrideDrops;
    private boolean noFarm;
    private boolean noFarmFire;

    private ecoCreature plugin;
    private List<AbstractDropCategory<?>> dropCategories;
    private Collection<PlayerGain> gainMultipliers;
    private Collection<Party> parties;

    private final FixedMetadataValue spawnerMobTag;

    public DropConfig(ecoCreature plugin)
    {
        this.plugin = plugin;
        dropCategories = new ArrayList<>();
        gainMultipliers = Collections.emptyList();
        parties = Collections.emptyList();

        spawnerMobTag = new FixedMetadataValue(plugin, true);
    }

    public boolean isClearEnchantedDrops()
    {
        return clearEnchantedDrops;
    }

    public void setClearEnchantedDrops(boolean clearEnchantedDrops)
    {
        this.clearEnchantedDrops = clearEnchantedDrops;
    }

    public boolean isClearOnNoDrops()
    {
        return clearOnNoDrops;
    }

    public void setClearOnNoDrops(boolean clearOnNoDrops)
    {
        this.clearOnNoDrops = clearOnNoDrops;
    }

    public boolean isOverrideDrops()
    {
        return overrideDrops;
    }

    public void setOverrideDrops(boolean overrideDrops)
    {
        this.overrideDrops = overrideDrops;
    }

    public boolean isNoFarm()
    {
        return noFarm;
    }

    public void setNoFarm(boolean noFarm)
    {
        this.noFarm = noFarm;
    }

    public boolean isNoFarmFire()
    {
        return noFarmFire;
    }

    public void setNoFarmFire(boolean noFarmFire)
    {
        this.noFarmFire = noFarmFire;
    }

    public void setGainMultipliers(Collection<PlayerGain> gainMultipliers)
    {
        this.gainMultipliers = gainMultipliers;
    }

    public void setParties(Collection<Party> parties)
    {
        this.parties = parties;
    }

    public void setDropCategories(List<AbstractDropCategory<?>> dropCategories)
    {
        this.dropCategories = dropCategories;
    }

    public Collection<AssembledDrop> assembleDrops(final Event event)
    {
        Collection<AssembledDrop> drops = new ArrayList<>();

        for (AbstractDropCategory<?> category : dropCategories) {
            for (AbstractDropSource source : category.getDropSources(event)) {
                drops.addAll(source.assembleDrops(event));
            }
        }

        return drops;
    }

    public double getGainMultiplier(Player player)
    {
        double multiplier = 1.0;
        LoggerUtil.getInstance().debug("===== START: gain calculation for " + player.getName());

        for (PlayerGain gain : gainMultipliers) {
            if (gain.hasPermission(player)) {
                multiplier *= gain.getGain(player);
            }
        }

        LoggerUtil.getInstance().debug("===== END: gain is " + multiplier);
        return multiplier;
    }

    public Collection<String> getPartyMembers(Player player)
    {
        Collection<String> players = new ArrayList<>();

        for (Party party : parties) {
            if (party.isShared()) {
                players.addAll(party.getMembers(player));
            }
        }

        return players;
    }

    @Override
    public void tagSpawnerMob(CreatureSpawnEvent event)
    {
        event.getEntity().setMetadata(SPAWNERMOB_TAG_MDID, spawnerMobTag);
        event.getEntity().setMetadata(SPAWNERLOC_TAG_MDID, new FixedMetadataValue(plugin, event.getLocation()));
    }

    @Override
    public boolean isSpawnerMob(LivingEntity entity)
    {
        return entity.hasMetadata(SPAWNERMOB_TAG_MDID);
    }
}
