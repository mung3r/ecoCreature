package se.crafted.chrisb.ecoCreature.utils;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.util.config.Configuration;

import se.crafted.chrisb.ecoCreature.ecoCreature;

public class ecoConstants
{
  public static File Configuration;
  public static String Plugin_Directory;
  public static boolean SSA;
  public static String WEP;
  public static boolean IC;
  public static boolean AC;
  public static boolean OD;
  public static boolean FD;
  public static boolean CCD;
  public static int CR;
  public static boolean BR;
  public static boolean PD;
  public static boolean PT;
  public static double PA;
  public static boolean AUSL;
  public static boolean MO;
  public static boolean MNR;
  public static boolean MS;
  public static String MNB;
  public static String MNC;
  public static String MPD;
  public static boolean WR;
  public static boolean uQuestHooking;
  public static double uQuestRQP;
  public static HashMap<String, Double> Gain = new HashMap();
  public static String[] Creatures = { "Creeper", "Skeleton", "Zombie", "Spider", "PigZombie", "Ghast", "Slime", "Giant", "Chicken", "Cow", "Pig", "Sheep", "Squid", "Wolf", "Monster", "Spawner" };
  public static double[][][] CD = new double[Creatures.length][][];
  public static double[] CCMIN = new double[Creatures.length];
  public static double[] CCMAX = new double[Creatures.length];
  public static double[] CCP = new double[Creatures.length];
  public static String[] CNR = new String[Creatures.length];
  public static String[] CRM = new String[Creatures.length];
  public static String[] CPM = new String[Creatures.length];

  public static void load(Configuration paramConfiguration)
  {
    String str1 = "";
    paramConfiguration.load();
    SSA = paramConfiguration.getBoolean("DidYou.Read.Understand.Configure", false);
    WEP = paramConfiguration.getString("System.Economy.Core");
    IC = paramConfiguration.getBoolean("System.Economy.IntegerCurrency", false);
    AC = paramConfiguration.getBoolean("System.Hunting.AllowCamping", false);
    CCD = paramConfiguration.getBoolean("System.Hunting.ClearCampDrops", false);
    OD = paramConfiguration.getBoolean("System.Hunting.OverrideDrops", false);
    FD = paramConfiguration.getBoolean("System.Hunting.FixedDrops", false);
    CR = paramConfiguration.getInt("System.Hunting.CampRadius", 0);
    BR = paramConfiguration.getBoolean("System.Hunting.BowRewards", false);
    PD = paramConfiguration.getBoolean("System.Hunting.PenalizeDeath", false);
    PT = paramConfiguration.getBoolean("System.Hunting.PenalizeType", false);
    PA = paramConfiguration.getDouble("System.Hunting.PenalizeAmount", 0.0D);
    AUSL = paramConfiguration.getBoolean("System.Hunting.AllowUnderSeaLVL", false);
    MO = paramConfiguration.getBoolean("System.Messages.Output", false);
    MNR = paramConfiguration.getBoolean("System.Messages.NoReward", false);
    MS = paramConfiguration.getBoolean("System.Messages.Spawner", false);
    MNB = paramConfiguration.getString("System.Messages.NoBowMessage").replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
    MNC = paramConfiguration.getString("System.Messages.NoCampMessage").replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
    MPD = paramConfiguration.getString("System.Messages.DeathPenaltyMessage").replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
    uQuestHooking = paramConfiguration.getBoolean("System.ExtraHooks.uQuest", false);
    uQuestRQP = paramConfiguration.getDouble("System.ExtraHooks.uQuestRQP", 0.0D);
    WR = paramConfiguration.getBoolean("System.Hunting.WolverineMode", false);
    List localList = paramConfiguration.getKeys("Gain");
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      Gain.put(str2, Double.valueOf(paramConfiguration.getDouble("Gain." + str2 + ".Amount", 0.0D)));
    }
    for (int i = 0; i < Creatures.length; i++)
    {
      loadCreatureDrops(paramConfiguration.getString("RewardTable." + Creatures[i] + ".Drops", str1), i);
      CCMIN[i] = paramConfiguration.getDouble("RewardTable." + Creatures[i] + ".Coin_Minimum", CCMIN[i]);
      CCMAX[i] = paramConfiguration.getDouble("RewardTable." + Creatures[i] + ".Coin_Maximum", CCMAX[i]);
      CCP[i] = paramConfiguration.getDouble("RewardTable." + Creatures[i] + ".Coin_Percent", CCP[i]);
      CNR[i] = paramConfiguration.getString("RewardTable." + Creatures[i] + ".NoReward_Message", CNR[i]).replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
      CRM[i] = paramConfiguration.getString("RewardTable." + Creatures[i] + ".Reward_Message", CRM[i]).replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
      CPM[i] = paramConfiguration.getString("RewardTable." + Creatures[i] + ".Penalty_Message", CPM[i]).replaceAll("&&", "\b").replaceAll("&", "§").replaceAll("\b", "&");
    }
  }

  private static void loadCreatureDrops(String paramString, int paramInt)
  {
    String[] arrayOfString1 = paramString.split(";");
    try
    {
      if (arrayOfString1.length > 0)
      {
        CD[paramInt] = new double[arrayOfString1.length][3];
        for (int i = 0; i < arrayOfString1.length; i++)
        {
          String[] arrayOfString2 = arrayOfString1[i].split(":");
          if (arrayOfString2.length != 3)
            continue;
          CD[paramInt][i][0] = Integer.parseInt(arrayOfString2[0]);
          CD[paramInt][i][1] = Integer.parseInt(arrayOfString2[1]);
          CD[paramInt][i][2] = Double.parseDouble(arrayOfString2[2]);
        }
        return;
      }
    }
    catch (Exception localException)
    {
      ecoCreature.logger.log(Level.INFO, "[ecoCreature] Failed to load drops for: " + Creatures[paramInt]);
      CD[paramInt] = ((double[][])null);
    }
  }
}