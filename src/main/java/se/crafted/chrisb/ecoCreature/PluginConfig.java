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

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.messages.Message;
import se.crafted.chrisb.ecoCreature.messages.NoCoinRewardMessage;
import se.crafted.chrisb.ecoCreature.messages.SpawnerCampMessage;
import se.crafted.chrisb.ecoCreature.rewards.RewardSettings;
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
import se.crafted.chrisb.ecoCreature.rewards.sources.RewardSourceType;

public class PluginConfig
{
    public static final String DEFAULT_WORLD = "__DEFAULT_WORLD__";

    private static final String OLD_CONFIG_FILE = "ecoCreature.yml";
    private static final String DEFAULT_CONFIG_FILE = "default.yml";

    private final ecoCreature plugin;
    private final File dataWorldsFolder;

    private boolean loaded;
    private File defaultConfigFile;
    private FileConfiguration defaultConfig;
    private Map<String, FileConfiguration> worldConfigs;
    private Map<String, RewardSettings> rewardSettings;

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

    public RewardSettings getRewardSettings(World world)
    {
        RewardSettings settings = rewardSettings.get(world.getName());
        if (settings == null) {
            settings = rewardSettings.get(PluginConfig.DEFAULT_WORLD);
        }
        return settings;
    }

    private void load() throws IOException, InvalidConfigurationException
    {
        defaultConfig = new YamlConfiguration();
        defaultConfigFile = new File(plugin.getDataFolder(), DEFAULT_CONFIG_FILE);

        File oldConfigFile = new File(plugin.getDataFolder(), OLD_CONFIG_FILE);

        if (defaultConfigFile.exists()) {
            defaultConfig.load(defaultConfigFile);
        }
        else if (oldConfigFile.exists()) {
            ECLogger.getInstance().info("Converting old config file.");
            defaultConfig = getConfig(oldConfigFile);
            if (oldConfigFile.delete()) {
                ECLogger.getInstance().info("Old config file converted.");
            }
        }
        else {
            defaultConfig = getConfig(defaultConfigFile);
        }

        ECLogger.getInstance().info("Loaded config defaults.");
        ECLogger.getInstance().setDebug(defaultConfig.getBoolean("System.Debug", false));

        RewardSettings defaultFactory = loadRewardConfig(defaultConfig);
        rewardSettings = new HashMap<String, RewardSettings>();
        rewardSettings.put(DEFAULT_WORLD, defaultFactory);

        worldConfigs = new HashMap<String, FileConfiguration>();

        for (World world : plugin.getServer().getWorlds()) {

            File worldConfigFile = new File(dataWorldsFolder, world.getName() + ".yml");

            if (worldConfigFile.exists()) {
                FileConfiguration worldConfig = getConfig(worldConfigFile);
                ECLogger.getInstance().info("Loaded config for " + world.getName() + " world.");
                rewardSettings.put(world.getName(), loadRewardConfig(worldConfig));
                worldConfigs.put(world.getName(), worldConfig);
            }
            else {
                rewardSettings.put(world.getName(), defaultFactory);
            }
        }
    }

    public void save()
    {
        if (loaded) {
            try {
                defaultConfig.save(defaultConfigFile);

                for (String worldName : worldConfigs.keySet()) {
                    FileConfiguration config = worldConfigs.get(worldName);
                    File configFile = new File(dataWorldsFolder, worldName + ".yml");
                    config.save(configFile);
                }
            }
            catch (IOException e) {
                ECLogger.getInstance().severe("Failed to write config: " + e.getMessage());
            }
        }
    }

