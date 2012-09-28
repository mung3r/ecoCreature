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

    public static boolean isSuicide(PlayerDeathEvent event)
    {
        return event != null && event.getEntity().getLastDamageCause() == null;
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
