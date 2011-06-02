package se.crafted.chrisb.ecoCreature.utils;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.iConomy.iConomy;
import com.iConomy.util.Constants;
import com.spikensbror.bukkit.mineconomy.MineConomy;
import cosine.boseconomy.BOSEconomy;
import java.text.DecimalFormat;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ecoEcon
{
  protected static iConomy iConomy = null;
  protected static BOSEconomy BOSEconomy = null;
  protected static Essentials Essentials = null;
  protected static MineConomy MineConomy = null;

  public static boolean initEcon(Server paramServer)
  {
    Plugin localPlugin;
    if (ecoConstants.WEP.equalsIgnoreCase("iconomy"))
    {
      localPlugin = paramServer.getPluginManager().getPlugin("iConomy");
      if (localPlugin != null)
        iConomy = (iConomy)localPlugin;
    }
    else if (ecoConstants.WEP.equalsIgnoreCase("boseconomy"))
    {
      localPlugin = paramServer.getPluginManager().getPlugin("BOSEconomy");
      if (localPlugin != null)
        BOSEconomy = (BOSEconomy)localPlugin;
    }
    else if (ecoConstants.WEP.equalsIgnoreCase("essentials"))
    {
      localPlugin = paramServer.getPluginManager().getPlugin("Essentials");
      if (localPlugin != null)
        Essentials = (Essentials)localPlugin;
    }
    else if (ecoConstants.WEP.equalsIgnoreCase("mineconomy"))
    {
      localPlugin = paramServer.getPluginManager().getPlugin("MineConomy");
      if (localPlugin != null)
        MineConomy = (MineConomy)localPlugin;
    }
    else
    {
      return false;
    }
    return true;
  }

  public static void regMoney(Player paramPlayer, double paramDouble)
  {
    if (iConomy != null)
    {
      iConomy.getAccount(paramPlayer.getName()).getHoldings().add(paramDouble);
    }
    else if (BOSEconomy != null)
    {
      BOSEconomy.addPlayerMoney(paramPlayer.getName(), (int)paramDouble, true);
    }
    else if (Essentials != null)
    {
      User localUser = Essentials.getUser(paramPlayer);
      localUser.setMoney(localUser.getMoney() + paramDouble);
    }
    else if (MineConomy != null)
    {
      MineConomy.getBank().add(paramPlayer.getName(), paramDouble);
    }
  }

  public static double getBalance(Player paramPlayer)
  {
    if (iConomy != null)
      return iConomy.getAccount(paramPlayer.getName()).getHoldings().balance();
    if (BOSEconomy != null)
      return BOSEconomy.getPlayerMoney(paramPlayer.getName());
    if (Essentials != null)
    {
      User localUser = Essentials.getUser(paramPlayer);
      return localUser.getMoney();
    }
    if (MineConomy != null)
      return MineConomy.getBank().getTotal(paramPlayer.getName());
    return 0.0D;
  }

  public static double rawAmount(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    double d = 0.0D;
    if ((paramDouble1 == 0.0D) && (paramDouble2 == 0.0D))
      d = 0.0D;
    else if (paramDouble2 == 0.0D)
      d = paramDouble1;
    else
      d = paramDouble1 + Math.random() * (paramDouble2 - paramDouble1);
    if (paramDouble3 == 0.0D)
      return 0.0D;
    if (paramDouble3 == 100.0D)
      return d;
    if (Math.random() < paramDouble3 / 100.0D)
      return d;
    return 0.0D;
  }

  public static String format(double paramDouble)
  {
    DecimalFormat localDecimalFormat;
    if (ecoConstants.IC)
    {
      localDecimalFormat = new DecimalFormat("0");
      if (iConomy != null)
        return String.valueOf(localDecimalFormat.format(paramDouble)) + " " + Constants.Major.get(0);
      if (BOSEconomy != null)
      {
        paramDouble = Math.round(paramDouble);
        return String.valueOf(paramDouble) + " " + BOSEconomy.getMoneyName();
      }
      if (Essentials != null)
        return String.valueOf(localDecimalFormat.format(paramDouble));
      if (MineConomy != null)
        return String.valueOf(localDecimalFormat.format(paramDouble));
    }
    else
    {
      localDecimalFormat = new DecimalFormat("#0.00");
      if (iConomy != null)
        return String.valueOf(localDecimalFormat.format(paramDouble)) + " " + Constants.Major.get(0);
      if (BOSEconomy != null)
      {
        paramDouble = Math.round(paramDouble);
        return String.valueOf(paramDouble) + " " + BOSEconomy.getMoneyName();
      }
      if (Essentials != null)
        return String.valueOf(localDecimalFormat.format(paramDouble));
      if (MineConomy != null)
        return String.valueOf(localDecimalFormat.format(paramDouble));
    }
    return String.format("%.2f", new Object[] { Double.valueOf(paramDouble) });
  }
}