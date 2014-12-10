/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2014, R. Ramos <http://github.com/mung3r/>
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
package se.crafted.chrisb.ecoCreature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.drops.categories.AbstractDropCategory;
import se.crafted.chrisb.ecoCreature.drops.categories.CustomDropCategory;
import se.crafted.chrisb.ecoCreature.drops.categories.CustomEntityDropCategory;
import se.crafted.chrisb.ecoCreature.drops.categories.CustomMaterialDropCategory;
import se.crafted.chrisb.ecoCreature.drops.categories.EntityDropCategory;
import se.crafted.chrisb.ecoCreature.drops.categories.HeroesDropCategory;
import se.crafted.chrisb.ecoCreature.drops.categories.MaterialDropCategory;
import se.crafted.chrisb.ecoCreature.drops.categories.McMMODropCategory;
import se.crafted.chrisb.ecoCreature.drops.categories.StreakDropCategory;
import se.crafted.chrisb.ecoCreature.drops.gain.BiomeGain;
import se.crafted.chrisb.ecoCreature.drops.gain.CronGain;
import se.crafted.chrisb.ecoCreature.drops.gain.EnvironmentGain;
import se.crafted.chrisb.ecoCreature.drops.gain.FactionsGain;
import se.crafted.chrisb.ecoCreature.drops.gain.GroupGain;
import se.crafted.chrisb.ecoCreature.drops.gain.HeroesGain;
import se.crafted.chrisb.ecoCreature.drops.gain.McMMOGain;
import se.crafted.chrisb.ecoCreature.drops.gain.MobArenaGain;
import se.crafted.chrisb.ecoCreature.drops.gain.PlayerGain;
import se.crafted.chrisb.ecoCreature.drops.gain.RegionGain;
import se.crafted.chrisb.ecoCreature.drops.gain.RegiosGain;
import se.crafted.chrisb.ecoCreature.drops.gain.ResidenceGain;
import se.crafted.chrisb.ecoCreature.drops.gain.TimeGain;
import se.crafted.chrisb.ecoCreature.drops.gain.TownyGain;
import se.crafted.chrisb.ecoCreature.drops.gain.WeaponGain;
import se.crafted.chrisb.ecoCreature.drops.gain.WeatherGain;
import se.crafted.chrisb.ecoCreature.drops.parties.HeroesParty;
import se.crafted.chrisb.ecoCreature.drops.parties.McMMOParty;
import se.crafted.chrisb.ecoCreature.drops.parties.MobArenaParty;
import se.crafted.chrisb.ecoCreature.drops.parties.Party;
import se.crafted.chrisb.ecoCreature.drops.sources.DropConfig;

public class DropConfigLoader
{
    public static final String DEFAULT_DROP_CONFIG = "__DEFAULT_CONFIG__";

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final String LEGACY_CONFIG_FILE = "ecoCreature.yml";
    private static final String DEFAULT_CONFIG_FILE = "default.yml";
    private static final int BUFFER_SIZE = 8192;

    private final ecoCreature plugin;
    private final File worldsDataFolder;
    private boolean initialized;
    private boolean checkForUpdates;

    private Map<String, DropConfig> worldConfigMap;

