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
package se.crafted.chrisb.ecoCreature.commons;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public final class EventUtils
{
    private EventUtils()
    {
    }

    public static boolean isEntityFarmed(EntityDeathEvent event)
    {
        boolean isFarmed = false;

        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

        if (damageEvent != null) {
            if (damageEvent instanceof EntityDamageByBlockEvent && damageEvent.getCause().equals(DamageCause.CONTACT)) {
                isFarmed = true;
            }
            else if (damageEvent.getCause() != null) {
                DamageCause cause = damageEvent.getCause();
                if (cause.equals(DamageCause.FALL) || cause.equals(DamageCause.DROWNING) || cause.equals(DamageCause.SUFFOCATION)) {
                    isFarmed = true;
                }
            }
        }

        return isFarmed;
    }

    public static boolean isEntityFireFarmed(EntityDeathEvent event)
    {
        boolean isFireFarmed = false;

        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

        if (damageEvent != null && damageEvent.getCause() != null) {
            DamageCause cause = damageEvent.getCause();
            if (cause.equals(DamageCause.FIRE) || cause.equals(DamageCause.FIRE_TICK)) {
                isFireFarmed = true;
            }
        }

        return isFireFarmed;
    }

    public static boolean isEntityKilledEvent(EntityDeathEvent event)
    {
        return getKillerFromDeathEvent(event) != null;
    }

    public static Player getKillerFromDeathEvent(EntityDeathEvent event)
    {
        if (event != null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

            Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();

            if (damager instanceof Player) {
                return (Player) damager;
            }
            else if (damager instanceof Tameable) {
                Tameable tameable = (Tameable) damager;
                if (tameable.isTamed() && tameable.getOwner() instanceof Player) {
                    return (Player) tameable.getOwner();
                }
            }
            else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    return (Player) projectile.getShooter();
                }
            }
        }

        return null;
    }

    public static LivingEntity getTamedKillerFromDeathEvent(EntityDeathEvent event)
    {
        if (event != null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

            Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();

            if (damager instanceof Tameable) {
                Tameable tameable = (Tameable) damager;
                if (tameable.isTamed() && tameable.getOwner() instanceof Player) {
                    return (LivingEntity) damager;
                }
            }
        }

        return null;
    }

    public static boolean isPVPDeath(PlayerDeathEvent event)
    {
        return event != null && event.getEntity().getKiller() != null;
    }

    public static boolean isNotSuicide(PlayerDeathEvent event)
    {
        boolean isNotSuicide = true;

        if (event != null && event.getEntity().getLastDamageCause() != null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

            Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();

            if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    Player shooter = (Player) projectile.getShooter();
                    isNotSuicide = !event.getEntity().getUniqueId().equals(shooter.getUniqueId());
                }
            }
        }

        return isNotSuicide;
    }

    public static boolean isProjectileKill(EntityDeathEvent event)
    {
        boolean isProjectileKill = false;

        if (event != null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

            Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();

            if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    isProjectileKill = true;
                }
            }
        }

        return isProjectileKill;
    }
}
