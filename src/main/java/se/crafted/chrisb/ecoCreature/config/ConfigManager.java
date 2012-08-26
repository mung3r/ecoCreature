package se.crafted.chrisb.ecoCreature.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.messages.MessageManager;
import se.crafted.chrisb.ecoCreature.rewards.Coin;
import se.crafted.chrisb.ecoCreature.rewards.Drop;
import se.crafted.chrisb.ecoCreature.rewards.Exp;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.RewardManager;
import se.crafted.chrisb.ecoCreature.rewards.RewardType;
import se.crafted.chrisb.ecoCreature.rewards.gain.EnvironmentGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.FactionsGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.Gain;
import se.crafted.chrisb.ecoCreature.rewards.gain.GroupGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.HeroesGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.McMMOGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.MobArenaGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.RegionGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.RegiosGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.ResidenceGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.TimeGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.TownyGain;

public class ConfigManager
{
    public static final String DEFAULT_WORLD = "__DEFAULT_WORLD__";

    private static final String OLD_CONFIG_FILE = "ecoCreature.yml";
    private static final String DEFAULT_CONFIG_FILE = "default.yml";

    private final ecoCreature plugin;
    private final File dataWorldsFolder;

    private File defaultConfigFile;
    private FileConfiguration defaultConfig;
    private Map<String, FileConfiguration> worldConfigs;
    private Map<String, RewardManager> globalRewardManager;

