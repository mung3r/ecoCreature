package se.crafted.chrisb.ecoCreature.commons;


import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Utils
{
    public static boolean isUnderSeaLevel(Entity entity)
    {
        return entity != null && (entity.getLocation().getBlockY() < entity.getWorld().getSeaLevel());
    }

    public static Player getKillerFromDeathEvent(EntityDeathEvent event)
    {
        if (event != null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

            Entity damager = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();

            if (damager instanceof Player) {
                return (Player) damager;
            }
            else if (damager instanceof Tameable) {
                if (((Tameable) damager).isTamed() && ((Tameable) damager).getOwner() instanceof Player) {
                    return (Player) ((Tameable) damager).getOwner();
                }
            }
            else if (damager instanceof Projectile) {
                if (((Projectile) damager).getShooter() instanceof Player) {
                    return (Player) ((Projectile) damager).getShooter();
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
                if (((Tameable) damager).isTamed() && ((Tameable) damager).getOwner() instanceof Player) {
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

    public static boolean isOwner(Player player, Entity entity)
    {
        if (entity instanceof Tameable) {
            if (((Tameable) entity).isTamed() && ((Tameable) entity).getOwner() instanceof Player) {
                Player owner = (Player) ((Tameable) entity).getOwner();
                if (owner.getName().equals(player.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
