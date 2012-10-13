package se.crafted.chrisb.ecoCreature.commons;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

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
import com.sk89q.worldguard.protection.managers.RegionManager;

import couk.Adamki11s.Regios.API.RegiosAPI;

public final class DependencyUtils
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

    private static Permission permission;
    private static Economy economy;

    private DependencyUtils()
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
        heroesPlugin = (Heroes) getPlugin("Heroes", "com.herocraftonline.heroes.Heroes");
        worldGuardPlugin = (WorldGuardPlugin) getPlugin("WorldGuard", "com.sk89q.worldguard.bukkit.WorldGuardPlugin");
        residencePlugin = (Residence) getPlugin("Residence", "com.bekvon.bukkit.residence.Residence");
        townyPlugin = (Towny) getPlugin("Towny", "com.palmergames.bukkit.towny.Towny");
        factionsPlugin = getPlugin("Factions", "com.massivecraft.factions.P");
        mcMMOPlugin = getPlugin("mcMMO", "com.gmail.nossr50.mcMMO");

        Plugin regiosPlugin = getPlugin("Regios", "couk.Adamki11s.Regios.Main.Regios");
        if (regiosPlugin != null) {
            regiosAPI = new RegiosAPI();
        }

        Plugin mobArenaPlugin = getPlugin("MobArena", "com.garbagemule.MobArena.MobArena");
        if (mobArenaPlugin != null) {
            mobArenaHandler = new MobArenaHandler();
        }
    }

    private static void initVault()
    {
        if (hasVault()) {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
                ECLogger.getInstance().info("Found permission provider " + permission.getName());
            }

            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
                ECLogger.getInstance().info("Found economy provider " + economy.getName());
            }
        }

        if (!hasPermission()) {
            ECLogger.getInstance().warning("Did not find permission provider");
        }

        if (!hasEconomy()) {
            ECLogger.getInstance().warning("Did not find economy provider");
        }
    }

    public static boolean hasPermission(Player player, String perm)
    {
        if (hasPermission()) {
            return permission.has(player.getWorld(), player.getName(), "ecoCreature." + perm) || permission.has(player.getWorld(), player.getName(), "ecocreature." + perm.toLowerCase());
        }
        else {
            return player.hasPermission("ecoCreature." + perm) || player.hasPermission("ecocreature." + perm.toLowerCase());
        }
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
        return vaultPlugin != null;
    }

    public static boolean hasMobArena()
    {
        return mobArenaHandler != null;
    }

    public static MobArenaHandler getMobArenaHandler()
    {
        return mobArenaHandler;
    }

    public static boolean hasHeroes()
    {
        return heroesPlugin != null;
    }

    public static Heroes getHeroes()
    {
        return heroesPlugin;
    }

    public static boolean hasResidence()
    {
        return residencePlugin != null;
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
        return worldGuardPlugin != null;
    }

    public static RegionManager getRegionManager(World world)
    {
        return worldGuardPlugin.getRegionManager(world);
    }

    public static boolean hasMcMMO()
    {
        return mcMMOPlugin != null;
    }

    public static boolean hasTowny()
    {
        return townyPlugin != null;
    }

    public static boolean hasFactions()
    {
        return factionsPlugin != null;
    }

    public static boolean hasDeathTpPlus()
    {
        return deathTpPlusPlugin != null;
    }

    private static Plugin getPlugin(String pluginName, String className)
    {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        try {
            Class<?> testClass = Class.forName(className);
            if (testClass.isInstance(plugin) && plugin.isEnabled()) {
                ECLogger.getInstance().info("Found plugin: " + plugin.getDescription().getName());
                return plugin;
            }
        }
        catch (ClassNotFoundException e) {
            ECLogger.getInstance().info("Did not find plugin: " + pluginName);
        }
        return null;
    }
}
