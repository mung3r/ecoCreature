package se.crafted.chrisb.ecoCreature.rewards.sources;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import se.crafted.chrisb.ecoCreature.commons.CustomType;

public final class RewardSourceFactory
{
    private RewardSourceFactory()
    {
    }

    public static AbstractRewardSource createSource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        if (Material.matchMaterial(name) != null) {
            source = new MaterialRewardSource(config);
        }
        else if (EntityType.fromName(name) != null) {
            source = new EntityRewardSource(config);
        }
        else {
            switch (CustomType.fromName(name)) {
                case ANGRY_WOLF:
                case PLAYER:
                case POWERED_CREEPER:
                    source = new EntityRewardSource(config);
                    break;
                case DEATH_PENALTY:
                    source = new DeathPenaltySource(config);
                    break;
                case DEATH_STREAK:
                case KILL_STREAK:
                    source = new StreakRewardSource(config);
                    break;
                case HERO_MASTERED:
                    source = new HeroesRewardSource(config);
                    break;
                case LEGACY_PVP:
                    source = new PVPRewardSource(config);
                    break;
                case LEGACY_SPAWNER:
                    source = new MaterialRewardSource(config);
                    break;
                case MCMMO_LEVELED:
                    source = new McMMORewardSource(config);
                    break;
                case SET:
                    source = new SetRewardSource(config);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + name);
            }
        }

        return source;
    }
}
