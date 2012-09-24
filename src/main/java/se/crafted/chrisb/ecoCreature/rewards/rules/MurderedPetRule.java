package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class MurderedPetRule extends AbstractRule
{
    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = EntityUtils.isOwner(event.getKiller(), event.getEntity());

        if (ruleBroken) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " murdering pets.");
        }

        return ruleBroken;
    }

    public static Set<Rule> parseConfig(ConfigurationSection config)
    {
        Set<Rule> rules = Collections.emptySet();

        if (config != null) {
            rules = new HashSet<Rule>();
            rules.add(new MurderedPetRule());
        }

        return rules;
    }
}
