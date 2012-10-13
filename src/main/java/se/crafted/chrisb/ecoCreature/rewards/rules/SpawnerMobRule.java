package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;
import se.crafted.chrisb.ecoCreature.messages.NoCampMessageDecorator;
import se.crafted.chrisb.ecoCreature.settings.SpawnerMobTracking;

public class SpawnerMobRule extends AbstractRule
{
    private static final String NO_CAMP_MESSAGE = "&7You find no rewards camping monster spawners.";

    private boolean canCampSpawner;
    private boolean campByEntity;

    public SpawnerMobRule()
    {
        canCampSpawner = false;
        campByEntity = false;
    }

    public void setCanCampSpawner(boolean canCampSpawner)
    {
        this.canCampSpawner = canCampSpawner;
    }

    public void setCampByEntity(boolean campByEntity)
    {
        this.campByEntity = campByEntity;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = false;

        if (!canCampSpawner && campByEntity) {
            SpawnerMobTracking tracking = event.getSpawnerMobTracking();
            if (tracking != null && tracking.isSpawnerMob(event.getEntity())) {
                ruleBroken = true;
            }
        }

        if (ruleBroken) {
            ECLogger.getInstance().debug(this.getClass(), "No reward for " + event.getKiller().getName() + " spawner camping.");
        }

        return ruleBroken;
    }

    public static Set<Rule> parseConfig(ConfigurationSection config)
    {
        Set<Rule> rules = Collections.emptySet();

        if (config != null) {
            SpawnerMobRule rule = new SpawnerMobRule();
            rule.setCanCampSpawner(config.getBoolean("System.Hunting.AllowCamping", false));
            rule.setClearDrops(config.getBoolean("System.Hunting.ClearCampDrops", true));
            rule.setCampByEntity(config.getBoolean("System.Hunting.CampingByEntity", false));
            NoCampMessageDecorator message = new NoCampMessageDecorator(new DefaultMessage(config.getString("System.Messages.NoCampMessage", NO_CAMP_MESSAGE)));
            message.setSpawnerCampMessageEnabled(config.getBoolean("System.Messages.Spawner", false));
            rule.setMessage(message);
            rules = new HashSet<Rule>();
            rules.add(rule);
        }

        return rules;
    }
}
