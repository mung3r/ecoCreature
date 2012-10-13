package se.crafted.chrisb.ecoCreature.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.settings.types.CustomMaterialRewardType;

public class CustomMaterialRewardSettings extends AbstractRewardSettings
{
    private Map<CustomMaterialRewardType, List<AbstractRewardSource>> sources;

    public CustomMaterialRewardSettings(Map<CustomMaterialRewardType, List<AbstractRewardSource>> sources)
    {
        this.sources = sources;
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
        if (event instanceof BlockBreakEvent) {
            return hasRewardSource((BlockBreakEvent) event);
        }

        return false;
    }

    private boolean hasRewardSource(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (DependencyUtils.hasPermission(player, "reward." + block.getType().name())) {
            return hasRewardSource(CustomMaterialRewardType.fromMaterial(block.getType()));
        }
        else {
            ECLogger.getInstance().debug(this.getClass(), "No reward for " + player.getName() + " due to lack of permission for " + block.getType().name());
        }

        return false;
    }

    private boolean hasRewardSource(CustomMaterialRewardType material)
    {
        return material != null && sources.containsKey(material) && !sources.get(material).isEmpty();
    }

    @Override
    public AbstractRewardSource getRewardSource(Event event)
    {
        if (event instanceof BlockBreakEvent) {
            return getRewardSource(((BlockBreakEvent) event).getBlock());
        }

        return null;
    }

    private AbstractRewardSource getRewardSource(Block block)
    {
        AbstractRewardSource source = null;

        if (hasRewardSource(CustomMaterialRewardType.fromMaterial(block.getType()))) {
            source = getRewardSource(CustomMaterialRewardType.fromMaterial(block.getType()));
        }
        else {
            ECLogger.getInstance().warning("No reward found for block: " + block.getType().name());
        }

        return source;
    }

    private AbstractRewardSource getRewardSource(CustomMaterialRewardType material)
    {
        AbstractRewardSource source = null;

        if (hasRewardSource(material)) {
            source = sources.get(material).get(random.nextInt(sources.get(material).size()));
        }
        else {
            ECLogger.getInstance().warning("No reward defined for material: " + material);
        }

        return source;
    }

    public static AbstractRewardSettings parseConfig(ConfigurationSection config)
    {
        Map<CustomMaterialRewardType, List<AbstractRewardSource>> sources = new HashMap<CustomMaterialRewardType, List<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                CustomMaterialRewardType type = CustomMaterialRewardType.fromName(typeName);

                if (type != CustomMaterialRewardType.INVALID) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(typeName, rewardTable.getConfigurationSection(typeName)), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(mergeSets(source, rewardTable, config.getConfigurationSection("RewardSets")));
                }
            }
        }

        return new CustomMaterialRewardSettings(sources);
    }
}
