package se.crafted.chrisb.ecoCreature.entities;

import com.nijiko.permissions.PermissionHandler;
import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.entity.Player;
import se.crafted.chrisb.ecoCreature.ecoCreature;
import se.crafted.chrisb.ecoCreature.utils.ecoConstants;
import se.crafted.chrisb.ecoCreature.utils.ecoEcon;

public class ecoRewardHandler
{
  public void RegisterAccident(Player paramPlayer)
  {
    double d1 = ecoEcon.getBalance(paramPlayer);
    double d2;
    if (ecoConstants.PT)
      d2 = d1 * ecoConstants.PA / 100.0D;
    else
      d2 = ecoConstants.PA;
    ecoEcon.regMoney(paramPlayer, -d2);
    paramPlayer.sendMessage(ecoConstants.MPD.replaceAll("<amt>", ecoEcon.format(d2).replaceAll("\\$", "\\\\\\$")));
  }

  public void CashRegistry(Player paramPlayer, int paramInt, String paramString)
  {
    double d = ecoEcon.rawAmount(ecoConstants.CCMIN[paramInt], ecoConstants.CCMAX[paramInt], ecoConstants.CCP[paramInt]);
    if (ecoConstants.IC)
      d = Math.round(d);
    if (ecoConstants.Gain.containsKey(ecoCreature.Permissions.getGroup(paramPlayer.getWorld().getName(), paramPlayer.getName())))
      d *= ((Double)ecoConstants.Gain.get(ecoCreature.Permissions.getGroup(paramPlayer.getWorld().getName(), paramPlayer.getName()))).doubleValue();
    if (d > 0.0D)
    {
      ecoEcon.regMoney(paramPlayer, d);
      if (ecoConstants.MO)
        paramPlayer.sendMessage(ecoConstants.CRM[paramInt].replaceAll("<amt>", ecoEcon.format(d).replaceAll("\\$", "\\\\\\$")).replaceAll("<itm>", toCamelCase(paramString)).replaceAll("<crt>", ecoConstants.Creatures[paramInt]));
    }
    else if (d == 0.0D)
    {
      if ((ecoConstants.MO) && (ecoConstants.MNR))
        paramPlayer.sendMessage(ecoConstants.CNR[paramInt].replaceAll("<crt>", ecoConstants.Creatures[paramInt]).replaceAll("<itm>", toCamelCase(paramString)));
    }
    else if (d < 0.0D)
    {
      ecoEcon.regMoney(paramPlayer, d);
      if (ecoConstants.MO)
        paramPlayer.sendMessage(ecoConstants.CPM[paramInt].replaceAll("<amt>", ecoEcon.format(d).replaceAll("\\$", "\\\\\\$")).replaceAll("<itm>", toCamelCase(paramString)).replaceAll("<crt>", ecoConstants.Creatures[paramInt]));
    }
  }

  static String toCamelCase(String paramString)
  {
    String[] arrayOfString1 = paramString.split("_");
    String str1 = "";
    for (String str2 : arrayOfString1)
      str1 = str1 + " " + toProperCase(str2);
    if (str1.trim().equals("Air"))
      return "Fists";
    if (str1.trim().equals("Bow"))
      return "Bow & Arrow";
    return str1.trim();
  }

  static String toProperCase(String paramString)
  {
    return paramString.substring(0, 1).toUpperCase() + paramString.substring(1).toLowerCase();
  }
}