package se.crafted.chrisb.ecoCreature.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoDrop;
import se.crafted.chrisb.ecoCreature.models.ecoMessage;
import se.crafted.chrisb.ecoCreature.models.ecoReward;
import se.crafted.chrisb.ecoCreature.utils.ecoEntityUtil.TIME_PERIOD;
import se.crafted.chrisb.ecoCreature.utils.ecoLogger;

public class ecoConfigManager
{
    private static final String OLD_CONFIG_FILE = "ecoCreature.yml";
    private static final String DEFAULT_CONFIG = "default.yml";

    private ecoCreature plugin;
    private ecoLogger log;
    private Configuration config;
    private Boolean isOldConfig;
    private Boolean isEnabled;

    public ecoConfigManager(ecoCreature plugin)
    {
        this.plugin = plugin;
        log = plugin.getLogger();
        config = null;
    }

    public Boolean isEnabled()
    {
        return isEnabled;
    }

    public void load() throws IOException
    {
        config = getConfig();
        config.load();

        ecoMessageManager messageManager = new ecoMessageManager(plugin);
        ecoRewardManager rewardManager = new ecoRewardManager(plugin);

        isEnabled = config.getBoolean("DidYou.Read.Understand.Configure", false);

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
        rewardManager.noFarm = config.getBoolean("System.Hunting.NoFarm", false);

        messageManager.shouldOutputMessages = config.getBoolean("System.Messages.Output", true);
        messageManager.noBowRewardMessage = new ecoMessage(convertMessage(config.getString("System.Messages.NoBowMessage", ecoMessageManager.NO_BOW_REWARD_MESSAGE)), true);
        messageManager.noCampMessage = new ecoMessage(convertMessage(config.getString("System.Messages.NoCampMessage", ecoMessageManager.NO_CAMP_MESSAGE)), config.getBoolean("System.Messages.Spawner", false));
        messageManager.deathPenaltyMessage = new ecoMessage(convertMessage(config.getString("System.Messages.DeathPenaltyMessage", ecoMessageManager.DEATH_PENALTY_MESSAGE)), true);
        messageManager.pvpRewardMessage = new ecoMessage(convertMessage(config.getString("System.Messages.PVPRewardMessage", ecoMessageManager.PVP_REWARD_MESSAGE)), true);

        for (String groupMultiplierName : config.getKeys("Gain.Groups")) {
            rewardManager.groupMultiplier.put(groupMultiplierName.toLowerCase(), Double.valueOf(config.getDouble("Gain.Groups." + groupMultiplierName + ".Amount", 0.0D)));
        }

        rewardManager.timeMultiplier.put(TIME_PERIOD.SUNRISE, Double.valueOf(config.getDouble("Gain.Time.Sunrise.Amount", 1.0D)));
        rewardManager.timeMultiplier.put(TIME_PERIOD.DAY, Double.valueOf(config.getDouble("Gain.Time.Day.Amount", 1.0D)));
        rewardManager.timeMultiplier.put(TIME_PERIOD.SUNSET, Double.valueOf(config.getDouble("Gain.Time.Sunset.Amount", 1.0D)));
        rewardManager.timeMultiplier.put(TIME_PERIOD.DUSK, Double.valueOf(config.getDouble("Gain.Time.Dusk.Amount", 1.125D)));
        rewardManager.timeMultiplier.put(TIME_PERIOD.NIGHT, Double.valueOf(config.getDouble("Gain.Time.Night.Amount", 1.25D)));
        rewardManager.timeMultiplier.put(TIME_PERIOD.DAWN, Double.valueOf(config.getDouble("Gain.Time.Dawn.Amount", 1.125D)));
        rewardManager.timeMultiplier.put(TIME_PERIOD.IDENTITY, 1.0D);

        rewardManager.rewards = new HashMap<CreatureType, ecoReward>();
        for (String creatureName : config.getKeys("RewardTable")) {
            ecoReward reward = new ecoReward();
            reward.setCreatureName(creatureName);
            reward.setCreatureType(CreatureType.fromName(creatureName));

            String root = "RewardTable." + creatureName;
            reward.setDrops(parseDrops(config.getString(root + ".Drops"), rewardManager.isFixedDrops));
            reward.setCoinMax(config.getDouble(root + ".Coin_Maximum", 0));
            reward.setCoinMin(config.getDouble(root + ".Coin_Minimum", 5));
            reward.setCoinPercentage(config.getDouble(root + ".Coin_Percent", 50));

            reward.setNoRewardMessage(new ecoMessage(convertMessage(config.getString(root + ".NoReward_Message", ecoMessageManager.NO_REWARD_MESSAGE)), config.getBoolean("System.Messages.NoReward", false)));
            reward.setRewardMessage(new ecoMessage(convertMessage(config.getString(root + ".Reward_Message", ecoMessageManager.REWARD_MESSAGE)), true));
            reward.setPenaltyMessage(new ecoMessage(convertMessage(config.getString(root + ".Penalty_Message", ecoMessageManager.PENALTY_MESSAGE)), true));

            if (creatureName.equals("Spawner")) {
                rewardManager.spawnerReward = reward;
            }
            else {
                rewardManager.rewards.put(reward.getCreatureType(), reward);
            }
        }

        ecoCreature.messageManagers.put("default", messageManager);
        ecoCreature.rewardManagers.put("default", rewardManager);
    }

    private Configuration getConfig() throws IOException
    {
        Configuration config = null;

        File configFile = new File(ecoCreature.dataFolder, DEFAULT_CONFIG);
        File oldConfigFile = new File(ecoCreature.dataFolder, OLD_CONFIG_FILE);

        if (configFile.exists()) {
            config = new Configuration(configFile);
            isOldConfig = false;
        }
        else if (oldConfigFile.exists()) {
            config = new Configuration(oldConfigFile);
            isOldConfig = true;
        }
        else {
            InputStream inputStream = ecoCreature.class.getResourceAsStream(DEFAULT_CONFIG);
            FileOutputStream outputStream = new FileOutputStream(configFile);

            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);

            inputStream.close();
            outputStream.close();

            log.info("Default settings file written: " + DEFAULT_CONFIG);

            config = new Configuration(new File(ecoCreature.dataFolder, DEFAULT_CONFIG));
            isOldConfig = false;
        }

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
        if (dropsString != null && !dropsString.isEmpty()) {
            List<ecoDrop> drops = new ArrayList<ecoDrop>();

            try {
                for (String dropString : dropsString.split(";")) {
                    String[] dropParts = dropString.split(":");
                    ecoDrop drop = new ecoDrop();
                    drop.setItem(Material.getMaterial(Integer.parseInt(dropParts[0])));
                    drop.setAmount(Integer.parseInt(dropParts[1]));
                    drop.setPercentage(Double.parseDouble(dropParts[2]));
                    drop.setIsFixedDrops(isFixedDrops);
                    drops.add(drop);
                }

                return drops;
            }
            catch (Exception exception) {
                log.warning("Failed to parse drops: " + dropsString);
            }
        }

        return null;
    }
}