package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;

public class SpawnerDistanceRule extends AbstractRule
{
    private static final String NO_CAMP_MESSAGE = "&7You find no rewards camping monster spawners.";

    private boolean canCampSpawner;
    private boolean campByDistance;
    private int campRadius;

    public SpawnerDistanceRule()
    {
        canCampSpawner = false;
        campByDistance = true;
        campRadius = 16;
    }

    public void setCanCampSpawner(boolean canCampSpawner)
    {
        this.canCampSpawner = canCampSpawner;
    }

    public void setCampByDistance(boolean campByDistance)
    {
        this.campByDistance = campByDistance;
    }

    public void setCampRadius(int campRadius)
    {
        this.campRadius = campRadius;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = false;

        if (!canCampSpawner && campByDistance) {
            if (EntityUtils.isNearSpawner(event.getKiller(), campRadius) || EntityUtils.isNearSpawner(event.getEntity(), campRadius)) {
                ruleBroken = true;
            }
        }

        if (ruleBroken) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " spawner camping.");
        }

        return ruleBroken;
    }

    public static Set<Rule> parseConfig(ConfigurationSection config)
    {
        Set<Rule> rules = Collections.emptySet();

        if (config != null) {
            SpawnerDistanceRule rule = new SpawnerDistanceRule();
            rule.setCanCampSpawner(config.getBoolean("System.Hunting.AllowCamping", false));
            rule.setClearDrops(config.getBoolean("System.Hunting.ClearCampDrops", true));
            rule.setCampByDistance(config.getBoolean("System.Hunting.CampingByDistance", true));
            rule.setCampRadius(config.getInt("System.Hunting.CampRadius", 16));
            rule.setMessage(new DefaultMessage(config.getString("System.Messages.NoCampMessage", NO_CAMP_MESSAGE)));
            rules = new HashSet<Rule>();
            rules.add(rule);
        }

        return rules;
    }
}
