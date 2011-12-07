package se.crafted.chrisb.ecoCreature.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.util.config.Configuration;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoDrop;
import se.crafted.chrisb.ecoCreature.models.ecoMessage;
import se.crafted.chrisb.ecoCreature.models.ecoReward;
import se.crafted.chrisb.ecoCreature.models.ecoReward.RewardType;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil.TimePeriod;
import se.crafted.chrisb.ecoCreature.utils.ecoLogger;

public class ecoConfigManager
{
    private static final String OLD_CONFIG_FILE = "ecoCreature.yml";
    private static final String DEFAULT_CONFIG_FILE = "default.yml";

    private static final String DEFAULT_WORLD = "default";

    private final ecoCreature plugin;
    private final ecoLogger log;
    private Boolean isEnabled;

    public static boolean debug = false;

    public ecoConfigManager(ecoCreature plugin)
    {
        this.plugin = plugin;
        log = this.plugin.getLogger();
    }

    public Boolean isEnabled()
    {
        return isEnabled;
    }

    public void load() throws IOException
    {
        Configuration defaultConfig;

        File defaultConfigFile = new File(ecoCreature.dataFolder, DEFAULT_CONFIG_FILE);
        File oldConfigFile = new File(ecoCreature.dataFolder, OLD_CONFIG_FILE);

        if (defaultConfigFile.exists()) {
            defaultConfig = new Configuration(defaultConfigFile);
        }
        else if (oldConfigFile.exists()) {
            defaultConfig = new Configuration(oldConfigFile);
            log.warning("Using old config file format " + OLD_CONFIG_FILE + ".");
            log.warning("Backup or delete the old config to generate the new " + DEFAULT_CONFIG_FILE+ ".");
        }
        else {
            defaultConfig = getConfig(defaultConfigFile);
        }

        defaultConfig.load();
        log.info("Loaded config defaults.");
        ecoMessageManager defaultMessageManager = loadMessageConfig(defaultConfig);
        ecoRewardManager defaultRewardManager = loadRewardConfig(defaultConfig);
        ecoCreature.messageManagers.put(DEFAULT_WORLD, defaultMessageManager);
        ecoCreature.rewardManagers.put(DEFAULT_WORLD, defaultRewardManager);

        for (World world : plugin.getServer().getWorlds()) {

            File worldConfigFile = new File(ecoCreature.dataWorldsFolder, world.getName() + ".yml");
            Configuration worldConfig;

            if (worldConfigFile.exists()) {
                worldConfig = getConfig(worldConfigFile);
                worldConfig.load();
                log.info("Loaded config for " + world.getName() + " world.");
                ecoCreature.messageManagers.put(world.getName(), loadMessageConfig(worldConfig));
                ecoCreature.rewardManagers.put(world.getName(), loadRewardConfig(worldConfig));
            }
            else {
                ecoCreature.messageManagers.put(world.getName(), defaultMessageManager);
                ecoCreature.rewardManagers.put(world.getName(), defaultRewardManager);
            }

        }
    }

    public ecoMessageManager loadMessageConfig(Configuration config)
    {
        ecoMessageManager messageManager = new ecoMessageManager(plugin);

        messageManager.shouldOutputMessages = config.getBoolean("System.Messages.Output", true);
        messageManager.noBowRewardMessage = new ecoMessage(convertMessage(config.getString("System.Messages.NoBowMessage", ecoMessageManager.NO_BOW_REWARD_MESSAGE)), true);
        messageManager.noCampMessage = new ecoMessage(convertMessage(config.getString("System.Messages.NoCampMessage", ecoMessageManager.NO_CAMP_MESSAGE)), config.getBoolean("System.Messages.Spawner", false));
        messageManager.deathPenaltyMessage = new ecoMessage(convertMessage(config.getString("System.Messages.DeathPenaltyMessage", ecoMessageManager.DEATH_PENALTY_MESSAGE)), true);
        messageManager.pvpRewardMessage = new ecoMessage(convertMessage(config.getString("System.Messages.PVPRewardMessage", ecoMessageManager.PVP_REWARD_MESSAGE)), true);

        return messageManager;
    }

