package se.crafted.chrisb.ecoCreature.rewards.rules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import se.crafted.chrisb.ecoCreature.commons.DependencyUtils;
import se.crafted.chrisb.ecoCreature.commons.ECLogger;
import se.crafted.chrisb.ecoCreature.events.EntityKilledEvent;

public class MobArenaRule extends AbstractRule
{
    private boolean mobArenaRewards;

    public MobArenaRule()
    {
        mobArenaRewards = false;
    }

    public void setMobArenaRewards(boolean mobArenaRewards)
    {
        this.mobArenaRewards = mobArenaRewards;
    }

    @Override
    public boolean isBroken(EntityKilledEvent event)
    {
        boolean ruleBroken = !mobArenaRewards && DependencyUtils.hasMobArena() && DependencyUtils.getMobArenaHandler().isPlayerInArena(event.getKiller());

        if (ruleBroken) {
            ECLogger.getInstance().debug("No reward for " + event.getKiller().getName() + " in Mob Arena.");
        }

        return ruleBroken;
    }

    public static Set<Rule> parseConfig(ConfigurationSection config)
    {
        Set<Rule> rules = Collections.emptySet();

        if (config != null) {
            MobArenaRule rule = new MobArenaRule();
            rule.setMobArenaRewards(config.getBoolean("System.Hunting.MobArenaRewards"));
            rules = new HashSet<Rule>();
            rules.add(rule);
        }

        return rules;
    }
}
