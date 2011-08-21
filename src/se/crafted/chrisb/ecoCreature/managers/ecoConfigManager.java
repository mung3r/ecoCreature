package se.crafted.chrisb.ecoCreature.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;

import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.models.ecoDrop;
import se.crafted.chrisb.ecoCreature.models.ecoReward;

public class ecoConfigManager
{
    private static final String MAIN_CONFIG_FILE = "ecoCreature.yml";

    private ecoCreature plugin;
    private Configuration config;
    private Boolean isEnabled;

    public ecoConfigManager(ecoCreature plugin)
    {
        this.plugin = plugin;
        config = null;
    }

    public Boolean isEnabled()
    {
        return isEnabled;
    }

    public void load() throws Exception
    {
        config = getConfig(MAIN_CONFIG_FILE);
        config.load();

        isEnabled = config.getBoolean("DidYou.Read.Understand.Configure", false);

        ecoRewardManager.isIntegerCurrency = config.getBoolean("System.Economy.IntegerCurrency", false);
        ecoRewardManager.canCampSpawner = config.getBoolean("System.Hunting.AllowCamping", false);
        ecoRewardManager.shouldClearCampDrops = config.getBoolean("System.Hunting.ClearCampDrops", true);
        ecoRewardManager.shouldOverrideDrops = config.getBoolean("System.Hunting.OverrideDrops", true);
        ecoRewardManager.isFixedDrops = config.getBoolean("System.Hunting.FixedDrops", false);
        ecoRewardManager.campRadius = config.getInt("System.Hunting.CampRadius", 15);
        ecoRewardManager.hasBowRewards = config.getBoolean("System.Hunting.BowRewards", true);
        ecoRewardManager.hasDeathPenalty = config.getBoolean("System.Hunting.PenalizeDeath", false);
        ecoRewardManager.isPercentPenalty = config.getBoolean("System.Hunting.PenalizeType", false);
        ecoRewardManager.penaltyAmount = config.getDouble("System.Hunting.PenalizeAmount", 0.0D);
        ecoRewardManager.canHuntUnderSeaLevel = config.getBoolean("System.Hunting.AllowUnderSeaLVL", true);
        ecoRewardManager.isWolverineMode = config.getBoolean("System.Hunting.WolverineMode", true);

        ecoRewardManager.shouldOutputMessages = config.getBoolean("System.Messages.Output", true);
        ecoRewardManager.shouldOutputNoRewardMessage = config.getBoolean("System.Messages.NoReward", false);
        ecoRewardManager.shouldOutputSpawnerMessage = config.getBoolean("System.Messages.Spawner", false);
        ecoRewardManager.noBowRewardMessage = convertMessage(config.getString("System.Messages.NoBowMessage"));
        ecoRewardManager.noCampMessage = convertMessage(config.getString("System.Messages.NoCampMessage"));
        ecoRewardManager.deathPenaltyMessage = convertMessage(config.getString("System.Messages.DeathPenaltyMessage"));

        for (String groupMultiplierName : config.getKeys("Gain")) {
            ecoRewardManager.groupMultiplier.put(groupMultiplierName, Double.valueOf(config.getDouble("Gain." + groupMultiplierName + ".Amount", 0.0D)));
        }

        ecoRewardManager.rewards = new HashMap<CreatureType, ecoReward>();
        for (String rewardName : config.getKeys("RewardTable")) {
            ecoReward reward = new ecoReward();
            reward.setRewardName(rewardName);
            reward.setCreatureType(CreatureType.fromName(rewardName));

            String root = "RewardTable." + rewardName;
            reward.setDrops(parseDrops(config.getString(root + ".Drops")));
            reward.setCoinMax(config.getDouble(root + ".Coin_Maximum", 0));
            reward.setCoinMin(config.getDouble(root + ".Coin_Minimum", 5));
            reward.setCoinPercentage(config.getDouble(root + ".Coin_Percent", 50));
            reward.setNoRewardMessage(convertMessage(config.getString(root + ".NoReward_Message")));
            reward.setRewardMessage(convertMessage(config.getString(root + ".Reward_Message")));
            reward.setPenaltyMessage(convertMessage(config.getString(root + ".Penalty_Message")));

            if (rewardName.equals("Spawner")) {
                ecoRewardManager.spawnerReward = reward;
            }
            else {
                ecoRewardManager.rewards.put(reward.getCreatureType(), reward);
            }
        }
    }

    private Configuration getConfig(String filename) throws Exception
    {
        File file = new File(ecoCreature.dataFolder, filename);
        if (!file.exists()) {
            InputStream inputStream = ecoCreature.class.getResourceAsStream("/settings/" + filename);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);

            inputStream.close();
            outputStream.close();

            ecoCreature.logger.log(Level.INFO, "[ecoCreature] Default settings file written: " + filename);
        }

        return new Configuration(new File(ecoCreature.dataFolder, filename));
    }

    private static String convertMessage(String message)
    {
        if (message != null) {
            return message.replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
        }

        return null;
    }

    private static List<ecoDrop> parseDrops(String dropsString)
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
                    drops.add(drop);
                }

                return drops;
            }
            catch (Exception exception) {
                ecoCreature.logger.log(Level.WARNING, "[ecoCreature] Failed to parse drops: " + dropsString);
            }
        }

        return null;
    }
}