    public ecoRewardManager loadRewardConfig(Configuration config)
    {
        ecoRewardManager rewardManager = new ecoRewardManager(plugin);

        isEnabled = config.getBoolean("DidYou.Read.Understand.Configure", true);

        debug = config.getBoolean("System.debug", false) || debug;

        rewardManager.isIntegerCurrency = config.getBoolean("System.Economy.IntegerCurrency", false);

        rewardManager.canCampSpawner = config.getBoolean("System.Hunting.AllowCamping", false);
        rewardManager.shouldClearCampDrops = config.getBoolean("System.Hunting.ClearCampDrops", true);
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
        rewardManager.hasMobArenaAwards = config.getBoolean("System.Hunting.MobArenaAwards", false);

        if (config.getKeys("Gain.Groups") != null) {
            for (String group : config.getKeys("Gain.Groups")) {
                rewardManager.groupMultiplier.put(group.toLowerCase(), Double.valueOf(config.getDouble("Gain.Groups." + group + ".Amount", 0.0D)));
            }
        }

        if (config.getKeys("Gain.Time") != null) {
            for (String period : config.getKeys("Gain.Time")) {
                rewardManager.timeMultiplier.put(TimePeriod.fromName(period), Double.valueOf(config.getDouble("Gain.Time." + period + ".Amount", 1.0D)));
            }
        }

        if (config.getKeys("Gain.Environment") != null) {
            for (String environment : config.getKeys("Gain.Environment")) {
                try {
                    rewardManager.envMultiplier.put(Environment.valueOf(environment.toUpperCase()), Double.valueOf(config.getDouble("Gain.Environment." + environment + ".Amount", 1.0D)));
                }
                catch (Exception e) {
                    log.warning("Skipping unknown environment name: " + environment);
                }
            }
        }

        if (config.getKeys("RewardTable") != null) {
            for (String rewardName : config.getKeys("RewardTable")) {
                ecoReward reward = new ecoReward();
                reward.setRewardName(rewardName);
                reward.setRewardType(RewardType.fromName(rewardName));

                String root = "RewardTable." + rewardName;
                reward.setDrops(parseDrops(config.getString(root + ".Drops"), rewardManager.isFixedDrops));
                reward.setCoinMax(config.getDouble(root + ".Coin_Maximum", 0));
                reward.setCoinMin(config.getDouble(root + ".Coin_Minimum", 0));
                reward.setCoinPercentage(config.getDouble(root + ".Coin_Percent", 0));

                reward.setNoRewardMessage(new ecoMessage(convertMessage(config.getString(root + ".NoReward_Message", ecoMessageManager.NO_REWARD_MESSAGE)), config.getBoolean("System.Messages.NoReward", false)));
                reward.setRewardMessage(new ecoMessage(convertMessage(config.getString(root + ".Reward_Message", ecoMessageManager.REWARD_MESSAGE)), true));
                reward.setPenaltyMessage(new ecoMessage(convertMessage(config.getString(root + ".Penalty_Message", ecoMessageManager.PENALTY_MESSAGE)), true));

                rewardManager.rewards.put(reward.getRewardType(), reward);
            }
        }

        return rewardManager;
    }

    private Configuration getConfig(File configFile) throws IOException
    {
        if (!configFile.exists()) {
            InputStream inputStream = ecoCreature.class.getResourceAsStream("/" + DEFAULT_CONFIG_FILE);
            FileOutputStream outputStream = new FileOutputStream(configFile);

            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);

            inputStream.close();
            outputStream.close();

            log.info("Default settings file written: " + DEFAULT_CONFIG_FILE);
        }

        return new Configuration(new File(configFile.getPath()));
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
                    String[] dropParts = dropString.split(":");
                    ecoDrop drop = new ecoDrop();
                    String[] itemParts = dropParts[0].split("\\.");
                    drop.setItem(Material.matchMaterial(itemParts.length > 0 ? itemParts[0] : dropParts[0]));
                    if (drop.getItem() == null) throw new Exception();
                    drop.setData(itemParts.length > 1 ? Byte.parseByte(itemParts[1]) : 0);
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
                log.warning("Failed to parse drops: " + dropsString);
            }
        }

        return drops;
    }
}