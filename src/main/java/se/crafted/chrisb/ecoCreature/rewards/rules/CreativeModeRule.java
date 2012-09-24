package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class CreativeModeRule extends AbstractRule
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

    public static Set<Rule> parseConfig(ConfigurationSection config)
    {
        Set<Rule> rules = Collections.emptySet();

        if (config != null) {
            CreativeModeRule rule = new CreativeModeRule();
            rule.setCreativeModeRewards(config.getBoolean("System.Hunting.CreativeModeRewards", false));
            rules = new HashSet<Rule>();
            rules.add(rule);
        }

        return rules;
    }
}
