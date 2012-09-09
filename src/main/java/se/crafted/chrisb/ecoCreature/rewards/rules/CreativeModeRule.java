package se.crafted.chrisb.ecoCreature.rewards.rules;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class CreativeModeRule extends DefaultRule
{
    private boolean creativeModeRewards;

    public CreativeModeRule()
    {
        creativeModeRewards = false;
    }

    public void setCreativeModeRewards(boolean creativeModeRewards)
    {
        this.creativeModeRewards = creativeModeRewards;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = !creativeModeRewards && event.getKiller().getGameMode() == GameMode.CREATIVE;

        if (ruleBroken) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " in creative mode.");
        }

        return ruleBroken;
    }

    public static Rule parseConfig(ConfigurationSection config)
    {
        CreativeModeRule rule = null;

        if (config != null) {
            rule = new CreativeModeRule();
            rule.setCreativeModeRewards(config.getBoolean("System.Hunting.CreativeModeRewards", false));
        }

        return rule;
    }
}
