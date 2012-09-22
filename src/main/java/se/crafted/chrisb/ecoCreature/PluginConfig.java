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

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCoinRewardMessage;
import se.crafted.chrisb.ecoCreature.messages.SpawnerCampMessage;
import se.crafted.chrisb.ecoCreature.rewards.WorldSettings;
import se.crafted.chrisb.ecoCreature.rewards.gain.BiomeGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.EnvironmentGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.FactionsGain;
import se.crafted.chrisb.ecoCreature.rewards.gain.Gain;
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
import se.crafted.chrisb.ecoCreature.rewards.rules.CreativeModeRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.MobArenaRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.MurderedPetRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.ProjectileRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.Rule;
import se.crafted.chrisb.ecoCreature.rewards.rules.SpawnerDistanceRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.SpawnerMobRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.TamedCreatureRule;
import se.crafted.chrisb.ecoCreature.rewards.rules.UnderSeaLevelRule;
import se.crafted.chrisb.ecoCreature.rewards.sources.DeathPenaltySource;
import se.crafted.chrisb.ecoCreature.rewards.sources.DefaultRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.PVPRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.RewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.CustomType;

public class PluginConfig
{
    public static final String DEFAULT_WORLD = "__DEFAULT_WORLD__";

    private static final String OLD_DEFAULT_FILE = "ecoCreature.yml";
    private static final String DEFAULT_FILE = "default.yml";

    private final ecoCreature plugin;
    private final File dataWorldsFolder;

    private boolean loaded;
    private File defaultFile;
    private FileConfiguration defaultConfig;
    private Map<String, FileConfiguration> fileConfigMap;
    private Map<String, WorldSettings> worldSettingsMap;

