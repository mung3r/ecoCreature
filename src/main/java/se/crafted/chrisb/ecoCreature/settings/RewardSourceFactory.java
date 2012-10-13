package se.crafted.chrisb.ecoCreature.settings;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.DeathPenaltySource;
import se.crafted.chrisb.ecoCreature.rewards.sources.EntityRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.HeroesRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.MaterialRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.McMMORewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.PVPRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.SetRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.StreakRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.CustomEntityRewardType;
import se.crafted.chrisb.ecoCreature.settings.types.CustomMaterialRewardType;
import se.crafted.chrisb.ecoCreature.settings.types.CustomRewardType;
import se.crafted.chrisb.ecoCreature.settings.types.DeathTpPlusRewardType;
import se.crafted.chrisb.ecoCreature.settings.types.HeroesRewardType;
import se.crafted.chrisb.ecoCreature.settings.types.McMMORewardType;

public final class RewardSourceFactory
{
    private RewardSourceFactory()
    {
    }

    public static AbstractRewardSource createSource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        if (CustomMaterialRewardType.fromName(name) != CustomMaterialRewardType.INVALID) {
            switch (CustomMaterialRewardType.fromName(name)) {
                case LEGACY_SPAWNER:
                    source = new MaterialRewardSource(config);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + name);
            }
        }
        else if (Material.matchMaterial(name) != null) {
            source = new MaterialRewardSource(config);
        }
        else if (CustomEntityRewardType.fromName(name) != CustomEntityRewardType.INVALID) {
            switch (CustomEntityRewardType.fromName(name)) {
                case ANGRY_WOLF:
                case PLAYER:
                case POWERED_CREEPER:
                    source = new EntityRewardSource(config);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + name);
            }
        }
        else if (EntityType.fromName(name) != null) {
            source = new EntityRewardSource(config);
        }
        else if (CustomRewardType.fromName(name) != CustomRewardType.INVALID) {
            switch (CustomRewardType.fromName(name)) {
                case DEATH_PENALTY:
                    source = new DeathPenaltySource(config);
                    break;
                case LEGACY_PVP:
                    source = new PVPRewardSource(config);
                    break;
                case SET:
                    source = new SetRewardSource(config);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + name);
            }
        }
        else if (DeathTpPlusRewardType.fromName(name) != DeathTpPlusRewardType.INVALID) {
            switch (DeathTpPlusRewardType.fromName(name)) {
                case DEATH_STREAK:
                case KILL_STREAK:
                    source = new StreakRewardSource(config);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + name);
            }
        }
        else if (HeroesRewardType.fromName(name) != HeroesRewardType.INVALID) {
            switch (HeroesRewardType.fromName(name)) {
                case HERO_LEVELED:
                case HERO_MASTERED:
                    source = new HeroesRewardSource(config);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + name);
            }
        }
        else if (McMMORewardType.fromName(name) != McMMORewardType.INVALID) {
            switch (McMMORewardType.fromName(name)) {
                case MCMMO_LEVELED:
                    source = new McMMORewardSource(config);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + name);
            }
        }

        if (source != null) {
            ECLogger.getInstance().debug(RewardSourceFactory.class, name + " mapped to " + source.getClass().getSimpleName());
        }
        return source;
    }
}
