package se.crafted.chrisb.ecoCreature.managers;

import java.io.File;
import java.io.FileOutputStream;
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
    private static final String MAIN_CONFIG_FILE = "ecoCreature.yml";

    private ecoCreature plugin;
    private ecoLogger log;
    private Configuration config;
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
        ecoRewardManager.campRadius = config.getInt("System.Hunting.CampRadius", 7);
        ecoRewardManager.hasBowRewards = config.getBoolean("System.Hunting.BowRewards", true);
        ecoRewardManager.hasDeathPenalty = config.getBoolean("System.Hunting.PenalizeDeath", false);
        ecoRewardManager.hasPVPReward = config.getBoolean("System.Hunting.PVPReward", true);
        ecoRewardManager.isPercentPenalty = config.getBoolean("System.Hunting.PenalizeType", true);
        ecoRewardManager.isPercentPvpReward = config.getBoolean("System.Hunting.PVPRewardType", true);
        ecoRewardManager.penaltyAmount = config.getDouble("System.Hunting.PenalizeAmount", 0.05D);
        ecoRewardManager.pvpRewardAmount = config.getDouble("System.Hunting.PenalizeAmount", 0.05D);
        ecoRewardManager.canHuntUnderSeaLevel = config.getBoolean("System.Hunting.AllowUnderSeaLVL", true);
        ecoRewardManager.isWolverineMode = config.getBoolean("System.Hunting.WolverineMode", true);
        ecoRewardManager.noFarm = config.getBoolean("System.Hunting.NoFarm", false);

        ecoMessageManager.shouldOutputMessages = config.getBoolean("System.Messages.Output", true);
        ecoMessageManager.noBowRewardMessage = new ecoMessage(convertMessage(config.getString("System.Messages.NoBowMessage")), true);
        ecoMessageManager.noCampMessage = new ecoMessage(convertMessage(config.getString("System.Messages.NoCampMessage")), config.getBoolean("System.Messages.Spawner", false));
        ecoMessageManager.deathPenaltyMessage = new ecoMessage(convertMessage(config.getString("System.Messages.DeathPenaltyMessage")), true);
        ecoMessageManager.pvpRewardMessage = new ecoMessage(convertMessage(config.getString("System.Messages.PVPRewardMessage")), true);

        for (String groupMultiplierName : config.getKeys("Gain")) {
            ecoRewardManager.groupMultiplier.put(groupMultiplierName.toLowerCase(), Double.valueOf(config.getDouble("Gain." + groupMultiplierName + ".Amount", 0.0D)));
        }

        ecoRewardManager.timeMultiplier.put(TIME_PERIOD.SUNRISE, Double.valueOf(config.getDouble("Time.Sunrise.Amount", 1.0D)));
        ecoRewardManager.timeMultiplier.put(TIME_PERIOD.DAY, Double.valueOf(config.getDouble("Time.Day.Amount", 1.0D)));
        ecoRewardManager.timeMultiplier.put(TIME_PERIOD.SUNSET, Double.valueOf(config.getDouble("Time.Sunset.Amount", 1.0D)));
        ecoRewardManager.timeMultiplier.put(TIME_PERIOD.DUSK, Double.valueOf(config.getDouble("Time.Dusk.Amount", 1.125D)));
        ecoRewardManager.timeMultiplier.put(TIME_PERIOD.NIGHT, Double.valueOf(config.getDouble("Time.Night.Amount", 1.25D)));
        ecoRewardManager.timeMultiplier.put(TIME_PERIOD.DAWN, Double.valueOf(config.getDouble("Time.Dawn.Amount", 1.125D)));
        ecoRewardManager.timeMultiplier.put(TIME_PERIOD.IDENTITY, 1.0D);

        ecoRewardManager.rewards = new HashMap<CreatureType, ecoReward>();
        for (String creatureName : config.getKeys("RewardTable")) {
            ecoReward reward = new ecoReward();
            reward.setCreatureName(creatureName);
            reward.setCreatureType(CreatureType.fromName(creatureName));

            String root = "RewardTable." + creatureName;
            reward.setDrops(parseDrops(config.getString(root + ".Drops")));
            reward.setCoinMax(config.getDouble(root + ".Coin_Maximum", 0));
            reward.setCoinMin(config.getDouble(root + ".Coin_Minimum", 5));
            reward.setCoinPercentage(config.getDouble(root + ".Coin_Percent", 50));

            reward.setNoRewardMessage(new ecoMessage(convertMessage(config.getString(root + ".NoReward_Message")), config.getBoolean("System.Messages.NoReward", false)));
            reward.setRewardMessage(new ecoMessage(convertMessage(config.getString(root + ".Reward_Message")), true));
            reward.setPenaltyMessage(new ecoMessage(convertMessage(config.getString(root + ".Penalty_Message")), true));

            if (creatureName.equals("Spawner")) {
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

            log.info("Default settings file written: " + filename);
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

    private List<ecoDrop> parseDrops(String dropsString)
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
                    drop.setIsFixedDrops(ecoRewardManager.isFixedDrops);
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