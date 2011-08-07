package se.crafted.chrisb.ecoCreature.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.util.config.Configuration;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoConstants
{
    public static File Configuration;
    public static String pluginDirectory;
    public static boolean isConfigurationEnabled;
    public static String economyCore;
    public static boolean isIntegerCurrency;
    public static boolean shouldAllowCamping;
    public static boolean shouldOverrideDrops;
    public static boolean isFixedDrops;
    public static boolean shouldClearCampDrops;
    public static int campRadius;
    public static boolean hasBowRewards;
    public static boolean hasDeathPenalty;
    public static boolean hasPenaltyType;
    public static double penaltyAmount;
    public static boolean shouldAllowUnderSeaLVL;
    public static boolean shouldOutputMessages;
    public static boolean shouldOutputNoRewardMessage;
    public static boolean shouldOutputSpawnerMessage;
    public static String shouldOuputNoBowMessage;
    public static String noCampMessage;
    public static String deathPenaltyMessage;
    public static boolean isWolverineMode;
    public static HashMap<String, Double> Gain = new HashMap<String, Double>();
    public static String[] Creatures = { "Creeper", "Skeleton", "Zombie", "Spider", "PigZombie", "Ghast", "Slime", "Giant", "Chicken", "Cow", "Pig", "Sheep", "Squid", "Wolf", "Monster", "Spawner" };
    public static double[][][] CreatureDrop = new double[Creatures.length][][];
    public static double[] CreatureCoinMin = new double[Creatures.length];
    public static double[] CreatureCoinMax = new double[Creatures.length];
    public static double[] CreatureCoinPercentage = new double[Creatures.length];
    public static String[] CreatureNoRewardMessage = new String[Creatures.length];
    public static String[] CreatureRewardMessage = new String[Creatures.length];
    public static String[] CreaturePenaltyMessage = new String[Creatures.length];

    public static void load(Configuration configuration)
    {
        configuration.load();
        isConfigurationEnabled = configuration.getBoolean("DidYou.Read.Understand.Configure", false);

        economyCore = configuration.getString("System.Economy.Core");
        isIntegerCurrency = configuration.getBoolean("System.Economy.IntegerCurrency", false);

        shouldAllowCamping = configuration.getBoolean("System.Hunting.AllowCamping", false);
        shouldClearCampDrops = configuration.getBoolean("System.Hunting.ClearCampDrops", false);
        shouldOverrideDrops = configuration.getBoolean("System.Hunting.OverrideDrops", false);
        isFixedDrops = configuration.getBoolean("System.Hunting.FixedDrops", false);
        campRadius = configuration.getInt("System.Hunting.CampRadius", 0);
        hasBowRewards = configuration.getBoolean("System.Hunting.BowRewards", false);
        hasDeathPenalty = configuration.getBoolean("System.Hunting.PenalizeDeath", false);
        hasPenaltyType = configuration.getBoolean("System.Hunting.PenalizeType", false);
        penaltyAmount = configuration.getDouble("System.Hunting.PenalizeAmount", 0.0D);
        shouldAllowUnderSeaLVL = configuration.getBoolean("System.Hunting.AllowUnderSeaLVL", false);
        isWolverineMode = configuration.getBoolean("System.Hunting.WolverineMode", false);

        shouldOutputMessages = configuration.getBoolean("System.Messages.Output", false);
        shouldOutputNoRewardMessage = configuration.getBoolean("System.Messages.NoReward", false);
        shouldOutputSpawnerMessage = configuration.getBoolean("System.Messages.Spawner", false);
        shouldOuputNoBowMessage = configuration.getString("System.Messages.NoBowMessage").replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
        noCampMessage = configuration.getString("System.Messages.NoCampMessage").replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
        deathPenaltyMessage = configuration.getString("System.Messages.DeathPenaltyMessage").replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");

        List<String> gainList = configuration.getKeys("Gain");
        Iterator<String> gainIterator = gainList.iterator();
        while (gainIterator.hasNext()) {
            String gainTypeName = (String) gainIterator.next();
            Gain.put(gainTypeName, Double.valueOf(configuration.getDouble("Gain." + gainTypeName + ".Amount", 0.0D)));
        }

        for (int i = 0; i < Creatures.length; i++) {
            loadCreatureDrops(configuration.getString("RewardTable." + Creatures[i] + ".Drops", ""), i);
            CreatureCoinMin[i] = configuration.getDouble("RewardTable." + Creatures[i] + ".Coin_Minimum", CreatureCoinMin[i]);
            CreatureCoinMax[i] = configuration.getDouble("RewardTable." + Creatures[i] + ".Coin_Maximum", CreatureCoinMax[i]);
            CreatureCoinPercentage[i] = configuration.getDouble("RewardTable." + Creatures[i] + ".Coin_Percent", CreatureCoinPercentage[i]);
            CreatureNoRewardMessage[i] = configuration.getString("RewardTable." + Creatures[i] + ".NoReward_Message", CreatureNoRewardMessage[i]).replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
            CreatureRewardMessage[i] = configuration.getString("RewardTable." + Creatures[i] + ".Reward_Message", CreatureRewardMessage[i]).replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
            CreaturePenaltyMessage[i] = configuration.getString("RewardTable." + Creatures[i] + ".Penalty_Message", CreaturePenaltyMessage[i]).replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
        }
    }

    private static void loadCreatureDrops(String pathString, int creatureIndex)
    {
        String[] dropList = pathString.split(";");
        try {
            if (dropList.length > 0) {
                CreatureDrop[creatureIndex] = new double[dropList.length][3];
                for (int i = 0; i < dropList.length; i++) {
                    String[] dropItemData = dropList[i].split(":");
                    if (dropItemData.length != 3)
                        continue;
                    CreatureDrop[creatureIndex][i][0] = Integer.parseInt(dropItemData[0]);
                    CreatureDrop[creatureIndex][i][1] = Integer.parseInt(dropItemData[1]);
                    CreatureDrop[creatureIndex][i][2] = Double.parseDouble(dropItemData[2]);
                }
                return;
            }
        }
        catch (Exception exception) {
            ecoCreature.logger.log(Level.WARNING, "[ecoCreature] Failed to load drops for: " + Creatures[creatureIndex]);
            CreatureDrop[creatureIndex] = ((double[][]) null);
        }
    }
}