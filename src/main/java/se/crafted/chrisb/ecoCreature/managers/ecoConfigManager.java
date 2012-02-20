package se.crafted.chrisb.ecoCreature.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoDrop;
import se.crafted.chrisb.ecoCreature.models.ecoMessage;
import se.crafted.chrisb.ecoCreature.models.ecoReward;
import se.crafted.chrisb.ecoCreature.models.ecoReward.RewardType;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil.TimePeriod;

public class ecoConfigManager
{
    private static final String OLD_CONFIG_FILE = "ecoCreature.yml";
    private static final String DEFAULT_CONFIG_FILE = "default.yml";

    private static final String DEFAULT_WORLD = "default";

    private final ecoCreature plugin;
    private Boolean isEnabled;

    public static boolean debug = false;

    public ecoConfigManager(ecoCreature plugin)
    {
        this.plugin = plugin;
    }

    public Boolean isEnabled()
    {
        return isEnabled;
    }

    public void load() throws Exception
    {
        FileConfiguration defaultConfig = new YamlConfiguration();

        File defaultConfigFile = new File(ecoCreature.dataFolder, DEFAULT_CONFIG_FILE);
        File oldConfigFile = new File(ecoCreature.dataFolder, OLD_CONFIG_FILE);

        if (defaultConfigFile.exists()) {
            defaultConfig.load(defaultConfigFile);
        }
        else if (oldConfigFile.exists()) {
            defaultConfig.load(oldConfigFile);
            ecoCreature.getEcoLogger().warning("Using old config file format " + OLD_CONFIG_FILE + ".");
            ecoCreature.getEcoLogger().warning("Backup or delete the old config to generate the new " + DEFAULT_CONFIG_FILE + ".");
        }
        else {
            defaultConfig = getConfig(defaultConfigFile);
        }

        ecoCreature.getEcoLogger().info("Loaded config defaults.");
        ecoMessageManager defaultMessageManager = loadMessageConfig(defaultConfig);
        ecoRewardManager defaultRewardManager = loadRewardConfig(defaultConfig);
        ecoCreature.messageManagers.put(DEFAULT_WORLD, defaultMessageManager);
        ecoCreature.rewardManagers.put(DEFAULT_WORLD, defaultRewardManager);

        for (World world : plugin.getServer().getWorlds()) {

            File worldConfigFile = new File(ecoCreature.dataWorldsFolder, world.getName() + ".yml");
            FileConfiguration worldConfig = new YamlConfiguration();

            if (worldConfigFile.exists()) {
                worldConfig = getConfig(worldConfigFile);
                ecoCreature.getEcoLogger().info("Loaded config for " + world.getName() + " world.");
                ecoCreature.messageManagers.put(world.getName(), loadMessageConfig(worldConfig));
                ecoCreature.rewardManagers.put(world.getName(), loadRewardConfig(worldConfig));
            }
            else {
                ecoCreature.messageManagers.put(world.getName(), defaultMessageManager);
                ecoCreature.rewardManagers.put(world.getName(), defaultRewardManager);
            }

        }
    }

    public ecoMessageManager loadMessageConfig(FileConfiguration config)
    {
        ecoMessageManager messageManager = new ecoMessageManager(plugin);

        messageManager.shouldOutputMessages = config.getBoolean("System.Messages.Output", true);
        messageManager.noBowRewardMessage = new ecoMessage(convertMessage(config.getString("System.Messages.NoBowMessage", ecoMessageManager.NO_BOW_REWARD_MESSAGE)), true);
        messageManager.noCampMessage = new ecoMessage(convertMessage(config.getString("System.Messages.NoCampMessage", ecoMessageManager.NO_CAMP_MESSAGE)), config.getBoolean("System.Messages.Spawner", false));
        messageManager.deathPenaltyMessage = new ecoMessage(convertMessage(config.getString("System.Messages.DeathPenaltyMessage", ecoMessageManager.DEATH_PENALTY_MESSAGE)), true);
        messageManager.pvpRewardMessage = new ecoMessage(convertMessage(config.getString("System.Messages.PVPRewardMessage", ecoMessageManager.PVP_REWARD_MESSAGE)), true);

        return messageManager;
    }

