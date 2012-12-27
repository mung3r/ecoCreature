/*
 * This file is part of ecoCreature.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import se.crafted.chrisb.ecoCreature.commons.LoggerUtil;
import se.crafted.chrisb.ecoCreature.rewards.gain.BiomeGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.EnvironmentGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.FactionsGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.PlayerGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.GroupGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.HeroesGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.McMMOGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.MobArenaGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.RegionGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.RegiosGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.ResidenceGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.TimeGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.TownyGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.WeaponGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.WeatherGain;
import se.crafted.chrisb.ecoCreature.rewards.parties.HeroesParty;
import se.crafted.chrisb.ecoCreature.rewards.parties.McMMOParty;
import se.crafted.chrisb.ecoCreature.rewards.parties.MobArenaParty;
import se.crafted.chrisb.ecoCreature.rewards.parties.Party;
import se.crafted.chrisb.ecoCreature.settings.AbstractRewardSettings;
import se.crafted.chrisb.ecoCreature.settings.CustomEntityRewardSettings;
import se.crafted.chrisb.ecoCreature.settings.CustomMaterialRewardSettings;
import se.crafted.chrisb.ecoCreature.settings.CustomRewardSettings;
import se.crafted.chrisb.ecoCreature.settings.StreakRewardSettings;
import se.crafted.chrisb.ecoCreature.settings.EntityRewardSettings;
import se.crafted.chrisb.ecoCreature.settings.HeroesRewardSettings;
import se.crafted.chrisb.ecoCreature.settings.MaterialRewardSettings;
import se.crafted.chrisb.ecoCreature.settings.McMMORewardSettings;
import se.crafted.chrisb.ecoCreature.settings.WorldSettings;

public class PluginConfig
{
    private static final String DEFAULT_WORLD = "__DEFAULT_WORLD__";

    private static final String OLD_DEFAULT_FILE = "ecoCreature.yml";
    private static final String DEFAULT_FILE = "default.yml";
    private static final int BUFFER_SIZE = 8192;

    private final ecoCreature plugin;
    private final File dataWorldsFolder;
    private boolean initialized;

    private Map<String, FileConfiguration> fileConfigMap;
    private Map<String, WorldSettings> worldSettingsMap;

    public PluginConfig(ecoCreature plugin)
    {
        this.plugin = plugin;
        dataWorldsFolder = new File(plugin.getDataFolder(), "worlds");
        dataWorldsFolder.mkdirs();
        initialized = initConfig();
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public WorldSettings getWorldSettings(World world)
    {
        WorldSettings settings = worldSettingsMap.get(world.getName());
        if (settings == null) {
            settings = worldSettingsMap.get(PluginConfig.DEFAULT_WORLD);
        }
        return settings;
    }

    private boolean initConfig()
    {
        FileConfiguration fileConfig = null;

        try {
            fileConfig = getDefaultConfig();
            LoggerUtil.getInstance().setDebug(fileConfig.getBoolean("System.Debug", LoggerUtil.getInstance().isDebug()));

            WorldSettings defaultSettings = loadWorldSettings(new WorldSettings(plugin), fileConfig);
            worldSettingsMap = new HashMap<String, WorldSettings>();
            worldSettingsMap.put(DEFAULT_WORLD, defaultSettings);

            fileConfigMap = new HashMap<String, FileConfiguration>();

            for (World world : plugin.getServer().getWorlds()) {

                File worldConfigFile = new File(dataWorldsFolder, world.getName() + ".yml");

                if (worldConfigFile.exists()) {
                    FileConfiguration configFile = getConfig(worldConfigFile);
                    LoggerUtil.getInstance().info("Loaded config for " + world.getName() + " world.");
                    worldSettingsMap.put(world.getName(), loadWorldSettings(new WorldSettings(plugin), configFile));
                    fileConfigMap.put(world.getName(), configFile);
                }
                else {
                    worldSettingsMap.put(world.getName(), defaultSettings);
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

    private static WorldSettings loadWorldSettings(WorldSettings settings, FileConfiguration config)
    {
        settings.setClearOnNoDrops(config.getBoolean("System.Hunting.ClearDefaultDrops", true));
        settings.setOverrideDrops(config.getBoolean("System.Hunting.OverrideDrops", true));
        settings.setNoFarm(config.getBoolean("System.Hunting.NoFarm", false));
        settings.setNoFarmFire(config.getBoolean("System.Hunting.NoFarmFire", false));

        settings.setGainMultipliers(loadGainMultipliers(config));
        settings.setParties(loadParties(config));
        settings.setRewardSettings(loadRewardSettings(config));

        return settings;
    }

    private static Set<PlayerGain> loadGainMultipliers(ConfigurationSection config)
    {
        Set<PlayerGain> gainMultipliers = new HashSet<PlayerGain>();

        gainMultipliers.addAll(GroupGain.parseConfig(config.getConfigurationSection("Gain.Groups")));
        gainMultipliers.addAll(TimeGain.parseConfig(config.getConfigurationSection("Gain.Time")));
        gainMultipliers.addAll(EnvironmentGain.parseConfig(config.getConfigurationSection("Gain.Environment")));
        gainMultipliers.addAll(BiomeGain.parseConfig(config.getConfigurationSection("Gain.Biome")));
        gainMultipliers.addAll(WeatherGain.parseConfig(config.getConfigurationSection("Gain.Weather")));
        gainMultipliers.addAll(WeaponGain.parseConfig(config.getConfigurationSection("Gain.Weapon")));
        gainMultipliers.addAll(RegionGain.parseConfig(config.getConfigurationSection("Gain.WorldGuard")));
        gainMultipliers.addAll(RegiosGain.parseConfig(config.getConfigurationSection("Gain.Regios")));
        gainMultipliers.addAll(ResidenceGain.parseConfig(config.getConfigurationSection("Gain.Residence")));
        gainMultipliers.addAll(FactionsGain.parseConfig(config.getConfigurationSection("Gain.Factions")));
        gainMultipliers.addAll(TownyGain.parseConfig(config.getConfigurationSection("Gain.Towny")));
        gainMultipliers.addAll(MobArenaGain.parseConfig(config.getConfigurationSection("Gain.MobArena")));
        gainMultipliers.addAll(HeroesGain.parseConfig(config.getConfigurationSection("Gain.Heroes")));
        gainMultipliers.addAll(McMMOGain.parseConfig(config.getConfigurationSection("Gain.mcMMO")));

        return gainMultipliers;
    }

    private static Set<Party> loadParties(ConfigurationSection config)
    {
        Set<Party> parties = new HashSet<Party>();

        parties.addAll(MobArenaParty.parseConfig(config.getConfigurationSection("Gain.MobArena")));
        parties.addAll(HeroesParty.parseConfig(config.getConfigurationSection("Gain.Heroes")));
        parties.addAll(McMMOParty.parseConfig(config.getConfigurationSection("Gain.mcMMO")));

        return parties;
    }

    private static List<AbstractRewardSettings<?>> loadRewardSettings(ConfigurationSection config)
    {
        List<AbstractRewardSettings<?>> rewardSettings = new ArrayList<AbstractRewardSettings<?>>();

        rewardSettings.add(CustomMaterialRewardSettings.parseConfig(config));
        rewardSettings.add(MaterialRewardSettings.parseConfig(config));
        rewardSettings.add(CustomEntityRewardSettings.parseConfig(config));
        rewardSettings.add(EntityRewardSettings.parseConfig(config));
        rewardSettings.add(CustomRewardSettings.parseConfig(config));
        rewardSettings.add(StreakRewardSettings.parseConfig(config));
        rewardSettings.add(HeroesRewardSettings.parseConfig(config));
        rewardSettings.add(McMMORewardSettings.parseConfig(config));

        return rewardSettings;
    }

    private FileConfiguration getDefaultConfig() throws IOException, InvalidConfigurationException
    {
        FileConfiguration fileConfig = new YamlConfiguration();
        File defaultFile = new File(plugin.getDataFolder(), DEFAULT_FILE);

        File oldDefaultFile = new File(plugin.getDataFolder(), OLD_DEFAULT_FILE);

        if (defaultFile.exists()) {
            fileConfig.load(defaultFile);
        }
        else if (oldDefaultFile.exists()) {
            LoggerUtil.getInstance().info("Converting old config file.");
            fileConfig = getConfig(oldDefaultFile);
            fileConfig.save(defaultFile);
            if (oldDefaultFile.delete()) {
                LoggerUtil.getInstance().info("Old config file converted.");
            }
        }
        else {
            fileConfig = getConfig(defaultFile);
            fileConfig.save(defaultFile);
        }

        LoggerUtil.getInstance().info("Loaded config defaults.");
        return fileConfig;
    }

    private FileConfiguration getConfig(File file) throws IOException, InvalidConfigurationException
    {
        FileConfiguration config = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdir();
            file.createNewFile();
            InputStream inputStream = plugin.getResource(file.getName());
            FileOutputStream outputStream = new FileOutputStream(file);

            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                int length = 0;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            catch (IOException e) {
                LoggerUtil.getInstance().warning("Could not read config file.");
            }
            finally {
                inputStream.close();
                outputStream.close();
            }

            LoggerUtil.getInstance().info("Created config file: " + file.getName());
        }
        else {
            LoggerUtil.getInstance().info("Found config file: " + file.getName());
        }

        config.load(file);
        config.setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource(DEFAULT_FILE)));
        config.options().copyDefaults(true);

        return config;
    }
}
