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

import se.crafted.chrisb.ecoCreature.commons.CustomType;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCoinMessageDecorator;
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
import se.crafted.chrisb.ecoCreature.rewards.sources.AbstractRewardSource;
import se.crafted.chrisb.ecoCreature.rewards.sources.RewardSourceFactory;

public class PluginConfig
{
    public static final String DEFAULT_WORLD = "__DEFAULT_WORLD__";

    private static final String OLD_DEFAULT_FILE = "ecoCreature.yml";
    private static final String DEFAULT_FILE = "default.yml";

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
            ECLogger.getInstance().setDebug(fileConfig.getBoolean("System.Debug", false));

            WorldSettings defaultSettings = loadWorldSettings(fileConfig);
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

            return true;
        }
        catch (IOException ioe) {
            ECLogger.getInstance().severe("Failed to read config: " + ioe.toString());
        }
        catch (InvalidConfigurationException ice) {
            ECLogger.getInstance().severe("Failed to parse config: " + ice.toString());
        }

        return false;
    }

    private static WorldSettings loadWorldSettings(FileConfiguration config)
    {
        WorldSettings settings = new WorldSettings();

        settings.setClearOnNoDrops(config.getBoolean("System.Hunting.ClearDefaultDrops", true));
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

    private static Map<Material, List<AbstractRewardSource>> loadMaterialSources(FileConfiguration config)
    {
        Map<Material, List<AbstractRewardSource>> sources = new HashMap<Material, List<AbstractRewardSource>>();
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
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(materialName, tableConfig.getConfigurationSection(materialName)), config);

                    if (!sources.containsKey(material)) {
                        sources.put(material, new ArrayList<AbstractRewardSource>());
                    }

                    List<String> setList = tableConfig.getConfigurationSection(materialName).getStringList("Sets");
                    if (!setList.isEmpty()) {
                        for (String setName : setList) {
                            if (setConfig != null && setConfig.getConfigurationSection(setName) != null) {
                                AbstractRewardSource setSource = RewardSourceFactory.createSource(CustomType.SET.getName(), setConfig.getConfigurationSection(setName));
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

    private static Map<EntityType, List<AbstractRewardSource>> loadEntitySources(FileConfiguration config)
    {
        Map<EntityType, List<AbstractRewardSource>> sources = new HashMap<EntityType, List<AbstractRewardSource>>();
        ConfigurationSection tableConfig = config.getConfigurationSection("RewardTable");
        ConfigurationSection setConfig = config.getConfigurationSection("RewardSets");

        if (tableConfig != null) {
            for (String entityName : tableConfig.getKeys(false)) {
                EntityType entityType = EntityType.fromName(entityName);
                if (entityType != null) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(entityName, tableConfig.getConfigurationSection(entityName)), config);

                    if (!sources.containsKey(entityType)) {
                        sources.put(entityType, new ArrayList<AbstractRewardSource>());
                    }

                    List<String> setList = tableConfig.getConfigurationSection(entityName).getStringList("Sets");
                    if (!setList.isEmpty()) {
                        for (String setName : setList) {
                            if (setConfig != null && setConfig.getConfigurationSection(setName) != null) {
                                AbstractRewardSource setSource = RewardSourceFactory.createSource(CustomType.SET.getName(), setConfig.getConfigurationSection(setName));
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

    private static Map<CustomType, List<AbstractRewardSource>> loadCustomSources(FileConfiguration config)
    {
        Map<CustomType, List<AbstractRewardSource>> sources = new HashMap<CustomType, List<AbstractRewardSource>>();
        ConfigurationSection tableConfig = config.getConfigurationSection("RewardTable");
        ConfigurationSection setConfig = config.getConfigurationSection("RewardSets");

        if (tableConfig != null) {
            for (String customName : tableConfig.getKeys(false)) {
                CustomType customType = CustomType.fromName(customName);
                if (customType != CustomType.INVALID) {
                    AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(customName, tableConfig.getConfigurationSection(customName)), config);

                    if (!sources.containsKey(customType)) {
                        sources.put(customType, new ArrayList<AbstractRewardSource>());
                    }

                    List<String> setList = tableConfig.getConfigurationSection(customName).getStringList("Sets");
                    if (!setList.isEmpty()) {
                        for (String setName : setList) {
                            if (setConfig != null && setConfig.getConfigurationSection(setName) != null) {
                                AbstractRewardSource setSource = RewardSourceFactory.createSource(CustomType.SET.getName(), setConfig.getConfigurationSection(setName));
                                sources.get(customType).add(mergeRewardSource(source, setSource));
                            }
                        }
                    }
                    else {
                        sources.get(customType).add(source);
                    }
                }
            }

            if (config.getBoolean("System.Hunting.PenalizeDeath", false)) {
                AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(CustomType.DEATH_PENALTY.getName(), config), config);
                if (!sources.containsKey(CustomType.DEATH_PENALTY)) {
                    sources.put(CustomType.DEATH_PENALTY, new ArrayList<AbstractRewardSource>());
                }

                sources.get(CustomType.DEATH_PENALTY).add(source);
            }

            if (config.getBoolean("System.Hunting.PVPReward", false)) {
                AbstractRewardSource source = configureRewardSource(RewardSourceFactory.createSource(CustomType.LEGACY_PVP.getName(), config), config);
                if (!sources.containsKey(CustomType.LEGACY_PVP)) {
                    sources.put(CustomType.LEGACY_PVP, new ArrayList<AbstractRewardSource>());
                }

                sources.get(CustomType.LEGACY_PVP).add(source);
            }
        }

        return sources;
    }

    private static AbstractRewardSource configureRewardSource(AbstractRewardSource source, ConfigurationSection config)
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

            if (message instanceof NoCoinMessageDecorator) {
                ((NoCoinMessageDecorator) message).setNoRewardMessageEnabled(config.getBoolean("System.Messages.NoReward", false));
            }
        }

        return message;
    }

    private static AbstractRewardSource mergeRewardSource(AbstractRewardSource from, AbstractRewardSource to)
    {
        to.setItemDrops(from.hasItemDrops() ? from.getItemDrops() : to.getItemDrops());
        to.setEntityDrops(from.hasEntityDrops() ? from.getEntityDrops() : to.getEntityDrops());
        to.setCoin(from.hasCoin() ? from.getCoin() : to.getCoin());

        to.setNoCoinRewardMessage(from.getNoCoinRewardMessage() != null ? from.getNoCoinRewardMessage() : to.getNoCoinRewardMessage());
        to.setCoinRewardMessage(from.getCoinRewardMessage() != null ? from.getCoinRewardMessage() : to.getCoinRewardMessage());
        to.setCoinPenaltyMessage(from.getCoinPenaltyMessage() != null ? from.getCoinPenaltyMessage() : to.getCoinPenaltyMessage());

        return to;
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
            ECLogger.getInstance().info("Converting old config file.");
            fileConfig = getConfig(oldDefaultFile);
            fileConfig.save(defaultFile);
            if (oldDefaultFile.delete()) {
                ECLogger.getInstance().info("Old config file converted.");
            }
        }
        else {
            fileConfig = getConfig(defaultFile);
            fileConfig.save(defaultFile);
        }

        ECLogger.getInstance().info("Loaded config defaults.");
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

            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            ECLogger.getInstance().info("Created config file: " + file.getName());
        }
        else {
            ECLogger.getInstance().info("Found config file: " + file.getName());
        }

        config.load(file);
        config.setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource(DEFAULT_FILE)));
        config.options().copyDefaults(true);

        return config;
    }
}
