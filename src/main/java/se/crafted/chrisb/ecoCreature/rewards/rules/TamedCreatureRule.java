package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class TamedCreatureRule extends AbstractRule
{
    private boolean wolverineMode;

    public TamedCreatureRule()
    {
        wolverineMode = true;
    }

    public void setWolverineMode(boolean wolverineMode)
    {
        this.wolverineMode = wolverineMode;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = !wolverineMode && event.isTamedCreatureKill();

        if (ruleBroken) {
            ECLogger.getInstance().debug(this.getClass(), "No reward for " + event.getKiller().getName() + " using tamed creatures.");
        }

        return ruleBroken;
    }

    public static Set<Rule> parseConfig(ConfigurationSection config)
    {
        Set<Rule> rules = Collections.emptySet();

        if (config != null) {
            TamedCreatureRule rule = new TamedCreatureRule();
            rule.setWolverineMode(config.getBoolean("System.Hunting.WolverineMode", true));
            rules = new HashSet<Rule>();
            rules.add(rule);
        }

        return rules;
    }
}
