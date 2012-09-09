package se.crafted.chrisb.ecoCreature.rewards.rules;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class MurderedPetRule extends DefaultRule
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

    public static Rule parseConfig(ConfigurationSection config)
    {
        return new MurderedPetRule();
    }
}