    public DropConfigLoader(ecoCreature plugin)
    {
        this.plugin = plugin;
        worldsDataFolder = new File(plugin.getDataFolder(), "worlds");
        initialized = (worldsDataFolder.exists() || worldsDataFolder.mkdirs()) && initConfig();
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public boolean isCheckForUpdates()
    {
        return checkForUpdates;
    }

    public DropConfig getDropConfig(World world)
    {
        DropConfig dropConfig = worldConfigMap.get(world.getName());
        if (dropConfig == null) {
            dropConfig = worldConfigMap.get(DropConfigLoader.DEFAULT_DROP_CONFIG);
        }
        return dropConfig;
    }

    public void loadConfig(String file) throws IOException, InvalidConfigurationException
    {
        worldConfigMap.put(DEFAULT_CONFIG_FILE, loadDropConfig(new DropConfig(plugin), getConfig(new File(plugin.getDataFolder(), file))));
    }

    private boolean initConfig()
    {
        try {
            FileConfiguration defaultConfigFile = getDefaultConfig();
            LoggerUtil.getInstance().setDebug(defaultConfigFile.getBoolean("System.Debug", LoggerUtil.getInstance().isDebug()));
            checkForUpdates = defaultConfigFile.getBoolean("System.CheckForUpdates", true);

            DropConfig defaultDropConfig = loadDropConfig(new DropConfig(plugin), defaultConfigFile);
            worldConfigMap = new HashMap<>();
            worldConfigMap.put(DEFAULT_DROP_CONFIG, defaultDropConfig);

            for (World world : plugin.getServer().getWorlds()) {

                File worldConfigFile = new File(worldsDataFolder, world.getName() + ".yml");

                if (worldConfigFile.exists()) {
                    FileConfiguration configFile = getConfig(worldConfigFile);
                    LoggerUtil.getInstance().info("Loaded config for " + world.getName() + " world.");
                    worldConfigMap.put(world.getName(), loadDropConfig(new DropConfig(plugin), configFile));
                }
                else {
                    worldConfigMap.put(world.getName(), defaultDropConfig);
                }
            }

            return true;
        }
        catch (IOException ioe) {
            LoggerUtil.getInstance().severe("Failed to read config: " + ioe.toString());
        }
        catch (InvalidConfigurationException ice) {
            LoggerUtil.getInstance().severe("Failed to parse config: " + ice.toString());
        }

        return false;
    }

    private static DropConfig loadDropConfig(DropConfig dropConfig, FileConfiguration config)
    {
        dropConfig.setClearEnchantedDrops(config.getBoolean("System.Hunting.ClearEnchantedDrops", false));
        dropConfig.setClearOnNoDrops(config.getBoolean("System.Hunting.ClearDefaultDrops", true));
        dropConfig.setOverrideDrops(config.getBoolean("System.Hunting.OverrideDrops", true));
        dropConfig.setNoFarm(config.getBoolean("System.Hunting.NoFarm", false));
        dropConfig.setNoFarmFire(config.getBoolean("System.Hunting.NoFarmFire", false));

        dropConfig.setGainMultipliers(loadGainMultipliers(config));
        dropConfig.setParties(loadParties(config));
        dropConfig.setDropCategories(loadDropCategories(config));

        return dropConfig;
    }

    private static Collection<PlayerGain> loadGainMultipliers(ConfigurationSection config)
    {
        Collection<PlayerGain> gainMultipliers = new ArrayList<>();

        gainMultipliers.addAll(GroupGain.parseConfig(config.getConfigurationSection("Gain.Groups")));
        gainMultipliers.addAll(TimeGain.parseConfig(config.getConfigurationSection("Gain.Time")));
        gainMultipliers.addAll(EnvironmentGain.parseConfig(config.getConfigurationSection("Gain.Environment")));
        gainMultipliers.addAll(BiomeGain.parseConfig(config.getConfigurationSection("Gain.Biome")));
        gainMultipliers.addAll(WeatherGain.parseConfig(config.getConfigurationSection("Gain.Weather")));
        gainMultipliers.addAll(WeaponGain.parseConfig(config.getConfigurationSection("Gain.Weapon")));
        gainMultipliers.addAll(RegionGain.parseConfig(config.getConfigurationSection("Gain.WorldGuard")));
        gainMultipliers.addAll(RegiosGain.parseConfig(config.getConfigurationSection("Gain.Regios")));
        gainMultipliers.addAll(ResidenceGain.parseConfig(config.getConfigurationSection("Gain.Residence")));
        if (DependencyUtils.hasFactions()) {
            gainMultipliers.addAll(FactionsGain.parseConfig(config.getConfigurationSection("Gain.Factions")));
        }
        gainMultipliers.addAll(TownyGain.parseConfig(config.getConfigurationSection("Gain.Towny")));
        gainMultipliers.addAll(MobArenaGain.parseConfig(config.getConfigurationSection("Gain.MobArena")));
        gainMultipliers.addAll(HeroesGain.parseConfig(config.getConfigurationSection("Gain.Heroes")));
        gainMultipliers.addAll(McMMOGain.parseConfig(config.getConfigurationSection("Gain.mcMMO")));
        gainMultipliers.addAll(CronGain.parseConfig(config.getConfigurationSection("Gain.Cron")));

        return gainMultipliers;
    }

    private static Collection<Party> loadParties(ConfigurationSection config)
    {
        Collection<Party> parties = new ArrayList<>();

        parties.addAll(MobArenaParty.parseConfig(config.getConfigurationSection("Gain.MobArena")));
        parties.addAll(HeroesParty.parseConfig(config.getConfigurationSection("Gain.Heroes")));
        parties.addAll(McMMOParty.parseConfig(config.getConfigurationSection("Gain.mcMMO")));

        return parties;
    }

    private static List<AbstractDropCategory<?>> loadDropCategories(ConfigurationSection config)
    {
        List<AbstractDropCategory<?>> dropCategory = new ArrayList<>();

        dropCategory.add(CustomMaterialDropCategory.parseConfig(config));
        dropCategory.add(MaterialDropCategory.parseConfig(config));
        dropCategory.add(CustomEntityDropCategory.parseConfig(config));
        dropCategory.add(EntityDropCategory.parseConfig(config));
        dropCategory.add(CustomDropCategory.parseConfig(config));
        dropCategory.add(StreakDropCategory.parseConfig(config));
        dropCategory.add(HeroesDropCategory.parseConfig(config));
        dropCategory.add(McMMODropCategory.parseConfig(config));

        return dropCategory;
    }

    private FileConfiguration getDefaultConfig() throws IOException, InvalidConfigurationException
    {
        FileConfiguration fileConfig = new YamlConfiguration();
        File defaultConfigFile = new File(plugin.getDataFolder(), DEFAULT_CONFIG_FILE);

        File legacyConfigFile = new File(plugin.getDataFolder(), LEGACY_CONFIG_FILE);

        if (defaultConfigFile.exists()) {
            fileConfig.load(defaultConfigFile);
        }
        else if (legacyConfigFile.exists()) {
            LoggerUtil.getInstance().info("Converting old config file.");
            fileConfig = getConfig(legacyConfigFile);
            fileConfig.save(defaultConfigFile);
            if (legacyConfigFile.delete()) {
                LoggerUtil.getInstance().info("Old config file converted.");
            }
        }
        else {
            createConfig(defaultConfigFile);
            fileConfig = getConfig(defaultConfigFile);
        }

        LoggerUtil.getInstance().info("Loaded config defaults.");
        return fileConfig;
    }

    private FileConfiguration getConfig(File file) throws IOException, InvalidConfigurationException
    {
        FileConfiguration config = new YamlConfiguration();

        if (file.exists()) {
            config.load(file);
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(DEFAULT_CONFIG_FILE), CHARSET)));
            config.options().copyDefaults(true);
            LoggerUtil.getInstance().info("Found config file: " + file.getName());
        }
        else {
            LoggerUtil.getInstance().severe("Could not read config file: " + file.getName());
        }

        return config;
    }

    private void createConfig(File file) throws IOException
    {
        boolean success = file.getParentFile().mkdir() && file.createNewFile();
   
        try (InputStream inputStream = plugin.getResource(file.getName()); FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            LoggerUtil.getInstance().warning("Could not read config file.");
        }
   
        LoggerUtil.getInstance().info("Created config file: " + file.getName());
    }
}
