package se.crafted.chrisb.ecoCreature.utils;

import java.text.DecimalFormat;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.iConomy.iConomy;
import com.iConomy.util.Constants;
import com.spikensbror.bukkit.mineconomy.MineConomy;

import cosine.boseconomy.BOSEconomy;

public class ecoEcon
{
    protected static iConomy iConomy = null;
    protected static BOSEconomy BOSEconomy = null;
    protected static Essentials Essentials = null;
    protected static MineConomy MineConomy = null;

    public static boolean initEcon(Server server)
    {
        Plugin economyPlugin;

        if (ecoConstants.economyCore.equalsIgnoreCase("iconomy")) {
            economyPlugin = server.getPluginManager().getPlugin("iConomy");
            if (economyPlugin != null)
                iConomy = (iConomy) economyPlugin;
        }
        else if (ecoConstants.economyCore.equalsIgnoreCase("boseconomy")) {
            economyPlugin = server.getPluginManager().getPlugin("BOSEconomy");
            if (economyPlugin != null)
                BOSEconomy = (BOSEconomy) economyPlugin;
        }
        else if (ecoConstants.economyCore.equalsIgnoreCase("essentials")) {
            economyPlugin = server.getPluginManager().getPlugin("Essentials");
            if (economyPlugin != null)
                Essentials = (Essentials) economyPlugin;
        }
        else if (ecoConstants.economyCore.equalsIgnoreCase("mineconomy")) {
            economyPlugin = server.getPluginManager().getPlugin("MineConomy");
            if (economyPlugin != null)
                MineConomy = (MineConomy) economyPlugin;
        }
        else {
            return false;
        }
        return true;
    }

    public static void addMoney(Player player, double amount)
    {
        if (iConomy != null) {
            iConomy.getAccount(player.getName()).getHoldings().add(amount);
        }
        else if (BOSEconomy != null) {
            BOSEconomy.addPlayerMoney(player.getName(), (int) amount, true);
        }
        else if (Essentials != null) {
            User user = Essentials.getUser(player);
            user.setMoney(user.getMoney() + amount);
        }
        else if (MineConomy != null) {
            MineConomy.getBank().add(player.getName(), amount);
        }
    }

    public static double getBalance(Player player)
    {
        if (iConomy != null)
            return iConomy.getAccount(player.getName()).getHoldings().balance();
        if (BOSEconomy != null)
            return BOSEconomy.getPlayerMoney(player.getName());
        if (Essentials != null) {
            User user = Essentials.getUser(player);
            return user.getMoney();
        }
        if (MineConomy != null)
            return MineConomy.getBank().getTotal(player.getName());
        return 0.0D;
    }

    public static double computeAmount(double amountMin, double amountMax, double percentage)
    {
        double amount = 0.0D;
        if ((amountMin == 0.0D) && (amountMax == 0.0D))
            amount = 0.0D;
        else if (amountMax == 0.0D)
            amount = amountMin;
        else
            amount = amountMin + Math.random() * (amountMax - amountMin);
        if (percentage == 0.0D)
            return 0.0D;
        if (percentage == 100.0D)
            return amount;
        if (Math.random() < percentage / 100.0D)
            return amount;
        return 0.0D;
    }

    public static String format(double amount)
    {
        DecimalFormat currencyFormat;

        if (ecoConstants.isIntegerCurrency) {
            currencyFormat = new DecimalFormat("0");
            if (iConomy != null)
                return String.valueOf(currencyFormat.format(amount)) + " " + Constants.Major.get(0);
            if (BOSEconomy != null) {
                amount = Math.round(amount);
                return String.valueOf(amount) + " " + BOSEconomy.getMoneyName();
            }
            if (Essentials != null)
                return String.valueOf(currencyFormat.format(amount));
            if (MineConomy != null)
                return String.valueOf(currencyFormat.format(amount));
        }
        else {
            currencyFormat = new DecimalFormat("#0.00");
            if (iConomy != null)
                return String.valueOf(currencyFormat.format(amount)) + " " + Constants.Major.get(0);
            if (BOSEconomy != null) {
                amount = Math.round(amount);
                return String.valueOf(amount) + " " + BOSEconomy.getMoneyName();
            }
            if (Essentials != null)
                return String.valueOf(currencyFormat.format(amount));
            if (MineConomy != null)
                return String.valueOf(currencyFormat.format(amount));
        }
        return String.format("%.2f", new Object[] { Double.valueOf(amount) });
    }
}