    public PluginConfig(ecoCreature plugin)
    {
        this.plugin = plugin;
        loaded = false;
        dataWorldsFolder = new File(plugin.getDataFolder(), "worlds");
        dataWorldsFolder.mkdirs();

        try {
            load();
            loaded = true;
        }
        catch (IOException ioe) {
            ECLogger.getInstance().severe("Failed to read config: " + ioe.toString());
        }
        catch (InvalidConfigurationException ice) {
            ECLogger.getInstance().severe("Failed to parse config: " + ice.toString());
        }
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public WorldSettings getWorldSettings(World world)
    {
        WorldSettings settings = worldSettingsMap.get(world.getName());
        if (settings == null) {
            settings = worldSettingsMap.get(PluginConfig.DEFAULT_WORLD);
        }
        return settings;
    }

    private void load() throws IOException, InvalidConfigurationException
    {
        defaultConfig = new YamlConfiguration();
        defaultFile = new File(plugin.getDataFolder(), DEFAULT_FILE);

        File oldDefaultFile = new File(plugin.getDataFolder(), OLD_DEFAULT_FILE);

        if (defaultFile.exists()) {
            defaultConfig.load(defaultFile);
        }
        else if (oldDefaultFile.exists()) {
            ECLogger.getInstance().info("Converting old config file.");
            defaultConfig = getConfig(oldDefaultFile);
            if (oldDefaultFile.delete()) {
                ECLogger.getInstance().info("Old config file converted.");
            }
        }
        else {
            defaultConfig = getConfig(defaultFile);
        }

        ECLogger.getInstance().info("Loaded config defaults.");
        ECLogger.getInstance().setDebug(defaultConfig.getBoolean("System.Debug", false));

        WorldSettings defaultSettings = loadWorldSettings(defaultConfig);
        worldSettingsMap = new HashMap<String, WorldSettings>();
        worldSettingsMap.put(DEFAULT_WORLD, defaultSettings);

        fileConfigMap = new HashMap<String, FileConfiguration>();

        for (World world : plugin.getServer().getWorlds()) {

            File worldConfigFile = new File(dataWorldsFolder, world.getName() + ".yml");

            if (worldConfigFile.exists()) {
                FileConfiguration configFile = getConfig(worldConfigFile);
                ECLogger.getInstance().info("Loaded config for " + world.getName() + " world.");
                worldSettingsMap.put(world.getName(), loadWorldSettings(configFile));
                fileConfigMap.put(world.getName(), configFile);
            }
            else {
                worldSettingsMap.put(world.getName(), defaultSettings);
            }
        }
    }

    public void save()
    {
        if (loaded) {
            try {
                defaultConfig.save(defaultFile);

                for (String worldName : fileConfigMap.keySet()) {
                    FileConfiguration config = fileConfigMap.get(worldName);
                    File configFile = new File(dataWorldsFolder, worldName + ".yml");
                    config.save(configFile);
                }
            }
            catch (IOException e) {
                ECLogger.getInstance().severe("Failed to write config: " + e.getMessage());
            }
        }
    }

    private static WorldSettings loadWorldSettings(FileConfiguration config)
    {
        WorldSettings settings = new WorldSettings();

        settings.setClearDefaultDrops(config.getBoolean("System.Hunting.ClearDefaultDrops", true));
        settings.setOverrideDrops(config.getBoolean("System.Hunting.OverrideDrops", true));
        settings.setNoFarm(config.getBoolean("System.Hunting.NoFarm", false));
        settings.setNoFarmFire(config.getBoolean("System.Hunting.NoFarmFire", false));

        settings.setGainMultipliers(loadGainMultipliers(config));
        settings.setParties(loadParties(config));
        settings.setHuntingRules(loadHuntingRules(config));
        settings.setMaterialSources(loadMaterialSources(config));
        settings.setEntitySources(loadEntitySources(config));
        settings.setCustomSources(loadCustomSources(config));

        return settings;
    }

    private static Set<Gain> loadGainMultipliers(ConfigurationSection config)
    {
        Set<Gain> gainMultipliers = new HashSet<Gain>();

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
        gainMultipliers.addAll(MobArenaGain.parseConfig(config.getConfigurationSection("Gain.MobArena.InArena")));
        gainMultipliers.addAll(HeroesGain.parseConfig(config.getConfigurationSection("Gain.Heroes.InParty")));
        gainMultipliers.addAll(McMMOGain.parseConfig(config.getConfigurationSection("Gain.mcMMO.InParty")));

        return gainMultipliers;
    }

    private static Set<Party> loadParties(ConfigurationSection config)
    {
        Set<Party> parties = new HashSet<Party>();

        parties.addAll(MobArenaParty.parseConfig(config.getConfigurationSection("Gain.MobArena.InArena")));
        parties.addAll(HeroesParty.parseConfig(config.getConfigurationSection("Gain.Heroes.InParty")));
        parties.addAll(McMMOParty.parseConfig(config.getConfigurationSection("Gain.mcMMO.InParty")));

        return parties;
    }

    private static Set<Rule> loadHuntingRules(ConfigurationSection config)
    {
        Set<Rule> rules = new HashSet<Rule>();

        rules.addAll(CreativeModeRule.parseConfig(config));
        rules.addAll(MobArenaRule.parseConfig(config));
        rules.addAll(MurderedPetRule.parseConfig(config));
        rules.addAll(ProjectileRule.parseConfig(config));
        rules.addAll(SpawnerDistanceRule.parseConfig(config));
        rules.addAll(SpawnerMobRule.parseConfig(config));
        rules.addAll(TamedCreatureRule.parseConfig(config));
        rules.addAll(UnderSeaLevelRule.parseConfig(config));

        return rules;
    }

    private static Map<Material, List<RewardSource>> loadMaterialSources(FileConfiguration config)
    {
        Map<Material, List<RewardSource>> sources = new HashMap<Material, List<RewardSource>>();
        ConfigurationSection tableConfig = config.getConfigurationSection("RewardTable");
        ConfigurationSection setConfig = config.getConfigurationSection("RewardSets");

        if (tableConfig != null) {
            for (String materialName : tableConfig.getKeys(false)) {
                Material material = Material.matchMaterial(materialName);

                // NOTE: backward compatibility
                if (material == null && CustomType.LEGACY_SPAWNER.equals(CustomType.fromName(materialName))) {
                    material = Material.MOB_SPAWNER;
                }

                if (material != null) {
                    RewardSource source = configureRewardSource(DefaultRewardSource.parseConfig(tableConfig.getConfigurationSection(materialName)), config);

                    if (!sources.containsKey(material)) {
                        sources.put(material, new ArrayList<RewardSource>());
                    }

                    List<String> setList = tableConfig.getConfigurationSection(materialName).getStringList("Sets");
                    if (!setList.isEmpty()) {
                        for (String setName : setList) {
                            if (setConfig != null && setConfig.getConfigurationSection(setName) != null) {
                                RewardSource setSource = DefaultRewardSource.parseConfig(setConfig.getConfigurationSection(setName));
                                sources.get(material).add(mergeRewardSource(source, setSource));
                            }
                        }
                    }
                    else {
                        sources.get(material).add(source);
                    }
                }
            }
        }

        return sources;
    }

    private static Map<EntityType, List<RewardSource>> loadEntitySources(FileConfiguration config)
    {
        Map<EntityType, List<RewardSource>> sources = new HashMap<EntityType, List<RewardSource>>();
        ConfigurationSection tableConfig = config.getConfigurationSection("RewardTable");
        ConfigurationSection setConfig = config.getConfigurationSection("RewardSets");

        if (tableConfig != null) {
            for (String entityName : tableConfig.getKeys(false)) {
                EntityType entityType = EntityType.fromName(entityName);
                if (entityType != null) {
                    RewardSource source = configureRewardSource(DefaultRewardSource.parseConfig(tableConfig.getConfigurationSection(entityName)), config);

                    if (!sources.containsKey(entityType)) {
                        sources.put(entityType, new ArrayList<RewardSource>());
                    }

                    List<String> setList = tableConfig.getConfigurationSection(entityName).getStringList("Sets");
                    if (!setList.isEmpty()) {
                        for (String setName : setList) {
                            if (setConfig != null && setConfig.getConfigurationSection(setName) != null) {
                                RewardSource setSource = DefaultRewardSource.parseConfig(setConfig.getConfigurationSection(setName));
                                sources.get(entityType).add(mergeRewardSource(source, setSource));
                            }
                        }
                    }
                    else {
                        sources.get(entityType).add(source);
                    }
                }
            }
        }

        return sources;
    }

    private static Map<CustomType, List<RewardSource>> loadCustomSources(FileConfiguration config)
    {
        Map<CustomType, List<RewardSource>> sources = new HashMap<CustomType, List<RewardSource>>();
        ConfigurationSection tableConfig = config.getConfigurationSection("RewardTable");
        ConfigurationSection setConfig = config.getConfigurationSection("RewardSets");

        if (tableConfig != null) {
            for (String customName : tableConfig.getKeys(false)) {
                CustomType customType = CustomType.fromName(customName);
                if (customType != null) {
                    RewardSource source = configureRewardSource(DefaultRewardSource.parseConfig(tableConfig.getConfigurationSection(customName)), config);

                    if (!sources.containsKey(customType)) {
                        sources.put(customType, new ArrayList<RewardSource>());
                    }

                    List<String> setList = tableConfig.getConfigurationSection(customName).getStringList("Sets");
                    if (!setList.isEmpty()) {
                        for (String setName : setList) {
                            if (setConfig != null && setConfig.getConfigurationSection(setName) != null) {
                                RewardSource setSource = DefaultRewardSource.parseConfig(setConfig.getConfigurationSection(setName));
                                sources.get(customType).add(mergeRewardSource(source, setSource));
                            }
                        }
                    }
                    else {
                        sources.get(customType).add(source);
                    }
                }

                if (config.getBoolean("System.Hunting.PenalizeDeath", false)) {
                    RewardSource source = configureRewardSource(DeathPenaltySource.parseConfig(config), config);
                    if (!sources.containsKey(CustomType.DEATH_PENALTY)) {
                        sources.put(CustomType.DEATH_PENALTY, new ArrayList<RewardSource>());
                    }

                    sources.get(CustomType.DEATH_PENALTY).add(source);
                }

                if (config.getBoolean("System.Hunting.PVPReward", false)) {
                    RewardSource source = configureRewardSource(PVPRewardSource.parseConfig(config), config);
                    if (!sources.containsKey(CustomType.LEGACY_PVP)) {
                        sources.put(CustomType.LEGACY_PVP, new ArrayList<RewardSource>());
                    }

                    sources.get(CustomType.LEGACY_PVP).add(source);
                }
            }
        }

        return sources;
    }

    private static RewardSource configureRewardSource(RewardSource source, ConfigurationSection config)
    {
        if (source != null && config != null) {
            source.setIntegerCurrency(config.getBoolean("System.Economy.IntegerCurrency", false));
            source.setFixedDrops(config.getBoolean("System.Hunting.FixedDrops", false));

            source.setCoinRewardMessage(configureMessage(source.getCoinRewardMessage(), config));
            source.setCoinPenaltyMessage(configureMessage(source.getCoinPenaltyMessage(), config));
            source.setNoCoinRewardMessage(configureMessage(source.getNoCoinRewardMessage(), config));
        }

        return source;
    }

    private static Message configureMessage(Message message, ConfigurationSection config)
    {
        if (message != null && config != null) {
            message.setMessageOutputEnabled(config.getBoolean("System.Messages.Output", true));
            message.setCoinLoggingEnabled(config.getBoolean("System.Messages.LogCoinRewards", true));

            if (message instanceof NoCoinRewardMessage) {
                ((NoCoinRewardMessage) message).setNoRewardMessageEnabled(config.getBoolean("System.Messages.NoReward", false));
            }

            if (message instanceof SpawnerCampMessage) {
                ((SpawnerCampMessage) message).setSpawnerCampMessageEnabled(config.getBoolean("System.Messages.Spawner", false));
            }
        }

        return message;
    }

    private static RewardSource mergeRewardSource(RewardSource from, RewardSource to)
    {
        to.setItemDrops(from.hasItemDrops() ? from.getItemDrops() : to.getItemDrops());
        to.setEntityDrops(from.hasEntityDrops() ? from.getEntityDrops() : to.getEntityDrops());
        to.setCoin(from.hasCoin() ? from.getCoin() : to.getCoin());

        to.setNoCoinRewardMessage(from.getNoCoinRewardMessage() != null ? from.getNoCoinRewardMessage() : to.getNoCoinRewardMessage());
        to.setCoinRewardMessage(from.getCoinRewardMessage() != null ? from.getCoinRewardMessage() : to.getCoinRewardMessage());
        to.setCoinPenaltyMessage(from.getCoinPenaltyMessage() != null ? from.getCoinPenaltyMessage() : to.getCoinPenaltyMessage());

        return to;
    }

    private FileConfiguration getConfig(File file) throws IOException, InvalidConfigurationException
    {
        FileConfiguration config = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdir();
            file.createNewFile();
            InputStream inputStream = plugin.getResource(file.getName());
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            ECLogger.getInstance().info("Default config written to " + file.getName());
        }
        else {
            ECLogger.getInstance().severe("Default config could not be created!");
        }

        config.load(file);
        config.setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource(file.getName())));
        config.options().copyDefaults(true);

        return config;
    }
}
