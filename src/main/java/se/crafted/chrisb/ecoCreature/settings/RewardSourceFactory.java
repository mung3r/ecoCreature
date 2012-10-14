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
import se.crafted.chrisb.ecoCreature.settings.types.StreakRewardType;
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
            source = createCustomMaterialSource(name, config);
        }
        else if (Material.matchMaterial(name) != null) {
            source = new MaterialRewardSource(config);
        }
        else if (CustomEntityRewardType.fromName(name) != CustomEntityRewardType.INVALID) {
            source = createCustomEntitySource(name, config);
        }
        else if (EntityType.fromName(name) != null) {
            source = new EntityRewardSource(config);
        }
        else if (CustomRewardType.fromName(name) != CustomRewardType.INVALID) {
            source = createCustomSource(name, config);
        }
        else if (StreakRewardType.fromName(name) != StreakRewardType.INVALID) {
            source = StreakRewardSource.createRewardSource(name, config);
        }
        else if (HeroesRewardType.fromName(name) != HeroesRewardType.INVALID) {
            source = HeroesRewardSource.createRewardSource(name, config);
        }
        else if (McMMORewardType.fromName(name) != McMMORewardType.INVALID) {
            source = McMMORewardSource.createRewardSource(name, config);
        }

        if (source != null) {
            ECLogger.getInstance().debug(RewardSourceFactory.class, name + " mapped to " + source.getClass().getSimpleName());
        }
        return source;
    }

    private static AbstractRewardSource createCustomMaterialSource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        switch (CustomMaterialRewardType.fromName(name)) {
            case LEGACY_SPAWNER:
                source = new MaterialRewardSource(config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + name);
        }
        return source;
    }

    private static AbstractRewardSource createCustomEntitySource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

        switch (CustomEntityRewardType.fromName(name)) {
            case ANGRY_WOLF:
            case PLAYER:
            case POWERED_CREEPER:
                source = new EntityRewardSource(config);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + name);
        }
        return source;
    }

    private static AbstractRewardSource createCustomSource(String name, ConfigurationSection config)
    {
        AbstractRewardSource source = null;

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
        return source;
    }
}
