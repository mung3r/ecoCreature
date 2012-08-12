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
import se.crafted.chrisb.ecoCreature.commons.TimePeriod;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.MessageManager;
import se.crafted.chrisb.ecoCreature.rewards.Drop;
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

    public ConfigManager(ecoCreature plugin)
    {
        this.plugin = plugin;
        dataWorldsFolder = new File(plugin.getDataFolder(), "worlds");
        dataWorldsFolder.mkdirs();

        try {
            load();
        }
        catch (Exception e) {
            ecoCreature.getECLogger().severe("Failed to load config: " + e.toString());
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
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
            ecoCreature.getECLogger().info("Converting old config file.");
            defaultConfig = getConfig(oldConfigFile);
            if (oldConfigFile.delete()) {
                ecoCreature.getECLogger().info("Old config file converted.");
            }
        }
        else {
            defaultConfig = getConfig(defaultConfigFile);
        }

        ecoCreature.getECLogger().info("Loaded config defaults.");
        MessageManager defaultMessageManager = loadMessageConfig(defaultConfig);
        RewardManager defaultRewardManager = loadRewardConfig(defaultConfig);
        plugin.getGlobalMessageManager().put(DEFAULT_WORLD, defaultMessageManager);
        plugin.getGlobalRewardManager().put(DEFAULT_WORLD, defaultRewardManager);

        worldConfigs = new HashMap<String, FileConfiguration>();

        for (World world : plugin.getServer().getWorlds()) {

            File worldConfigFile = new File(dataWorldsFolder, world.getName() + ".yml");

            if (worldConfigFile.exists()) {
                FileConfiguration worldConfig = getConfig(worldConfigFile);
                ecoCreature.getECLogger().info("Loaded config for " + world.getName() + " world.");
                plugin.getGlobalMessageManager().put(world.getName(), loadMessageConfig(worldConfig));
                plugin.getGlobalRewardManager().put(world.getName(), loadRewardConfig(worldConfig));
                worldConfigs.put(world.getName(), worldConfig);
            }
            else {
                plugin.getGlobalMessageManager().put(world.getName(), defaultMessageManager);
                plugin.getGlobalRewardManager().put(world.getName(), defaultRewardManager);
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
            ecoCreature.getECLogger().severe(e.getMessage());
        }
    }

    private MessageManager loadMessageConfig(FileConfiguration config)
    {
        MessageManager messageManager = new MessageManager(plugin);

        messageManager.shouldOutputMessages = config.getBoolean("System.Messages.Output", true);
        messageManager.shouldLogCoinRewards = config.getBoolean("System.Messages.LogCoinRewards", true);
        messageManager.noBowRewardMessage = new Message(convertMessage(config.getString("System.Messages.NoBowMessage", MessageManager.NO_BOW_REWARD_MESSAGE)), true);
        messageManager.noCampMessage = new Message(convertMessage(config.getString("System.Messages.NoCampMessage", MessageManager.NO_CAMP_MESSAGE)), config.getBoolean("System.Messages.Spawner", false));
        messageManager.deathPenaltyMessage = new Message(convertMessage(config.getString("System.Messages.DeathPenaltyMessage", MessageManager.DEATH_PENALTY_MESSAGE)), true);
        messageManager.pvpRewardMessage = new Message(convertMessage(config.getString("System.Messages.PVPRewardMessage", MessageManager.PVP_REWARD_MESSAGE)), true);

        return messageManager;
    }

    private RewardManager loadRewardConfig(FileConfiguration config)
    {
        RewardManager rewardManager = new RewardManager(plugin);

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
                    ecoCreature.getECLogger().warning("Skipping unknown environment name: " + environment);
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
                rewardSet.put(setName, createReward(RewardType.CUSTOM, rewardSetsConfig.getConfigurationSection(setName), rewardManager, config.getBoolean("System.Messages.NoReward", false)));
            }
        }

        ConfigurationSection rewardTableConfig = config.getConfigurationSection("RewardTable");
        if (rewardTableConfig != null) {
            for (String rewardName : rewardTableConfig.getKeys(false)) {
                Reward reward = createReward(RewardType.fromName(rewardName), rewardTableConfig.getConfigurationSection(rewardName), rewardManager, config.getBoolean("System.Messages.NoReward", false));

                if (!rewardManager.rewards.containsKey(reward.getRewardType())) {
                    rewardManager.rewards.put(reward.getRewardType(), new ArrayList<Reward>());
                }

                if (rewardTableConfig.getConfigurationSection(rewardName).getList("Sets") != null) {
                    List<String> setList = rewardTableConfig.getConfigurationSection(rewardName).getStringList("Sets");
                    for (String setName : setList) {
                        if (rewardSet.containsKey(setName)) {
                            rewardManager.rewards.get(reward.getRewardType()).add(mergeReward(reward, rewardSet.get(setName)));
                        }
                    }
                }
                else {
                    rewardManager.rewards.get(reward.getRewardType()).add(reward);
                }
            }
        }

        return rewardManager;
    }

    private static Reward mergeReward(Reward from, Reward to)
    {
        Reward reward = new Reward();

        reward.setRewardName(to.getRewardName());
        reward.setRewardType(to.getRewardType());

        reward.setDrops(!from.getDrops().isEmpty() ? from.getDrops() : to.getDrops());

        reward.setCoinMin(from.getCoinMin() > 0.0 ? from.getCoinMin() : to.getCoinMin());
        reward.setCoinMax(from.getCoinMax() > 0.0 ? from.getCoinMax() : to.getCoinMax());
        reward.setCoinPercentage(from.getCoinPercentage() > 0.0 ? from.getCoinPercentage() : to.getCoinPercentage());

        reward.setExpMin(from.getExpMin() != null ? from.getExpMin() : to.getExpMin());
        reward.setExpMax(from.getExpMax() != null ? from.getExpMax() : to.getExpMax());
        reward.setExpPercentage(from.getExpPercentage() != null ? from.getExpPercentage() : to.getExpPercentage());

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

            ecoCreature.getECLogger().info("Default config written to " + file.getName());
        }
        else {
            ecoCreature.getECLogger().severe("Default config could not be created!");
        }

        config.load(file);
        config.setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource(file.getName())));
        config.options().copyDefaults(true);

        return config;
    }

    private static Reward createReward(RewardType rewardType, ConfigurationSection rewardConfig, RewardManager rewardManager, boolean isNoRewardMessage)
    {
        Reward reward = new Reward();
        reward.setRewardName(rewardConfig.getName());
        reward.setRewardType(rewardType);

        if (rewardConfig.getList("Drops") != null) {
            List<String> dropsList = rewardConfig.getStringList("Drops");
            reward.setDrops(Drop.parseDrops(dropsList, rewardManager.isFixedDrops));
        }
        else {
            reward.setDrops(Drop.parseDrops(rewardConfig.getString("Drops"), rewardManager.isFixedDrops));
        }
        reward.setCoinMax(rewardConfig.getDouble("Coin_Maximum", 0));
        reward.setCoinMin(rewardConfig.getDouble("Coin_Minimum", 0));
        reward.setCoinPercentage(rewardConfig.getDouble("Coin_Percent", 0));
        String expMin = rewardConfig.getString("ExpMin");
        String expMax = rewardConfig.getString("ExpMax");
        String expPercentage = rewardConfig.getString("ExpPercent");
        if (expMin != null && expMax != null && expPercentage != null) {
            try {
                reward.setExpMin(Integer.parseInt(expMin));
                reward.setExpMax(Integer.parseInt(expMax));
                reward.setExpPercentage(Double.parseDouble(expPercentage));
            }
            catch (NumberFormatException e) {
                ecoCreature.getECLogger().warning("Could not parse exp for " + rewardConfig.getName());
            }
        }

        reward.setNoRewardMessage(new Message(convertMessage(rewardConfig.getString("NoReward_Message", MessageManager.NO_REWARD_MESSAGE)), isNoRewardMessage));
        reward.setRewardMessage(new Message(convertMessage(rewardConfig.getString("Reward_Message", MessageManager.REWARD_MESSAGE)), true));
        reward.setPenaltyMessage(new Message(convertMessage(rewardConfig.getString("Penalty_Message", MessageManager.PENALTY_MESSAGE)), true));

        return reward;
    }

    private static String convertMessage(String message)
    {
        if (message != null) {
            return message.replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
        }

        return null;
    }

}