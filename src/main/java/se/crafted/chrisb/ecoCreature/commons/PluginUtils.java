/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
 * ecoCreature is licensed under the GNU Lesser General Public License.
 *
 * ecoCreature is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ecoCreature is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.crafted.chrisb.ecoCreature.commons;

import mc.alk.arena.BattleArena;
import net.jzx7.regiosapi.RegiosAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.bekvon.bukkit.residence.Residence;
import com.garbagemule.MobArena.MobArenaHandler;
import com.herocraftonline.heroes.Heroes;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public final class PluginUtils
{
    private static Plugin vaultPlugin;
    private static Plugin deathTpPlusPlugin;
    private static MobArenaHandler mobArenaHandler;
    private static Heroes heroesPlugin;
    private static WorldGuardPlugin worldGuardPlugin;
    private static Plugin mcMMOPlugin;
    private static Residence residencePlugin;
    private static RegiosAPI regiosAPI;
    private static Towny townyPlugin;
    private static Plugin factionsPlugin;
    private static BattleArena battleArenaPlugin;
    private static SimpleClans simpleClansPlugin;

    private static Permission permission;
    private static Economy economy;

    private PluginUtils()
    {
    }

    public static void init()
    {
        initPlugins();
        initVault();
    }

    private static void initPlugins()
    {
        vaultPlugin = getPlugin("Vault", "net.milkbowl.vault.Vault");
        deathTpPlusPlugin = getPlugin("DeathTpPlus", "org.simiancage.DeathTpPlus.DeathTpPlus");
        heroesPlugin = getPlugin("Heroes", "com.herocraftonline.heroes.Heroes");
        worldGuardPlugin = getPlugin("WorldGuard", "com.sk89q.worldguard.bukkit.WorldGuardPlugin");
        residencePlugin = getPlugin("Residence", "com.bekvon.bukkit.residence.Residence");
        townyPlugin = getPlugin("Towny", "com.palmergames.bukkit.towny.Towny");
        factionsPlugin = getPlugin("Factions", "com.massivecraft.factions.Factions");
        mcMMOPlugin = getPlugin("mcMMO", "com.gmail.nossr50.mcMMO");
        battleArenaPlugin = getPlugin("BattleArena", "mc.alk.arena.BattleArena");
        simpleClansPlugin = getPlugin("SimpleClans", "net.sacredlabyrinth.phaed.simpleclans.SimpleClans");

        Plugin regiosPlugin = getPlugin("Regios", "net.jzx7.regios.RegiosPlugin");
        if (regiosPlugin instanceof RegiosAPI) {
            regiosAPI = (RegiosAPI) regiosPlugin;
        }

        Plugin mobArenaPlugin = getPlugin("MobArena", "com.garbagemule.MobArena.MobArena");
        if (mobArenaPlugin != null) {
            mobArenaHandler = new MobArenaHandler();
        }
    }

    private static void initVault()
    {
        if (hasVault()) {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
                LoggerUtil.getInstance().info("Found permission provider " + permission.getName());
            }

            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
                LoggerUtil.getInstance().info("Found economy provider " + economy.getName());
            }
        }

        if (!hasPermission()) {
            LoggerUtil.getInstance().warning("Did not find permission provider");
        }

        if (!hasEconomy()) {
            LoggerUtil.getInstance().warning("Did not find economy provider");
        }
    }

    public static boolean hasPermission(Player player, String perm)
    {
        String mixedCasePerm = "ecoCreature." + perm;

        boolean isAllowed = hasPermission() ? permission.has(player, mixedCasePerm)
                || permission.has(player, mixedCasePerm.toLowerCase()) : player.hasPermission(mixedCasePerm)
                || player.hasPermission(mixedCasePerm.toLowerCase());

        LoggerUtil.getInstance().debugTrue(player.getName() + " denied permission for " + mixedCasePerm.toLowerCase(), !isAllowed);

        return isAllowed;
    }

    public static boolean hasPermission()
    {
        return permission != null;
    }

    public static Permission getPermission()
    {
        return permission;
    }

    public static boolean hasEconomy()
    {
        return economy != null;
    }

    public static Economy getEconomy()
    {
        return economy;
    }

    public static boolean hasVault()
    {
        return vaultPlugin != null && vaultPlugin.isEnabled();
    }

    public static boolean hasMobArena()
    {
        return mobArenaHandler != null;
    }

    public static boolean hasBattleArena()
    {
        return battleArenaPlugin != null && battleArenaPlugin.isEnabled();
    }

    public static boolean hasSimpleClans()
    {
        return simpleClansPlugin != null && simpleClansPlugin.isEnabled();
    }

    public static MobArenaHandler getMobArenaHandler()
    {
        return mobArenaHandler;
    }

    public static boolean hasHeroes()
    {
        return heroesPlugin != null && heroesPlugin.isEnabled();
    }

    public static Heroes getHeroes()
    {
        return heroesPlugin;
    }

    public static boolean hasResidence()
    {
        return residencePlugin != null && residencePlugin.isEnabled();
    }

    public static boolean hasRegios()
    {
        return regiosAPI != null;
    }

    public static RegiosAPI getRegiosAPI()
    {
        return regiosAPI;
    }

    public static boolean hasWorldGuard()
    {
        return worldGuardPlugin != null && worldGuardPlugin.isEnabled();
    }

    public static Object getRegionManager(World world)
    {
        return worldGuardPlugin.getRegionManager(world);
    }

    public static boolean hasMcMMO()
    {
        return mcMMOPlugin != null && mcMMOPlugin.isEnabled();
    }

    public static boolean hasTowny()
    {
        return townyPlugin != null && townyPlugin.isEnabled();
    }

    public static boolean hasFactions()
    {
        return factionsPlugin != null && factionsPlugin.isEnabled();
    }

    public static boolean hasDeathTpPlus()
    {
        return deathTpPlusPlugin != null && deathTpPlusPlugin.isEnabled();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getPlugin(String pluginName, String className)
    {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        try {
            Class<?> testClass = Class.forName(className);
            if (testClass.isInstance(plugin)) {
                LoggerUtil.getInstance().info("Found plugin: " + plugin.getDescription().getName());
                return (T) plugin;
            }
        }
        catch (ClassNotFoundException e) {
            LoggerUtil.getInstance().debug("Did not find plugin: " + pluginName);
        }
        return null;
    }
}
