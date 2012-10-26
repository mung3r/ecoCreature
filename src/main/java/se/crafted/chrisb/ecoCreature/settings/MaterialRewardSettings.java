package se.crafted.chrisb.ecoCreature.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;

public class MaterialRewardSettings extends AbstractRewardSettings
{
    private Map<Material, List<AbstractRewardSource>> sources;

    public MaterialRewardSettings(Map<Material, List<AbstractRewardSource>> sources)
    {
        this.sources = sources;
    }

    @Override
    public boolean hasRewardSource(Event event)
    {
        return event instanceof BlockBreakEvent && hasRewardSource((BlockBreakEvent) event);
    }

    private boolean hasRewardSource(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block != null) { // TODO: fix this properly for BuildCraft
            if (DependencyUtils.hasPermission(player, "reward." + block.getType().name())) {
                return hasRewardSource(block.getType());
            }
            else {
                ECLogger.getInstance().debug(this.getClass(), "No reward for " + player.getName() + " due to lack of permission for " + block.getType().name());
            }
        }

        return false;
    }

    private boolean hasRewardSource(Material material)
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

        if (hasRewardSource(block.getType())) {
            source = getRewardSource(block.getType());
        }
        else {
            ECLogger.getInstance().warning("No reward found for block: " + block.getType().name());
        }

        return source;
    }

    private AbstractRewardSource getRewardSource(Material material)
    {
        AbstractRewardSource source = null;

        if (hasRewardSource(material)) {
            source = sources.get(material).get(nextInt(sources.get(material).size()));
        }
        else {
            ECLogger.getInstance().warning("No reward defined for material: " + material);
        }

        return source;
    }

    public static AbstractRewardSettings parseConfig(ConfigurationSection config)
    {
        Map<Material, List<AbstractRewardSource>> sources = new HashMap<Material, List<AbstractRewardSource>>();
        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");

        if (rewardTable != null) {
            for (String typeName : rewardTable.getKeys(false)) {
                Material type = Material.matchMaterial(typeName);

                if (type != null) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(typeName, rewardTable.getConfigurationSection(typeName)), config);

                    if (!sources.containsKey(type)) {
                        sources.put(type, new ArrayList<AbstractRewardSource>());
                    }

                    sources.get(type).add(mergeSets(source, rewardTable, config.getConfigurationSection("RewardSets")));
                }
            }
        }

        return new MaterialRewardSettings(sources);
    }
}
