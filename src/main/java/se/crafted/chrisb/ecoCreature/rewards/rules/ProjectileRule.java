package se.crafted.chrisb.ecoCreature.rewards.rules;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;
import se.crafted.chrisb.ecoCreature.messages.DefaultMessage;

public class ProjectileRule extends DefaultRule
{
    private static final String NO_BOW_REWARD_MESSAGE = "&7You find no rewards on this creature.";

    private boolean bowRewards;

    public ProjectileRule()
    {
        bowRewards = true;
    }

    public void setBowRewards(boolean bowRewards)
    {
        this.bowRewards = bowRewards;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = !bowRewards && event.isProjectileKill();

        if (ruleBroken) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " using projectiles.");
        }

        return ruleBroken;
    }

    public static Rule parseConfig(ConfigurationSection config)
    {
        ProjectileRule rule = null;

        if (config != null) {
            rule = new ProjectileRule();
            rule.setBowRewards(config.getBoolean("System.Hunting.BowRewards", true));
            rule.setMessage(new DefaultMessage(config.getString("System.Messages.NoBowMessage", NO_BOW_REWARD_MESSAGE)));
        }

        return rule;
    }
}
