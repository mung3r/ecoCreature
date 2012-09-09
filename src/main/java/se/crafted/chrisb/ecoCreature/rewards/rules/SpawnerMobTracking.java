package se.crafted.chrisb.ecoCreature.rewards.rules;

import org.bukkit.entity.LivingEntity;

public interface SpawnerMobTracking
{
    void addSpawnerMob(LivingEntity entity);

    boolean isSpawnerMob(LivingEntity entity);
}
