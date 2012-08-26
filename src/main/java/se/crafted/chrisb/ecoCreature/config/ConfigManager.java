package se.crafted.chrisb.ecoCreature.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.commons.TimePeriod;
import se.crafted.chrisb.ecoCreature.messages.MessageManager;
import se.crafted.chrisb.ecoCreature.rewards.Coin;
import se.crafted.chrisb.ecoCreature.rewards.Drop;
import se.crafted.chrisb.ecoCreature.rewards.Exp;
import se.crafted.chrisb.ecoCreature.rewards.Reward;
import se.crafted.chrisb.ecoCreature.rewards.RewardManager;
import se.crafted.chrisb.ecoCreature.rewards.RewardType;

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
        RewardManager rewardManager = new RewardManager(MessageManager.parseConfig(config), plugin.getMetrics());

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

        ConfigurationSection groupGainConfig = config.getConfigurationSection("Gain.Groups");
        if (groupGainConfig != null) {
            for (String group : groupGainConfig.getKeys(false)) {
                rewardManager.groupMultiplier.put(group.toLowerCase(), Double.valueOf(groupGainConfig.getConfigurationSection(group).getDouble("Amount", 0.0D)));
            }
        }

        ConfigurationSection timeGainConfig = config.getConfigurationSection("Gain.Time");
        if (timeGainConfig != null) {
            for (String period : timeGainConfig.getKeys(false)) {
                rewardManager.timeMultiplier.put(TimePeriod.fromName(period), Double.valueOf(timeGainConfig.getConfigurationSection(period).getDouble("Amount", 1.0D)));
            }
        }

        ConfigurationSection envGainConfig = config.getConfigurationSection("Gain.Environment");
        if (envGainConfig != null) {
            for (String environment : envGainConfig.getKeys(false)) {
                try {
                    rewardManager.envMultiplier.put(Environment.valueOf(environment.toUpperCase()), Double.valueOf(envGainConfig.getConfigurationSection(environment).getDouble("Amount", 1.0D)));
                }
                catch (Exception e) {
                    ECLogger.getInstance().warning("Skipping unknown environment name: " + environment);
                }
            }
        }

        ConfigurationSection worldGuardGainConfig = config.getConfigurationSection("Gain.WorldGuard");
        if (worldGuardGainConfig != null) {
            for (String regionName : worldGuardGainConfig.getKeys(false)) {
                rewardManager.worldGuardMultiplier.put(regionName, Double.valueOf(worldGuardGainConfig.getConfigurationSection(regionName).getDouble("Amount", 1.0D)));
            }
        }

        ConfigurationSection regiosGainConfig = config.getConfigurationSection("Gain.Regios");
        if (regiosGainConfig != null) {
            for (String regionName: regiosGainConfig.getKeys(false)) {
                rewardManager.regiosMultiplier.put(regionName, Double.valueOf(regiosGainConfig.getConfigurationSection(regionName).getDouble("Amount", 1.0D)));
            }
        }

        ConfigurationSection residenceGainConfig = config.getConfigurationSection("Gain.Residence");
        if (residenceGainConfig != null) {
            for (String residenceName: residenceGainConfig.getKeys(false)) {
                rewardManager.residenceMultiplier.put(residenceName, Double.valueOf(residenceGainConfig.getConfigurationSection(residenceName).getDouble("Amount", 1.0D)));
            }
        }

        ConfigurationSection factionsGainConfig = config.getConfigurationSection("Gain.Factions");
        if (factionsGainConfig != null) {
            for (String factionsTag: factionsGainConfig.getKeys(false)) {
                rewardManager.factionsMultiplier.put(factionsTag, Double.valueOf(factionsGainConfig.getConfigurationSection(factionsTag).getDouble("Amount", 1.0D)));
            }
        }

        ConfigurationSection townyGainConfig = config.getConfigurationSection("Gain.Towny");
        if (townyGainConfig != null) {
            for (String townName: townyGainConfig.getKeys(false)) {
                rewardManager.townyMultiplier.put(townName, Double.valueOf(townyGainConfig.getConfigurationSection(townName).getDouble("Amount", 1.0D)));
            }
        }

        ConfigurationSection mobArenaGainConfig = config.getConfigurationSection("Gain.MobArena.InArena");
        if (mobArenaGainConfig != null) {
            rewardManager.mobArenaMultiplier = mobArenaGainConfig.getDouble("Amount", 1.0D);
            rewardManager.isMobArenaShare = mobArenaGainConfig.getBoolean("Share", true);
        }
        else {
            rewardManager.mobArenaMultiplier = 1.0D;
        }

        ConfigurationSection heroesGainConfig = config.getConfigurationSection("Gain.Heroes.InParty");
        if (heroesGainConfig != null) {
            rewardManager.heroesPartyMultiplier = heroesGainConfig.getDouble("Amount", 1.0D);
            rewardManager.isHeroesPartyShare = heroesGainConfig.getBoolean("Share", true);
        }
        else {
            rewardManager.heroesPartyMultiplier = 1.0D;
        }

        ConfigurationSection mcMMOGainConfig = config.getConfigurationSection("Gain.mcMMO.InParty");
        if (mcMMOGainConfig != null) {
            rewardManager.mcMMOPartyMultiplier = mcMMOGainConfig.getDouble("Amount", 1.0D);
            rewardManager.isMcMMOPartyShare = mcMMOGainConfig.getBoolean("Share", true);
        }

        Map<String, Reward> rewardSet = new HashMap<String, Reward>();
        ConfigurationSection rewardSetsConfig = config.getConfigurationSection("RewardSets");
        if (rewardSetsConfig != null) {
            for (String setName : rewardSetsConfig.getKeys(false)) {
                rewardSet.put(setName, createReward(RewardType.CUSTOM, rewardSetsConfig.getConfigurationSection(setName)));
            }
        }

        ConfigurationSection rewardTableConfig = config.getConfigurationSection("RewardTable");
        if (rewardTableConfig != null) {
            for (String rewardName : rewardTableConfig.getKeys(false)) {
                Reward reward = createReward(RewardType.fromName(rewardName), rewardTableConfig.getConfigurationSection(rewardName));

                if (!rewardManager.rewards.containsKey(reward.getType())) {
                    rewardManager.rewards.put(reward.getType(), new ArrayList<Reward>());
                }

                if (rewardTableConfig.getConfigurationSection(rewardName).getList("Sets") != null) {
                    List<String> setList = rewardTableConfig.getConfigurationSection(rewardName).getStringList("Sets");
                    for (String setName : setList) {
                        if (rewardSet.containsKey(setName)) {
                            rewardManager.rewards.get(reward.getType()).add(mergeReward(reward, rewardSet.get(setName)));
                        }
                    }
                }
                else {
                    rewardManager.rewards.get(reward.getType()).add(reward);
                }
            }
        }

        return rewardManager;
    }

    private static Reward mergeReward(Reward from, Reward to)
    {
        Reward reward = new Reward();

        reward.setName(to.getName());
        reward.setType(to.getType());

        if (from.hasDrops()) {
            reward.setDrops(from.getDrops());
        }

        if (from.hasCoin()) {
            reward.setCoin(from.getCoin());
        }

        if (from.hasExp()) {
            reward.setExp(from.getExp());
        }

        reward.setNoRewardMessage(!from.getNoRewardMessage().equals(to.getNoRewardMessage()) ? from.getNoRewardMessage() : to.getNoRewardMessage());
        reward.setRewardMessage(!from.getRewardMessage().equals(to.getRewardMessage()) ? from.getRewardMessage() : to.getRewardMessage());
        reward.setPenaltyMessage(!from.getPenaltyMessage().equals(to.getPenaltyMessage()) ? from.getPenaltyMessage() : to.getPenaltyMessage());

        return reward;
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
