package se.crafted.chrisb.ecoCreature.rewards.rules;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.commons.EntityUtils;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;

public class UnderSeaLevelRule extends DefaultRule
{
    private static final String NO_UNDER_SEA_LEVEL_MESSAGE = "&7You find no rewards on this creature.";

    boolean huntUnderSeaLevel;

    public UnderSeaLevelRule()
    {
        huntUnderSeaLevel = true;
    }

    public void setHuntUnderSeaLevel(boolean huntUnderSeaLevel)
    {
        this.huntUnderSeaLevel = huntUnderSeaLevel;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = !huntUnderSeaLevel && EntityUtils.isUnderSeaLevel(event.getKiller());

        if (ruleBroken) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " killing under sea level.");
        }

        return ruleBroken;
    }

    public static Rule parseConfig(ConfigurationSection config)
    {
        UnderSeaLevelRule rule = null;

        if (config != null) {
            rule = new UnderSeaLevelRule();
            rule.setHuntUnderSeaLevel(config.getBoolean("System.Hunting.AllowUnderSeaLVL", true));
            rule.setMessage(new DefaultMessage(config.getString("System.Messages.NoUnderSeaLevel", NO_UNDER_SEA_LEVEL_MESSAGE)));
        }

        return rule;
    }
}