    private static RewardSettings loadRewardConfig(FileConfiguration config)
    {
        RewardSettings settings = new RewardSettings();

        settings.setClearDefaultDrops(config.getBoolean("System.Hunting.ClearDefaultDrops", true));
        settings.setOverrideDrops(config.getBoolean("System.Hunting.OverrideDrops", true));
        settings.setNoFarm(config.getBoolean("System.Hunting.NoFarm", false));
        settings.setNoFarmFire(config.getBoolean("System.Hunting.NoFarmFire", false));

        settings.setGainMultipliers(loadGainConfig(config));
        settings.setParties(loadPartyConfig(config));
        settings.setHuntingRules(loadRulesConfig(config));

        Map<String, RewardSource> rewardSets = new HashMap<String, RewardSource>();
        ConfigurationSection rewardSetsConfig = config.getConfigurationSection("RewardSets");
        if (rewardSetsConfig != null) {
            for (String setName : rewardSetsConfig.getKeys(false)) {
                rewardSets.put(setName, configureSource(DefaultRewardSource.parseConfig(RewardSourceType.CUSTOM, rewardSetsConfig.getConfigurationSection(setName)), config));
            }
        }

        ConfigurationSection rewardTableConfig = config.getConfigurationSection("RewardTable");
        if (rewardTableConfig != null) {
            Map<RewardSourceType, List<RewardSource>> sources = new HashMap<RewardSourceType, List<RewardSource>>();
            for (String rewardName : rewardTableConfig.getKeys(false)) {
                RewardSource source = configureSource(DefaultRewardSource.parseConfig(RewardSourceType.fromName(rewardName), rewardTableConfig.getConfigurationSection(rewardName)), config);

                if (!sources.containsKey(source.getType())) {
                    sources.put(source.getType(), new ArrayList<RewardSource>());
                }

                if (rewardTableConfig.getConfigurationSection(rewardName).getList("Sets") != null) {
                    List<String> setList = rewardTableConfig.getConfigurationSection(rewardName).getStringList("Sets");
                    for (String setName : setList) {
                        if (rewardSets.containsKey(setName)) {
                            sources.get(source.getType()).add(mergeRewardSource(source, rewardSets.get(setName)));
                        }
                    }
                }
                else {
                    sources.get(source.getType()).add(source);
                }
            }

            if (config.getBoolean("System.Hunting.PenalizeDeath", false)) {
                RewardSource source = configureSource(DeathPenaltySource.parseConfig(RewardSourceType.DEATH_PENALTY, config), config);
                if (!sources.containsKey(source.getType())) {
                    sources.put(source.getType(), new ArrayList<RewardSource>());
                }

                sources.get(source.getType()).add(source);
            }

            if (config.getBoolean("System.Hunting.PVPReward", false)) {
                RewardSource source = configureSource(PVPRewardSource.parseConfig(RewardSourceType.LEGACY_PVP, config), config);
                if (!sources.containsKey(source.getType())) {
                    sources.put(source.getType(), new ArrayList<RewardSource>());
                }

                sources.get(source.getType()).add(source);
            }

            settings.setRewardSources(sources);
        }

        return settings;
    }

    private static RewardSource configureSource(RewardSource source, ConfigurationSection config)
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
        to.setDrops(from.hasDrops() ? from.getDrops() : to.getDrops());
        to.setCoin(from.hasCoin() ? from.getCoin() : to.getCoin());
        to.setExp(from.hasExp() ? from.getExp() : to.getExp());

        to.setNoCoinRewardMessage(from.getNoCoinRewardMessage() != null ? from.getNoCoinRewardMessage() : to.getNoCoinRewardMessage());
        to.setCoinRewardMessage(from.getCoinRewardMessage() != null ? from.getCoinRewardMessage() : to.getCoinRewardMessage());
        to.setCoinPenaltyMessage(from.getCoinPenaltyMessage() != null ? from.getCoinPenaltyMessage() : to.getCoinPenaltyMessage());

        return to;
    }

    private static Set<Gain> loadGainConfig(ConfigurationSection config)
    {
        Set<Gain> gainMultipliers = new HashSet<Gain>();

        gainMultipliers.add(GroupGain.parseConfig(config.getConfigurationSection("Gain.Groups")));
        gainMultipliers.add(TimeGain.parseConfig(config.getConfigurationSection("Gain.Time")));
        gainMultipliers.add(EnvironmentGain.parseConfig(config.getConfigurationSection("Gain.Environment")));
        gainMultipliers.add(RegionGain.parseConfig(config.getConfigurationSection("Gain.WorldGuard")));
        gainMultipliers.add(RegiosGain.parseConfig(config.getConfigurationSection("Gain.Regios")));
        gainMultipliers.add(ResidenceGain.parseConfig(config.getConfigurationSection("Gain.Residence")));
        gainMultipliers.add(FactionsGain.parseConfig(config.getConfigurationSection("Gain.Factions")));
        gainMultipliers.add(TownyGain.parseConfig(config.getConfigurationSection("Gain.Towny")));
        gainMultipliers.add(MobArenaGain.parseConfig(config.getConfigurationSection("Gain.MobArena.InArena")));
        gainMultipliers.add(HeroesGain.parseConfig(config.getConfigurationSection("Gain.Heroes.InParty")));
        gainMultipliers.add(McMMOGain.parseConfig(config.getConfigurationSection("Gain.mcMMO.InParty")));

        return gainMultipliers;
    }

    private static Set<Party> loadPartyConfig(ConfigurationSection config)
    {
        Set<Party> parties = new HashSet<Party>();

        parties.add(MobArenaParty.parseConfig(config.getConfigurationSection("Gain.MobArena.InArena")));
        parties.add(HeroesParty.parseConfig(config.getConfigurationSection("Gain.Heroes.InParty")));
        parties.add(McMMOParty.parseConfig(config.getConfigurationSection("Gain.mcMMO.InParty")));

        return parties;
    }

    private static Set<Rule> loadRulesConfig(ConfigurationSection config)
    {
        Set<Rule> rules = new HashSet<Rule>();

        rules.add(CreativeModeRule.parseConfig(config));
        rules.add(MobArenaRule.parseConfig(config));
        rules.add(MurderedPetRule.parseConfig(config));
        rules.add(ProjectileRule.parseConfig(config));
        rules.add(SpawnerDistanceRule.parseConfig(config));
        rules.add(SpawnerMobRule.parseConfig(config));
        rules.add(TamedCreatureRule.parseConfig(config));
        rules.add(UnderSeaLevelRule.parseConfig(config));

        return rules;
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