    public ecoRewardManager loadRewardConfig(FileConfiguration config)
    {
        ecoRewardManager rewardManager = new ecoRewardManager(plugin);

        isEnabled = config.getBoolean("DidYou.Read.Understand.Configure", true);

        debug = config.getBoolean("System.debug", false) || debug;

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
        rewardManager.hasDTPRewards = config.getBoolean("System.Hunting.DTPRewards", true);
        rewardManager.dtpPenaltyAmount = config.getDouble("System.Hunting.DTPDeathStreakPenalty", 5.0D);
        rewardManager.dtpRewardAmount = config.getDouble("System.Hunting.DTPKillStreakPenalty", 10.0D);
        rewardManager.noFarm = config.getBoolean("System.Hunting.NoFarm", false);
        rewardManager.noFarmFire = config.getBoolean("System.Hunting.NoFarmFire", false);
        rewardManager.hasMobArenaRewards = config.getBoolean("System.Hunting.MobArenaRewards", false);

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
                    ecoCreature.getEcoLogger().warning("Skipping unknown environment name: " + environment);
                }
            }
        }

        ConfigurationSection rewardTable = config.getConfigurationSection("RewardTable");
        if (rewardTable != null) {
            for (String rewardName : rewardTable.getKeys(false)) {
                ecoReward reward = new ecoReward();
                reward.setRewardName(rewardName);
                reward.setRewardType(RewardType.fromName(rewardName));

                ConfigurationSection rewardConfig = rewardTable.getConfigurationSection(rewardName);
                reward.setDrops(parseDrops(rewardConfig.getString("Drops"), rewardManager.isFixedDrops));
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
                        ecoCreature.getEcoLogger().warning("Could not parse exp for " + rewardName);
                    }
                }

                reward.setNoRewardMessage(new ecoMessage(convertMessage(rewardConfig.getString("NoReward_Message", ecoMessageManager.NO_REWARD_MESSAGE)), config.getBoolean("System.Messages.NoReward", false)));
                reward.setRewardMessage(new ecoMessage(convertMessage(rewardConfig.getString("Reward_Message", ecoMessageManager.REWARD_MESSAGE)), true));
                reward.setPenaltyMessage(new ecoMessage(convertMessage(rewardConfig.getString("Penalty_Message", ecoMessageManager.PENALTY_MESSAGE)), true));

                rewardManager.rewards.put(reward.getRewardType(), reward);
            }
        }

        return rewardManager;
    }

    private FileConfiguration getConfig(File file) throws Exception
    {
        FileConfiguration config = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdir();
            file.createNewFile();
            InputStream inputStream = ecoCreature.class.getResourceAsStream("/" + DEFAULT_CONFIG_FILE);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);

            inputStream.close();
            outputStream.close();

            ecoCreature.getEcoLogger().info("Default settings file written: " + DEFAULT_CONFIG_FILE);
        }

        config.load(file);
        return config;
    }

    private static String convertMessage(String message)
    {
        if (message != null) {
            return message.replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
        }

        return null;
    }

    private List<ecoDrop> parseDrops(String dropsString, Boolean isFixedDrops)
    {
        List<ecoDrop> drops = new ArrayList<ecoDrop>();

        if (dropsString != null && !dropsString.isEmpty()) {
            try {
                for (String dropString : dropsString.split(";")) {
                    ecoDrop drop = new ecoDrop();
                    String[] dropParts = dropString.split(":");
                    String[] itemParts = dropParts[0].split(",");
                    // check for enchantment
                    if (itemParts.length > 1) {
                        for (int i = 1; i < itemParts.length; i++) {
                            String[] enchantParts = itemParts[i].split("\\.");
                            drop.addEnchantment(Enchantment.getByName(enchantParts[0].toUpperCase()), enchantParts.length > 1 ? Integer.parseInt(enchantParts[1]) : 1);
                        }
                    }
                    // check for data id
                    String[] itemSubParts = itemParts[0].split("\\.");
                    drop.setItem(Material.matchMaterial(itemSubParts[0]));
                    if (drop.getItem() == null) throw new Exception();
                    drop.setData(itemSubParts.length > 1 ? Byte.parseByte(itemSubParts[1]) : null);
                    drop.setDurability(itemSubParts.length > 2 ? Short.parseShort(itemSubParts[2]) : null);
                    // check for range on amount
                    String[] amountRange = dropParts[1].split("-");
                    if (amountRange.length == 2) {
                        drop.setMinAmount(Integer.parseInt(amountRange[0]));
                        drop.setMaxAmount(Integer.parseInt(amountRange[1]));
                    }
                    else {
                        drop.setMaxAmount(Integer.parseInt(dropParts[1]));
                    }
                    drop.setPercentage(Double.parseDouble(dropParts[2]));
                    drop.setIsFixedDrops(isFixedDrops);
                    drops.add(drop);
                }
            }
            catch (Exception exception) {
                ecoCreature.getEcoLogger().warning("Failed to parse drops: " + dropsString);
            }
        }

        return drops;
    }
}