    public ConfigManager(ecoCreature plugin)
    {
        this.plugin = plugin;
        dataWorldsFolder = new File(plugin.getDataFolder(), "worlds");
        dataWorldsFolder.mkdirs();

        try {
            load();
        }
        catch (Exception e) {
            ECLogger.getInstance().severe("Failed to load config: " + e.toString());
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public Map<String, RewardManager> getGlobalRewardManager()
    {
        return globalRewardManager;
    }

    private void load() throws IOException, InvalidConfigurationException
    {
        defaultConfig = new YamlConfiguration();
        defaultConfigFile = new File(plugin.getDataFolder(), DEFAULT_CONFIG_FILE);

        File oldConfigFile = new File(plugin.getDataFolder(), OLD_CONFIG_FILE);

        if (defaultConfigFile.exists()) {
            defaultConfig.load(defaultConfigFile);
        }
        else if (oldConfigFile.exists()) {
            ECLogger.getInstance().info("Converting old config file.");
            defaultConfig = getConfig(oldConfigFile);
            if (oldConfigFile.delete()) {
                ECLogger.getInstance().info("Old config file converted.");
            }
        }
        else {
            defaultConfig = getConfig(defaultConfigFile);
        }

        ECLogger.getInstance().info("Loaded config defaults.");
        RewardManager defaultRewardManager = loadRewardConfig(defaultConfig);
        globalRewardManager = new HashMap<String, RewardManager>();
        globalRewardManager.put(DEFAULT_WORLD, defaultRewardManager);

        worldConfigs = new HashMap<String, FileConfiguration>();

        for (World world : plugin.getServer().getWorlds()) {

            File worldConfigFile = new File(dataWorldsFolder, world.getName() + ".yml");

            if (worldConfigFile.exists()) {
                FileConfiguration worldConfig = getConfig(worldConfigFile);
                ECLogger.getInstance().info("Loaded config for " + world.getName() + " world.");
                globalRewardManager.put(world.getName(), loadRewardConfig(worldConfig));
                worldConfigs.put(world.getName(), worldConfig);
            }
            else {
                globalRewardManager.put(world.getName(), defaultRewardManager);
            }

        }
    }

    public void save()
    {
        try {
            defaultConfig.save(defaultConfigFile);

            for (String worldName : worldConfigs.keySet()) {
                FileConfiguration config = worldConfigs.get(worldName);
                File configFile = new File(dataWorldsFolder, worldName + ".yml");
                config.save(configFile);
            }
        }
        catch (Exception e) {
            ECLogger.getInstance().severe(e.getMessage());
        }
    }

    private RewardManager loadRewardConfig(FileConfiguration config)
    {
        RewardManager rewardManager = new RewardManager();

        rewardManager.setMetricsManager(plugin.getMetrics());
        rewardManager.setMessageManager(MessageManager.parseConfig(config));
        rewardManager.setGainMultipliers(loadGainConfig(config));

        rewardManager.isIntegerCurrency = config.getBoolean("System.Economy.IntegerCurrency", false);

        rewardManager.canCampSpawner = config.getBoolean("System.Hunting.AllowCamping", false);
        rewardManager.shouldClearCampDrops = config.getBoolean("System.Hunting.ClearCampDrops", true);
        rewardManager.campByDistance = config.getBoolean("System.Hunting.CampingByDistance", true);
        rewardManager.campByEntity = config.getBoolean("System.Hunting.CampingByEntity", false);
        rewardManager.shouldClearDefaultDrops = config.getBoolean("System.Hunting.ClearDefaultDrops", true);
        rewardManager.shouldOverrideDrops = config.getBoolean("System.Hunting.OverrideDrops", true);
        rewardManager.isFixedDrops = config.getBoolean("System.Hunting.FixedDrops", false);
        rewardManager.campRadius = config.getInt("System.Hunting.CampRadius", 7);
        rewardManager.hasBowRewards = config.getBoolean("System.Hunting.BowRewards", true);
        rewardManager.hasDeathPenalty = config.getBoolean("System.Hunting.PenalizeDeath", false);
        rewardManager.hasPVPReward = config.getBoolean("System.Hunting.PVPReward", true);
        rewardManager.isPercentPenalty = config.getBoolean("System.Hunting.PenalizeType", true);
        rewardManager.isPercentPvpReward = config.getBoolean("System.Hunting.PVPRewardType", true);
        rewardManager.penaltyAmount = config.getDouble("System.Hunting.PenalizeAmount", 0.05D);
        rewardManager.pvpRewardAmount = config.getDouble("System.Hunting.PenalizeAmount", 0.05D);
        rewardManager.canHuntUnderSeaLevel = config.getBoolean("System.Hunting.AllowUnderSeaLVL", true);
        rewardManager.isWolverineMode = config.getBoolean("System.Hunting.WolverineMode", true);
        rewardManager.noFarm = config.getBoolean("System.Hunting.NoFarm", false);
        rewardManager.noFarmFire = config.getBoolean("System.Hunting.NoFarmFire", false);
        rewardManager.hasMobArenaRewards = config.getBoolean("System.Hunting.MobArenaRewards", false);
        rewardManager.hasCreativeModeRewards = config.getBoolean("System.Hunting.CreativeModeRewards", false);

        Map<String, Reward> rewardSet = new HashMap<String, Reward>();
        ConfigurationSection rewardSetsConfig = config.getConfigurationSection("RewardSets");
        if (rewardSetsConfig != null) {
            for (String setName : rewardSetsConfig.getKeys(false)) {
                rewardSet.put(setName, createReward(RewardType.CUSTOM, rewardSetsConfig.getConfigurationSection(setName)));
            }
        }

        ConfigurationSection rewardTableConfig = config.getConfigurationSection("RewardTable");
        if (rewardTableConfig != null) {
            Map<RewardType, List<Reward>> rewards = new HashMap<RewardType, List<Reward>>();
            for (String rewardName : rewardTableConfig.getKeys(false)) {
                Reward reward = createReward(RewardType.fromName(rewardName), rewardTableConfig.getConfigurationSection(rewardName));

                if (!rewards.containsKey(reward.getType())) {
                    rewards.put(reward.getType(), new ArrayList<Reward>());
                }

                if (rewardTableConfig.getConfigurationSection(rewardName).getList("Sets") != null) {
                    List<String> setList = rewardTableConfig.getConfigurationSection(rewardName).getStringList("Sets");
                    for (String setName : setList) {
                        if (rewardSet.containsKey(setName)) {
                            rewards.get(reward.getType()).add(mergeReward(reward, rewardSet.get(setName)));
                        }
                    }
                }
                else {
                    rewards.get(reward.getType()).add(reward);
                }
            }
            rewardManager.setRewards(rewards);
        }

        return rewardManager;
    }

    private static Reward mergeReward(Reward from, Reward to)
    {
        Reward reward = new Reward();
        reward.setName(to.getName());
        reward.setType(to.getType());

        reward.setDrops(from.hasDrops() ? from.getDrops() : to.getDrops());
        reward.setCoin(from.hasCoin() ? from.getCoin() : to.getCoin());
        reward.setExp(from.hasExp() ? from.getExp() : to.getExp());

        reward.setNoRewardMessage(!from.getNoRewardMessage().equals(to.getNoRewardMessage()) ? from.getNoRewardMessage() : to.getNoRewardMessage());
        reward.setRewardMessage(!from.getRewardMessage().equals(to.getRewardMessage()) ? from.getRewardMessage() : to.getRewardMessage());
        reward.setPenaltyMessage(!from.getPenaltyMessage().equals(to.getPenaltyMessage()) ? from.getPenaltyMessage() : to.getPenaltyMessage());

        return reward;
    }

    private Set<Gain> loadGainConfig(ConfigurationSection config)
    {
        Set<Gain> gainMultipliers = new HashSet<Gain>();

        gainMultipliers.add(GroupGain.parseConfig(config.getConfigurationSection("Gain.Groups")));
        gainMultipliers.add(TimeGain.parseConfig(config.getConfigurationSection("Gain.Time")));
        gainMultipliers.add(EnvironmentGain.parseConfig(config.getConfigurationSection("Gain.Environment")));
        gainMultipliers.add(RegionGain.parseConfig(config.getConfigurationSection("Gain.WorldGuard")));
        gainMultipliers.add(RegiosGain.parseConfig(config.getConfigurationSection("Gain.Regios")));
        gainMultipliers.add(ResidenceGain.parseConfig(config.getConfigurationSection("Gain.Residence")));
        gainMultipliers.add(FactionsGain.parseConfig(config.getConfigurationSection("Gain.Factions")));
        gainMultipliers.add(TownyGain.parseConfig(config.getConfigurationSection("Gain.Towny")));
        gainMultipliers.add(MobArenaGain.parseConfig(config.getConfigurationSection("Gain.MobArena.InArena")));
        gainMultipliers.add(HeroesGain.parseConfig(config.getConfigurationSection("Gain.Heroes.InParty")));
        gainMultipliers.add(McMMOGain.parseConfig(config.getConfigurationSection("Gain.mcMMO.InParty")));

        return gainMultipliers;
    }

    private FileConfiguration getConfig(File file) throws IOException, InvalidConfigurationException
    {
        FileConfiguration config = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdir();
            file.createNewFile();
            InputStream inputStream = plugin.getResource(file.getName());
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            ECLogger.getInstance().info("Default config written to " + file.getName());
        }
        else {
            ECLogger.getInstance().severe("Default config could not be created!");
        }

        config.load(file);
        config.setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource(file.getName())));
        config.options().copyDefaults(true);

        return config;
    }

    private static Reward createReward(RewardType type, ConfigurationSection config)
    {
        Reward reward = new Reward();
        reward.setName(config.getName());
        reward.setType(type);

        reward.setDrops(Drop.parseConfig(config));
        reward.setCoin(Coin.parseConfig(config));
        reward.setExp(Exp.parseConfig(config));

        reward.setNoRewardMessage(MessageManager.convertMessage(config.getString("NoReward_Message", MessageManager.NO_REWARD_MESSAGE)));
        reward.setRewardMessage(MessageManager.convertMessage(config.getString("Reward_Message", MessageManager.REWARD_MESSAGE)));
        reward.setPenaltyMessage(MessageManager.convertMessage(config.getString("Penalty_Message", MessageManager.PENALTY_MESSAGE)));

        return reward;
    }
}
